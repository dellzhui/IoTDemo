package com.aliyun.alink.devicesdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.alink.devicesdk.adapter.SubDeviceListAdapter;
import com.aliyun.alink.devicesdk.app.DemoApplication;
import com.aliyun.alink.dm.api.BaseInfo;
import com.aliyun.alink.dm.api.DeviceInfo;
import com.aliyun.alink.dm.api.SignUtils;
import com.aliyun.alink.dm.model.ResponseModel;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.channel.gateway.api.subdevice.ISubDeviceActionListener;
import com.aliyun.alink.linksdk.channel.gateway.api.subdevice.ISubDeviceChannel;
import com.aliyun.alink.linksdk.channel.gateway.api.subdevice.ISubDeviceConnectListener;
import com.aliyun.alink.linksdk.channel.gateway.api.subdevice.ISubDeviceRemoveListener;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttRrpcRequest;
import com.aliyun.alink.linksdk.cmp.core.base.AMessage;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcHandle;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.alink.linksdk.tools.ALog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *
 *  Copyright (c) 2014-2016 Alibaba Group. All rights reserved.
 *  License-Identifier: Apache-2.0
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may
 *  not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


public class GatewayActivity extends BaseActivity {

    private List<DeviceInfo> subDeviceTripleInfoList = new ArrayList<>();

    private String testPublishTopic = "/sys/{productKey}/{deviceName}/thing/event/property/post";
    private String testSubscribePropertyService = "/sys/{productKey}/{deviceName}/thing/service/property/set";
    private String testSubscribeService = "/sys/{productKey}/{deviceName}/thing/service/+";
    private String testSubscribeSyncService = "/sys/{productKey}/{deviceName}/rrpc/request/+";
    private String[] subscribeServiceList = {testSubscribePropertyService, testSubscribeService, testSubscribeSyncService};


