package com.aliyun.alink.devicesdk.demo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.aliyun.alink.h2.api.CompletableListener;
import com.aliyun.alink.h2.api.IAuthSign;
import com.aliyun.alink.h2.api.Profile;
import com.aliyun.alink.h2.stream.api.IStreamSender;
import com.aliyun.alink.h2.stream.api.StreamSenderFactory;
import com.aliyun.alink.linkkit.api.LinkKit;
import com.aliyun.alink.linksdk.tools.ALog;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import h2.Constraints;


public class H2FileManagerActivity extends BaseH2TestActivity {
    private static final String TAG = "H2FileManagerActivity";

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initViewData() {
        verifyStoragePermissions(this);
        funcTV1.setText("建联");
        funcBT1.setText("connect");
        funcET1.setText("");

        funcTV2.setText("打开流");
        funcBT2.setText("openStream");
        funcET2.setText("");

        funcTV3.setText("文件上传");
        funcBT3.setText("sendStream");
        funcET3.setText("/sdcard/Android/data/tes.jpg");
        funcRL3.setVisibility(View.VISIBLE);

        funcRL4.setVisibility(View.VISIBLE);
        funcTV4.setText("关闭流");
        funcBT4.setText("closeStream");
        funcET4.setText("");

        funcRL5.setVisibility(View.VISIBLE);
        funcTV5.setText("关闭连接");
        funcBT5.setText("disconnect");
        funcET5.setText("");
    }

    @Override
    protected void onFunc6Click() {

    }

    @Override
    protected void onFunc5Click() {
        try {
            disconnect(new CompletableListener() {
                @Override
                public void complete(Object o) {
                    Log.d(TAG, "complete() called with: o = [" + o + "]");
                }

                @Override
                public void completeExceptionally(Throwable throwable) {
                    Log.d(TAG, "completeExceptionally() called with: throwable = [" + throwable + "]");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFunc4Click() {
        try {
            closeStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFunc3Click() {
        try {
            String filePath = funcET3.getText().toString();
            sendFile(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFunc2Click() {
        ALog.d(TAG, "onFunc2Click() called openStream and Send data");
        try {
            openStream(Constraints.FILE_UPLOAD_SERVICE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            ALog.w(TAG, "openStream exception=" + e);
        }
    }

    @Override
    protected void onFunc1Click() {
        ALog.d(TAG, "onFunc1Click() called connect");
        try {
            connect(new CompletableListener() {
                @Override
                public void complete(Object o) {
                    Log.d(TAG, "complete() called with: o = [" + o + "]");
                }

                @Override
                public void completeExceptionally(Throwable throwable) {
                    Log.d(TAG, "completeExceptionally() called with: throwable = [" + throwable + "]");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ALog.w(TAG, "openStream exception=" + e);
        }
    }
}
