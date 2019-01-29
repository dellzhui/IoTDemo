package com.aliyun.alink.devicesdk.demo;

/*
 * Copyright (c) 2014-2016 Alibaba Group. All rights reserved.
 * License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.alink.dm.model.RequestModel;
import com.aliyun.alink.dm.model.ResponseModel;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttRrpcRequest;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcHandle;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectRrpcListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener;
import com.aliyun.alink.linksdk.tools.AError;

import java.util.Map;

public class COTAActivity extends BaseTemplateActivity {

    private String cOTAGet = "{" + "  \"id\": 123," + "  \"version\": \"1.0\"," +
            "  \"params\": {" + "\"configScope\": \"product\"," + "\"getType\": \"file\"" +
            "  }," + "  \"method\": \"thing.config.get\"" + "}";

    @Override
    protected void initViewData() {
        funcTV1.setText("获取远程配置");
        funcBT1.setText("获取");
        funcET1.setText(cOTAGet);

        funcTV2.setText("监听远程配置下行");
        funcBT2.setText("监听");
    }

    @Override
    protected void onFunc6Click() {

    }

    @Override
    protected void onFunc5Click() {

    }

    @Override
    protected void onFunc4Click() {

    }

    @Override
    protected void onFunc3Click() {

    }

    @Override
    protected void onFunc2Click() {
        LinkKit.getInstance().getDeviceCOTA().setCOTAChangeListener(new IConnectRrpcListener() {
            @Override
            public void onSubscribeSuccess(ARequest aRequest) {
                Log.d(TAG, "onSubscribeSuccess() called with: aRequest = [" + aRequest + "]");
                showToast("订阅成功");
            }

            @Override
            public void onSubscribeFailed(ARequest aRequest, AError aError) {
                Log.d(TAG, "onSubscribeFailed() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]");
                showToast("订阅失败");
            }

            @Override
            public void onReceived(ARequest aRequest, IConnectRrpcHandle iConnectRrpcHandle) {
                Log.d(TAG, "onReceived() called with: aRequest = [" + aRequest + "], iConnectRrpcHandle = [" + iConnectRrpcHandle + "]");
                showToast("接收到远程配置下行数据");
                if (aRequest instanceof MqttRrpcRequest){
                    // 云端下行数据 拿到
                    // ((MqttRrpcRequest) aRequest).payloadObj;
//                    ResponseModel<Map<String, String>> responseModel = JSONObject.parseObject(((MqttRrpcRequest) aRequest).payloadObj, new TypeReference<ResponseModel<Map<String, String>>>(){}.getType());


                }
                // 返回数据示例
                    /*{
                        "id": "123",
                        "version": "1.0",
                        "code": 200,
                        "data": {
                        "configId": "123dagdah",
                            "configSize": 1234565,
                            "sign": "123214adfadgadg",
                            "signMethod": "Sha256",
                            "url": "https://iotx-config.oss-cn-shanghai.aliyuncs.com/nopoll_0.4.4.tar.gz?Expires=1502955804&OSSAccessKeyId=XXXXXXXXXXXXXXXXXXXX&Signature=XfgJu7P6DWWejstKJgXJEH0qAKU%3D&security-token=CAISuQJ1q6Ft5B2yfSjIpK6MGsyN1Jx5jo6mVnfBglIPTvlvt5D50Tz2IHtIf3NpAusdsv03nWxT7v4flqFyTINVAEvYZJOPKGrGR0DzDbDasumZsJbo4f%2FMQBqEaXPS2MvVfJ%2BzLrf0ceusbFbpjzJ6xaCAGxypQ12iN%2B%2Fr6%2F5gdc9FcQSkL0B8ZrFsKxBltdUROFbIKP%2BpKWSKuGfLC1dysQcO1wEP4K%2BkkMqH8Uic3h%2Boy%2BgJt8H2PpHhd9NhXuV2WMzn2%2FdtJOiTknxR7ARasaBqhelc4zqA%2FPPlWgAKvkXba7aIoo01fV4jN5JXQfAU8KLO8tRjofHWmojNzBJAAPpYSSy3Rvr7m5efQrrybY1lLO6iZy%2BVio2VSZDxshI5Z3McKARWct06MWV9ABA2TTXXOi40BOxuq%2B3JGoABXC54TOlo7%2F1wTLTsCUqzzeIiXVOK8CfNOkfTucMGHkeYeCdFkm%2FkADhXAnrnGf5a4FbmKMQph2cKsr8y8UfWLC6IzvJsClXTnbJBMeuWIqo5zIynS1pm7gf%2F9N3hVc6%2BEeIk0xfl2tycsUpbL2FoaGk6BAF8hWSWYUXsv59d5Uk%3D",
                            "getType": "file"
                        }
                    }*/

            }

            @Override
            public void onResponseSuccess(ARequest aRequest) {
                Log.d(TAG, "onResponseSuccess() called with: aRequest = [" + aRequest + "]");
                showToast("回复远程配置成功");
            }

            @Override
            public void onResponseFailed(ARequest aRequest, AError aError) {
                Log.d(TAG, "onResponseFailed() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]");
                showToast("回复远程配置失败");
            }
        });
    }

    @Override
    protected void onFunc1Click() {
        try {
            String getData = funcET1.getText().toString();
            RequestModel<Map> requestModel = JSONObject.parseObject(getData, new TypeReference<RequestModel<Map>>() {
            }.getType());
            LinkKit.getInstance().getDeviceCOTA().COTAGet(requestModel, new IConnectSendListener() {
                @Override
                public void onResponse(ARequest aRequest, AResponse aResponse) {
                    Log.d(TAG, "onResponse() called with: aRequest = [" + aRequest + "], aResponse = [" + (aResponse == null ? null : aResponse.data) + "]");
                    showToast("获取远程配置结果成功");
                }

                @Override
                public void onFailure(ARequest aRequest, AError aError) {
                    Log.d(TAG, "onFailure() called with: aRequest = [" + aRequest + "], aError = [" + aError + "]");
                    showToast("获取远程配置失败");
                }
            });
        } catch (Exception e) {
            showToast("数据格式不对");
            e.printStackTrace();
        }
    }
}
