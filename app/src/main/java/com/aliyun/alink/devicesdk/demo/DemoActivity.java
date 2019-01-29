package com.aliyun.alink.devicesdk.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.alink.devicesdk.app.DemoApplication;
import com.aliyun.alink.devicesdk.manager.IDemoCallback;
import com.aliyun.alink.devicesdk.manager.InitManager;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.alink.linksdk.tools.ALog;


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

public class DemoActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "DemoActivity";

    private TextView errorTV = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ALog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        errorTV = findViewById(R.id.id_error_info);
        setListener();
    }

    private void setListener() {
        try {
            LinearLayout demoLayout = findViewById(R.id.id_demo_layout);
            int size = demoLayout.getChildCount();
            for (int i = 0; i < size; i++) {
                if (i == size - 1) {
                    break;
                }
                View child = demoLayout.getChildAt(i);
                child.setOnClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ALog.w(TAG, "setListener exception " + e);
        }
    }

    public void startOTATest(View view) {
        if (!checkReady()) {
            return;
        }

        Intent intent = new Intent(this, OTAActivity.class);
        startActivity(intent);
    }

    public void startLPTest(View view) {
        if (!checkReady()) {
            return;
        }
        Intent intent = new Intent(this, ControlPannelActivity.class);
        startActivity(intent);
    }

    public void startLabelTest(View view) {
        if (!checkReady()) {
            return;
        }
        Intent intent = new Intent(this, LabelActivity.class);
        startActivity(intent);
    }

    public void startCOTATest(View view) {
        if (!checkReady()) {
            return;
        }
        Intent intent = new Intent(this, COTAActivity.class);
        startActivity(intent);
    }

    public void startShadowTest(View view) {
        if (!checkReady()) {
            return;
        }
        showToast("基础版设备影子功能，高级版不适用，需要设备的三元组是基础版才可以测试使用");
        Intent intent = new Intent(this, ShadowActivity.class);
        startActivity(intent);
    }

    public void startGatewayTest(View view) {
        if (!checkReady()) {
            return;
        }
        Intent intent = new Intent(this, GatewayActivity.class);
        startActivity(intent);
    }

    private boolean checkReady() {
        if (DemoApplication.userDevInfoError) {
            showToast("设备三元组信息res/raw/deviceinfo格式错误");
            errorTV.setText("设备三元组信息res/raw/deviceinfo格式错误");
            errorTV.setVisibility(View.VISIBLE);
            return false;
        }
        if (!DemoApplication.isInitDone) {
            showToast("初始化尚未成功，请稍后点击");
            return false;
        }
        errorTV.setVisibility(View.GONE);
        return true;
    }

    public void startH2Test(View view) {
        if (!checkReady()) {
            return;
        }
        Intent intent = new Intent(this, H2Activity.class);
        startActivity(intent);
    }

    public void startH2FileTest(View view) {
        if (!checkReady()) {
            return;
        }
        Intent intent = new Intent(this, H2FileManagerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_start_LP:
                startLPTest(v);
                break;
            case R.id.id_start_label:
                startLabelTest(v);
                break;
            case R.id.id_start_cota:
                startCOTATest(v);
                break;
            case R.id.id_start_shadow:
                startShadowTest(v);
                break;
            case R.id.id_start_gateway:
                startGatewayTest(v);
                break;
            case R.id.id_start_ota:
                startOTATest(v);
                break;
            case R.id.id_start_h2:
                startH2Test(v);
                break;
            case R.id.id_start_h2_file:
                startH2FileTest(v);
                break;
            case R.id.id_test_init:
                connect();
                break;
            case R.id.id_test_deinit:
                deinit();
                break;

        }
    }


    private void connect() {
        Log.d(TAG, "connect() called");
        // SDK初始化
        InitManager.init(this, DemoApplication.productKey, DemoApplication.deviceName,
                DemoApplication.deviceSecret, DemoApplication.productSecret, new IDemoCallback() {

                    @Override
                    public void onError(AError aError) {
                        Log.d(TAG, "onError() called with: aError = [" + aError + "]");
                        // 初始化失败，初始化失败之后需要用户负责重新初始化
                        // 如一开始网络不通导致初始化失败，后续网络回复之后需要重新初始化
                        showToast("初始化失败");
                    }

                    @Override
                    public void onInitDone(Object data) {
                        Log.d(TAG, "onInitDone() called with: data = [" + data + "]");
                        showToast("初始化成功");
                    }
                });
    }

    private void deinit() {
        ALog.d(TAG, "deinit");
        LinkKit.getInstance().deinit();
    }
}
