package com.aliyun.alink.devicesdk.manager;

import android.util.Log;

import com.aliyun.alink.devicesdk.data.DeviceRegisiterRequestInfo;
import com.aliyun.alink.devicesdk.data.DeviceRegisiterResponseInfo;
import com.google.gson.Gson;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class DeviceRegisiterUtil {
    private static final String TAG = "DeviceRegisiterUtil";

    public static DeviceRegisiterResponseInfo getDeviceRegisiterResponseInfo() {
        try {
            DeviceRegisiterResponseInfo deviceRegisiterResponseInfo = getDeviceRegisiterResponseInfoFromProperity();
            return deviceRegisiterResponseInfo != null ? deviceRegisiterResponseInfo : getDeviceRegisiterResponseInfoFromRequest();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DeviceRegisiterResponseInfo getDeviceRegisiterResponseInfoFromProperity() {
        DeviceRegisiterResponseInfo deviceRegisiterResponseInfo = new DeviceRegisiterResponseInfo();
        deviceRegisiterResponseInfo.productKey = PropertyUtils.get("persist.sys.iot.productKey", "");
        deviceRegisiterResponseInfo.deviceName = PropertyUtils.get("persist.sys.iot.productKey", "");
        deviceRegisiterResponseInfo.productSecret = PropertyUtils.get("persist.sys.iot.productKey", "");
        deviceRegisiterResponseInfo.deviceSecret = PropertyUtils.get("persist.sys.iot.productKey", "");
        Log.d(TAG, "get deviceRegisiterResponseInfo is " + deviceRegisiterResponseInfo);
        return deviceRegisiterResponseInfo.isAvailable() ? deviceRegisiterResponseInfo : null;
    }

    private static void setDeviceRegisiterResponseInfoFromProperity(DeviceRegisiterResponseInfo deviceRegisiterResponseInfo) {
        PropertyUtils.set("persist.sys.iot.productKey", deviceRegisiterResponseInfo.productKey);
        PropertyUtils.set("persist.sys.iot.deviceName", deviceRegisiterResponseInfo.deviceName);
        PropertyUtils.set("persist.sys.iot.productSecret", deviceRegisiterResponseInfo.productSecret);
        PropertyUtils.set("persist.sys.iot.deviceSecret", deviceRegisiterResponseInfo.deviceSecret);
    }

    private static DeviceRegisiterResponseInfo getDeviceRegisiterResponseInfoFromRequest() {
        try {
            String url = "http://192.168.52.107:8000/regisiter/";
            RequestTask requestTask = new RequestTask(url);
            Thread t = new Thread(requestTask);
            Log.d(TAG, "begin to start regisiter request task");
            t.start();
            t.join(10000);
            DeviceRegisiterResponseInfo deviceRegisiterResponseInfo = requestTask.get();
            setDeviceRegisiterResponseInfoFromProperity(deviceRegisiterResponseInfo);
            return deviceRegisiterResponseInfo;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class RequestTask implements Runnable {
        private String mUrl;
        private DeviceRegisiterResponseInfo mDeviceRegisiterResponseInfo;

        RequestTask(String url) {
            this.mUrl = url;
        }

        public void run() {
            DeviceRegisiterRequestInfo deviceRegisiterRequestInfo = new DeviceRegisiterRequestInfo();
            Log.d(TAG, "regeister request info is " + deviceRegisiterRequestInfo);
            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("ProductName", deviceRegisiterRequestInfo.ProductName)
                    .add("CardNumber", deviceRegisiterRequestInfo.CardNumber)
                    .add("MacAddress", deviceRegisiterRequestInfo.MacAddress)
                    .add("SerialNumber", deviceRegisiterRequestInfo.SerialNumber)
                    .build();
            Request request = new Request.Builder().url(mUrl).post(requestBody).build();
            try {
                okhttp3.Response response = client.newCall(request).execute();
                Log.d(TAG, "response code is " + response.code());
                if (response.isSuccessful()) {
                    String json_result = response.body().string();
                    Log.d(TAG, "got regisiter response json result was [" + json_result + "]");
                    Gson gson = new Gson();
                    set(gson.fromJson(json_result, DeviceRegisiterResponseInfo.class));
                }
//                Call call = client.newCall(request);
//                call.enqueue(new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        try {
//                            Log.d(TAG, "response code is " + response.code());
//                            if (response.isSuccessful()) {
//                                String json_result = response.body().string();
//                                Log.d(TAG, "got regisiter response json result was [" + json_result + "]");
//                                Gson gson = new Gson();
//                                set(gson.fromJson(json_result, DeviceRegisiterResponseInfo.class));
//                            }
//                        } catch(Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        private synchronized void set(DeviceRegisiterResponseInfo deviceRegisiterResponseInfo) {
            mDeviceRegisiterResponseInfo = deviceRegisiterResponseInfo;
        }

        synchronized DeviceRegisiterResponseInfo get() {
            Log.d(TAG, "got regisiter response info was [" + mDeviceRegisiterResponseInfo + "]");
            return mDeviceRegisiterResponseInfo;
        }
    }
}