    private Spinner subDeviceListSpinner = null;
    private DeviceInfo selectedSubdeviceInfo = null;
    private SubDeviceListAdapter subDeviceListAdapter = null;
    private EditText publishPayloadET = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        initViews();
    }

    private void initViews() {
//        publishPayloadET = findViewById(R.id.id_publish_payload);
        subDeviceListSpinner = findViewById(R.id.id_sub_dev_list);
        subDeviceListAdapter = new SubDeviceListAdapter(this);
        subDeviceListSpinner.setAdapter(subDeviceListAdapter);
        subDeviceListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected() called with: parent = [" + parent + "], view = [" + view + "], position = [" + position + "], id = [" + id + "]");
                if (subDeviceListAdapter == null) {
                    return;
                }
                if (subDeviceListAdapter.getCount() < 1 || position < 0 || position > subDeviceListAdapter.getCount() - 1) {
                    Log.w(TAG, "position invalid, position=" + position + ",count=" + subDeviceListAdapter.getCount());
                    return;
                }
                selectedSubdeviceInfo = (DeviceInfo) subDeviceListAdapter.getItem(position);
                if (selectedSubdeviceInfo == null) {
                    Log.w(TAG, "selected device info is null.");
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected() called with: parent = [" + parent + "]");

            }
        });
    }


    private void updateSpinnerList(final List<DeviceInfo> infoList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (subDeviceListAdapter != null) {
                    subDeviceListAdapter.setListData(infoList);
                    subDeviceListAdapter.notifyDataSetChanged();
                    if (selectedSubdeviceInfo == null && infoList.size() > 0) {
                        selectedSubdeviceInfo = (DeviceInfo) subDeviceListAdapter.getItem(0);
                    }
                }
            }
        });
    }

    /**
     * 子设备动态注册
     *
     * @param view 云端安全策略问题  需要先在云端创建 子设备
     */
    public void subDevRegister(View view) {

        LinkKit.getInstance().getGateway().gatewaySubDevicRegister(getSubDevList(), new IConnectSendListener() {
            @Override
            public void onResponse(ARequest aRequest, AResponse aResponse) {
                ALog.d(TAG, "onResponse() called with: aRequest = [" + aRequest + "], aResponse = [" + (aResponse == null ? "null" : aResponse.data) + "]");
                try {
                    showToast("子设备动态注册成功");
                    ResponseModel<List<DeviceInfo>> response = JSONObject.parseObject(aResponse.data.toString(), new TypeReference<ResponseModel<List<DeviceInfo>>>() {
                    }.getType());
                    //TODO 保存子设备的三元组信息
                    // for test
                    selectedSubdeviceInfo = null;
                    updateSpinnerList(response.data);
                    log(TAG, "子设备动态注册成功 " + response.data.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ARequest aRequest, AError aError) {
                ALog.d(TAG, "onFailure() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]");
                showToast("子设备动态注册失败");
                log(TAG, "子设备动态注册失败");
            }
        });
    }


    /**
     * 获取当前网关的子设备列表
     * 需要先添加子设备到网关
     *
     * @param view
     */
    public void getSubDevices(View view) {
        LinkKit.getInstance().getGateway().gatewayGetSubDevices(new IConnectSendListener() {
            @Override
            public void onResponse(ARequest aRequest, AResponse aResponse) {
                ALog.d(TAG, "onResponse() called with: aRequest = [" + aRequest + "], aResponse = [" + (aResponse == null ? "null" : aResponse.data) + "]");
                showToast("获取子设备列表成功");
                log(TAG, "获取子设备列表成功 aResponse=" + (aResponse == null ? "null" : aResponse.data));
                try {
                    ResponseModel<List<DeviceInfo>> response = JSONObject.parseObject(aResponse.data.toString(), new TypeReference<ResponseModel<List<DeviceInfo>>>() {
                    }.getType());

                    subDeviceTripleInfoList.addAll(response.data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(ARequest aRequest, AError aError) {
                ALog.d(TAG, "onFailure() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]");
                showToast("获取子设备列表失败");
                log(TAG, "获取子设备列表失败");
            }
        });
    }

    /**
     * 添加子设备到网关
     * 子设备动态注册之后　可以拿到子设备的 deviceSecret 信息，签名的时候需要使用到
     * 签名方式 sign = hmac_md5(deviceSecret, clientId123deviceNametestproductKey123timestamp1524448722000)
     *
     * @param view
     */
    public void addSubDevice(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，添加失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        LinkKit.getInstance().getGateway().gatewayAddSubDevice(info, new ISubDeviceConnectListener() {
            @Override
            public String getSignMethod() {
                ALog.d(TAG, "getSignMethod() called");
                return "hmacsha1";
            }

            @Override
            public String getSignValue() {
                ALog.d(TAG, "getSignValue() called");
                Map<String, String> signMap = new HashMap<>();
                signMap.put("productKey", info.productKey);
                signMap.put("deviceName", info.deviceName);
//                signMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
                signMap.put("clientId", getClientId());
                return SignUtils.hmacSign(signMap, info.deviceSecret);
            }

            @Override
            public String getClientId() {
                ALog.d(TAG, "getClientId() called");
                return "id";
            }

            @Override
            public void onConnectResult(boolean isSuccess, ISubDeviceChannel iSubDeviceChannel, AError aError) {
                ALog.d(TAG, "onConnectResult() called with: isSuccess = [" + isSuccess + "], iSubDeviceChannel = [" + iSubDeviceChannel + "], aError = [" + aError + "]");
                if (isSuccess) {
                    showToast("子设备添加成功");
                    log(TAG, "子设备添加成功 " + getPkDn(info));
                    subDevOnline(null);
                }
            }

            @Override
            public void onDataPush(String s, AMessage message) {
                // new String((byte[]) message.getData())
                // {"method":"thing.service.property.set","id":"184220091","params":{"test":2},"version":"1.0.0"} 示例
                ALog.d(TAG, "收到子设备下行数据  onDataPush() called with: s = [" + s + "], s1 = [" + message + "]");
            }
        });
    }

    /**
     * 删除子设备
     *
     * @param view
     */
    private void deleteSubDevice(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，删除失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        LinkKit.getInstance().getGateway().gatewayDeleteSubDevice(info, new ISubDeviceRemoveListener() {
            @Override
            public void onSuceess() {
                ALog.d(TAG, "onSuceess() called");
                showToast("成功删除子设备 ");
                log(TAG, "成功删除子设备" + getPkDn(info));
            }

            @Override
            public void onFailed(AError aError) {
                ALog.d(TAG, "onFailed() called with: aError = [" + aError + "]");
                showToast("删除子设备失败");
                log(TAG, "删除子设备失败" + getPkDn(info));
            }
        });
    }

    /**
     * 网关添加子设备之后才能代理子设备上线
     *
     * @param view
     */
    private void subDevOnline(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，上线失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        LinkKit.getInstance().getGateway().gatewaySubDeviceLogin(info, new ISubDeviceActionListener() {
            @Override
            public void onSuccess() {
                ALog.d(TAG, "onSuccess() called");
                showToast("代理子设备上线成功");
                log(TAG, "代理子设备上线成功" + getPkDn(info));
                subDevDisable(null);
                subDevDelete(null);
            }

            @Override
            public void onFailed(AError aError) {
                ALog.d(TAG, "onFailed() called with: aError = [" + aError + "]");
                showToast("代理子设备上线失败");
                log(TAG, "代理子设备上线失败" + getPkDn(info));
            }
        });
    }

    /**
     * 网关添加子设备之后才能代理子设备下线
     *
     * @param view
     */
    public void subDevOffline(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，下线失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        LinkKit.getInstance().getGateway().gatewaySubDeviceLogout(info, new ISubDeviceActionListener() {
            @Override
            public void onSuccess() {
                ALog.d(TAG, "onSuccess() called");
                showToast("代理子设备下线成功");
                log(TAG, "代理子设备下线成功" + getPkDn(info));
                deleteSubDevice(null);
            }

            @Override
            public void onFailed(AError aError) {
                ALog.d(TAG, "onFailed() called with: aError = [" + aError + "]");
                showToast("代理子设备下线失败");
                log(TAG, "代理子设备下线失败" + getPkDn(info));
            }
        });
    }

    /**
     * 代理子设备订阅
     *
     * @param view
     */
    public void subDevSubscribe(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，代理子设备订阅失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        String topic = null;
//        for (int i = 0; i < subscribeServiceList.length; i++) {
            topic = subscribeServiceList[0];

            final String tempTopic = topic.replace("{deviceName}", selectedSubdeviceInfo.deviceName)
                    .replace("{productKey}", selectedSubdeviceInfo.productKey);

            LinkKit.getInstance().getGateway().gatewaySubDeviceSubscribe(tempTopic, info, new ISubDeviceActionListener() {
                @Override
                public void onSuccess() {
                    ALog.d(TAG, "onSuccess() called");
                    showToast("代理子设备订阅成功");
                    log(TAG, "代理子设备订阅成功" + getPkDn(info) + tempTopic);
                }

                @Override
                public void onFailed(AError aError) {
                    ALog.d(TAG, "onFailed() called with: aError = [" + aError + "]");
                    showToast("代理子设备订阅失败");
                    log(TAG, "代理子设备订阅失败" + getPkDn(info) + tempTopic);
                }
            });
//        }
    }

    /**
     * 代理子设备发布
     *
     * @param view
     */
    public void subDevPublish(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，代理子设备发布失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        String topic = testPublishTopic.replace("{deviceName}", selectedSubdeviceInfo.deviceName)
                .replace("{productKey}", selectedSubdeviceInfo.productKey);
        String data = publishPayloadET.getText().toString();
        LinkKit.getInstance().getGateway().gatewaySubDevicePublish(topic, data, info, new ISubDeviceActionListener() {
            @Override
            public void onSuccess() {
                ALog.d(TAG, "onSuccess() called");
                showToast("代理子设备发布成功");
                log(TAG, "代理子设备发布成功" + getPkDn(info));
            }

            @Override
            public void onFailed(AError aError) {
                ALog.d(TAG, "onFailed() called with: aError = [" + aError + "]");
                showToast("代理子设备发布失败");
                log(TAG, "代理子设备发布失败" + getPkDn(info));
            }
        });
    }

    /**
     * 代理子设备取消订阅
     *
     * @param view
     */
    public void subDevUnsubscribe(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，代理子设备取消订阅失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        String topic = testSubscribePropertyService.replace("{deviceName}", selectedSubdeviceInfo.deviceName)
                .replace("{productKey}", selectedSubdeviceInfo.productKey);
        LinkKit.getInstance().getGateway().gatewaySubDeviceUnsubscribe(topic, info, new ISubDeviceActionListener() {
            @Override
            public void onSuccess() {
                ALog.d(TAG, "onSuccess() called");
                showToast("代理子设备取消订阅成功");
                log(TAG, "代理子设备取消订阅成功" + getPkDn(info));
            }

            @Override
            public void onFailed(AError aError) {
                ALog.d(TAG, "onFailed() called with: aError = [" + aError + "]");
                showToast("代理子设备取消订阅事变");
                log(TAG, "代理子设备取消订阅失败" + getPkDn(info));
            }
        });
    }


    private List<BaseInfo> getSubDevList() {
        return DemoApplication.mDeviceInfoData.subDevice;
    }

    private void subDevDisable(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，注册禁用监听失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        LinkKit.getInstance().getGateway().gatewaySetSubDeviceDisableListener(info, new IConnectRrpcListener() {
            @Override
            public void onSubscribeSuccess(ARequest aRequest) {
                log(TAG, "订阅禁用下行成功");
            }

            @Override
            public void onSubscribeFailed(ARequest aRequest, AError aError) {
                log(TAG, "订阅禁用下行失败 " + (aError == null ? "" : (aError.getCode() + aError.getMsg())));
            }

            @Override
            public void onReceived(ARequest aRequest, IConnectRrpcHandle iConnectRrpcHandle) {
                Log.d(TAG, "onReceived() called with: aRequest = [" + aRequest + "], iConnectRrpcHandle = [" + iConnectRrpcHandle + "]");
                showToast("子设备禁用");
                log(TAG, "子设备禁用通知" + getPkDn(info));
                AResponse response = new AResponse();
                // 回复示例
                response.data = "{\"id\":\"123\", \"code\":\"200\"" + ",\"data\":{} }";
                //TODO
                if (aRequest instanceof MqttRrpcRequest) {
                    String receivedData = new String((byte[]) ((MqttRrpcRequest) aRequest).payloadObj);
                    //{"method":"thing.disable","id":"123643484","params":{},"version":"1.0.0"}  参考数据
                    // TODO 数据解析处理
                    iConnectRrpcHandle.onRrpcResponse(((MqttRrpcRequest) aRequest).replyTopic, response);
                }
            }

            @Override
            public void onResponseSuccess(ARequest aRequest) {
                Log.d(TAG, "onResponseSuccess() called with: aRequest = [" + aRequest + "]");
            }

            @Override
            public void onResponseFailed(ARequest aRequest, AError aError) {
                Log.d(TAG, "onResponseFailed() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]");
            }
        });
    }
//

    /**
     * 解禁下行通知目前不支持
     *
     * @param view
     */
//    public void subDevEnable(View view) {
//        if (subDeviceTripleInfoList == null || subDeviceTripleInfoList.size() < 1){
//            showToast("无有效设备，请先确保有添加子设备列表并动态注册设备");
//            return;
//        }
//        final DeviceInfo info = selectedSubdeviceInfo;
//        if (info == null) {
//            showToast("尚未添加该子设备.");
//            log(TAG, "尚未添加该子设备.");
//            return;
//        }
//        LinkKit.getInstance().getGateway().gatewaySetSubDeviceEnableListener(info, new IConnectRrpcListener() {
//            @Override
//            public void onSubscribeSuccess(ARequest aRequest) {
//                log(TAG, "订阅成功");
//            }
//
//            @Override
//            public void onSubscribeFailed(ARequest aRequest, AError aError) {
//                log(TAG, "订阅失败");
//            }
//
//            @Override
//            public void onReceived(ARequest aRequest, IConnectRrpcHandle iConnectRrpcHandle) {
//                Log.d(TAG, "onReceived() called with: aRequest = [" + aRequest + "], iConnectRrpcHandle = [" + iConnectRrpcHandle + "]");
//                showToast("子设备启用");
//                log(TAG, "子设备启用禁用" + getPkDn(info));
//                iConnectRrpcHandle.onRrpcResponse(null, null);
//            }
//
//            @Override
//            public void onResponseSuccess(ARequest aRequest) {
//                Log.d(TAG, "onResponseSuccess() called with: aRequest = [" + aRequest + "]");
//            }
//
//            @Override
//            public void onResponseFailed(ARequest aRequest, AError aError) {
//                Log.d(TAG, "onResponseFailed() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]");
//            }
//        });
//    }
    private void subDevDelete(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，订阅设备删除通知失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        LinkKit.getInstance().getGateway().gatewaySetSubDeviceDeleteListener(info, new IConnectRrpcListener() {
            @Override
            public void onSubscribeSuccess(ARequest aRequest) {
                log(TAG, "订阅子设备删除成功");
            }

            @Override
            public void onSubscribeFailed(ARequest aRequest, AError aError) {
                log(TAG, "订阅子设备删除失败" + (aError == null ? "" : (aError.getCode() + aError.getMsg())));
            }

            @Override
            public void onReceived(ARequest aRequest, IConnectRrpcHandle iConnectRrpcHandle) {
                Log.d(TAG, "onReceived() called with: aRequest = [" + aRequest + "], iConnectRrpcHandle = [" + iConnectRrpcHandle + "]");
                showToast("子设备删除");
                log(TAG, "子设备删除通知" + getPkDn(info));
                iConnectRrpcHandle.onRrpcResponse(null, null);
            }

            @Override
            public void onResponseSuccess(ARequest aRequest) {
                Log.d(TAG, "onResponseSuccess() called with: aRequest = [" + aRequest + "]");
            }

            @Override
            public void onResponseFailed(ARequest aRequest, AError aError) {
                Log.d(TAG, "onResponseFailed() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]");
            }
        });
    }

    private String getPkDn(DeviceInfo info) {
        if (info == null) {
            return null;
        }
        return "[pk=" + info.productKey + ",dn=" + info.deviceName + "]";
    }

    public void startSubDeviceControl(View view) {
        if (selectedSubdeviceInfo == null || TextUtils.isEmpty(selectedSubdeviceInfo.productKey) ||
                TextUtils.isEmpty(selectedSubdeviceInfo.deviceName)) {
            showToast("无有效已动态注册的设备，订阅设备删除通知失败");
            return;
        }
        final DeviceInfo info = selectedSubdeviceInfo;
        Intent intent = new Intent(this, SubDeviceControlPannelActivity.class);
        intent.putExtra("pk", info.productKey);
        intent.putExtra("dn", info.deviceName);
        startActivity(intent);
    }
}
