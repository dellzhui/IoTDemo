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

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.aliyun.alink.dm.api.IOta;
import com.aliyun.alink.dm.api.OtaInfo;
import com.aliyun.alink.dm.api.ResultCallback;
import com.aliyun.alink.linkkit.api.LinkKit;

import java.io.File;

public class OTAActivity extends BaseActivity {

    private IOta mOta;
    private EditText mText;
    private OtaInfo mInfo;
    private int mProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ota);

        mText = findViewById(R.id.text);

        init();
    }

    void init(){
        mOta = LinkKit.getInstance().getOta();
    }

    public void reportVersion(View view){
        String version = "0.0.1";
        if (!TextUtils.isEmpty(mText.getText())){
            version = mText.getText().toString();
        }

        log(TAG, "reportVersion:" + version);
        final String finalVersion = version;
        mOta.reportVersion(version, new ResultCallback<String>() {
            @Override
            public void onRusult(int error, String data) {
                showToast("上报版本 " + finalVersion + " " + (error == ResultCallback.SUCCESS ? "成功" : "失败"));
            }
        });
    }

    public void subscribe(View view){
        mOta.subscribeOtaInfo(new IOta.SubscribeListener() {
            @Override
            public void onSubscribeResult(int error, String errorMessage) {
                showToast("订阅OTA " + (error == ResultCallback.SUCCESS ? "成功" : "失败"));
            }

            @Override
            public void onOtaData(OtaInfo data) {
                Log.d(TAG,  " data:" + data);

                mInfo = data;
                showToast("收到OTA 下推通知，可以开始OTA 升级");
            }
        });
    }

    public void unSubscribe(View view){
        mOta.unSubscribeOtaInfo(new ResultCallback<String>() {
            @Override
            public void onRusult(int error, String data) {
                showToast("取消订阅OTA " + (error == ResultCallback.SUCCESS ? "成功" : "失败"));
            }
        });
    }

    public void startDownload(View view){
        if (null != mInfo){

            File apkDir = new File(getCacheDir(), "apk");
            apkDir.mkdirs();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                apkDir = Environment.getExternalStorageDirectory();
            }
            final String filePath = new File(apkDir, mInfo.version + ".apk").getPath();
            mOta.startDownload(filePath, mInfo, new IOta.OnDownloadListener() {
                @Override
                public void onProgress(int progress, String desc) {
                    Log.d(TAG, "onProgress. progress:" + progress + " desc:" + desc);

                    if (mProgress != progress) {
                        mProgress = progress;

                        boolean toast = mProgress % 10 == 0;
                        reportProgress(progress, "progress:" + progress, toast);

                        if (100 == progress) {
                            File apkFile = new File(filePath);

                            Intent install = new Intent(Intent.ACTION_VIEW);
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Uri contentUri = null;

                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                                apkFile.setReadable(true);
                                contentUri = Uri.fromFile(apkFile);
                            } else {
                                contentUri = FileProvider.getUriForFile(OTAActivity.this, "com.aliyun.alink.devicesdk.demo.fileprovider", apkFile);
                            }

                            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
                            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(install);
                        }
                    }
                }
            });
        }
    }

    public void stopDownload(View view){
        mOta.stopDownload();
    }

    public void reportOtaProgress(View view){
        reportProgress(mProgress, " test", true);
    }

    public void reportOtaSuccess(View view){
        if (null == mInfo) {
            showToast("没有固件信息");
            return;
        }

        String version = mInfo.version;
        log(TAG, "reportVersion:" + version);
        final String finalVersion = version;
        mOta.reportVersion(version, new ResultCallback<String>() {
            @Override
            public void onRusult(int error, String data) {
                showToast("上报版本 " + finalVersion + " " + (error == ResultCallback.SUCCESS ? "成功" : "失败"));
            }
        });
    }

    void reportProgress(final int progress, String desc, final boolean showToast){
        mOta.reportProgress(progress, desc, new ResultCallback<String>() {
            @Override
            public void onRusult(int error, String data) {
                if (showToast) {
                    showToast("上报进度 " + progress + " " + (error == ResultCallback.SUCCESS ? "成功" : "失败"));
                }
            }
        });
    }


}
