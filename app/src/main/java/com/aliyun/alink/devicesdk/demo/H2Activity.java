package com.aliyun.alink.devicesdk.demo;

import android.util.Log;
import android.view.View;

import com.aliyun.alink.h2.api.CompletableListener;
import com.aliyun.alink.linksdk.tools.ALog;

import h2.Constraints;


/**
 * date:    2018-10-23
 * author:  jeeking
 * description: null
 */

public class H2Activity extends BaseH2TestActivity {
    private static final String TAG = "H2Activity";

    @Override
    protected void initViewData() {
        funcTV1.setText("建联");
        funcBT1.setText("connect");
        funcET1.setText("");

        funcTV2.setText("打开流");
        funcBT2.setText("sendStream");
        funcET2.setText("");

        funcRL3.setVisibility(View.VISIBLE);
        funcTV3.setText("发送流");
        funcBT3.setText("sendStream");
        funcET3.setText("data");

        funcRL4.setVisibility(View.VISIBLE);
        funcTV4.setText("下推流");
        funcBT4.setText("downStream");
        funcET4.setText("");

        funcRL5.setVisibility(View.VISIBLE);
        funcTV5.setText("关闭流");
        funcBT5.setText("closeStream");
        funcET5.setText("");

        funcRL6.setVisibility(View.VISIBLE);
        funcTV6.setText("关闭连接");
        funcBT6.setText("disconnect");
        funcET6.setText("");
    }

    @Override
    protected void onFunc6Click() {
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
    protected void onFunc5Click() {
        try {
            closeStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFunc4Click() {
        try {
            String data = funcET4.getText().toString();
            sendStreamData(data, TYPE_DOWNSTREAM_REQUEST, data.length(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onFunc3Click() {
        try {
            String data = funcET3.getText().toString();
            sendStreamData(data, TYPE_REQUEST, data.length(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onFunc2Click() {
        ALog.d(TAG, "onFunc1Click() called openStream and Send data");
        try {
            openStream(Constraints.DEFAULT_SERVICE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            ALog.w(TAG, "openStream exception=" + e);
        }
    }

    @Override
    protected void onFunc1Click() {
        ALog.d(TAG, "onFunc1Click() called openStream and Send data");
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


    @Override
    protected void onPause() {
        super.onPause();
        try {
            closeStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            disconnect(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
