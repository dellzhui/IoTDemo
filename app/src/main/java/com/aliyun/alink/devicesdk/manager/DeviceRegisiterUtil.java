package com.aliyun.alink.devicesdk.manager;

import android.util.Log;

import com.aliyun.alink.devicesdk.app.DeviceInfoData;
import com.aliyun.alink.devicesdk.data.DeviceRegisiterRequestInfo;
import com.aliyun.alink.dm.api.BaseInfo;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class DeviceRegisiterUtil {
    private static final String TAG = "DeviceRegisiterUtil";

    public static DeviceInfoData getDeviceInfoData() {
        try {
            DeviceInfoData deviceInfoData = getDeviceInfoDataFromProperity();
            return deviceInfoData != null ? deviceInfoData : getDeviceInfoDataFromRequest();
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static DeviceInfoData getDeviceInfoDataFromProperity() {
        DeviceInfoData deviceInfoData = new DeviceInfoData();
        deviceInfoData.productKey = PropertyUtils.get("persist.sys.iot.productKey", "");
        deviceInfoData.deviceName = PropertyUtils.get("persist.sys.iot.deviceName", "");
        deviceInfoData.productSecret = PropertyUtils.get("persist.sys.iot.productSecret", "");
        deviceInfoData.deviceSecret = PropertyUtils.get("persist.sys.iot.deviceSecret", "");
        Log.d(TAG, "get deviceInfoData from property is " + deviceInfoData);
        return deviceInfoData.checkValid() ? deviceInfoData : null;
    }

    private static void setDeviceInfoDataFromProperity(DeviceInfoData deviceInfoData) {
        PropertyUtils.set("persist.sys.iot.productKey", deviceInfoData.productKey);
        PropertyUtils.set("persist.sys.iot.deviceName", deviceInfoData.deviceName);
        PropertyUtils.set("persist.sys.iot.productSecret", deviceInfoData.productSecret);
        PropertyUtils.set("persist.sys.iot.deviceSecret", deviceInfoData.deviceSecret);
    }

    private static DeviceInfoData getDeviceInfoDataFromRequest() {
        try {
            String url = "http://192.168.52.107:8000/regisiter/";
            RequestTask requestTask = new RequestTask(url);
            Thread t = new Thread(requestTask);
            Log.d(TAG, "begin to start regisiter request task");
            t.start();
            t.join(10000);
            DeviceInfoData deviceInfoData = requestTask.get();
            if(deviceInfoData.checkValid()) {
                setDeviceInfoDataFromProperity(deviceInfoData);
                return deviceInfoData;
            }
            return null;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class RequestTask implements Runnable {
        private String mUrl;
        private DeviceInfoData mDeviceInfoData;

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
                    set(gson.fromJson(json_result, DeviceInfoData.class));
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
//                                set(gson.fromJson(json_result, DeviceInfoData.class));
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

        private synchronized void set(DeviceInfoData deviceInfoData) {
            mDeviceInfoData = deviceInfoData;
        }

        synchronized DeviceInfoData get() {
            Log.d(TAG, "got regisiter response info was [" + mDeviceInfoData + "]");
            return mDeviceInfoData;
        }
    }
}
