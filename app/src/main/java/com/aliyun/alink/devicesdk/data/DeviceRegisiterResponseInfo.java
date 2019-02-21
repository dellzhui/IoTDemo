package com.aliyun.alink.devicesdk.data;

import com.alibaba.fastjson.JSONObject;

public class DeviceRegisiterResponseInfo {
    public String productKey;
    public String deviceName;
    public String productSecret;
    public String deviceSecret;

    public boolean isAvailable() {
        return productKey != null && !"".equals(productKey) &&
                deviceName != null && !"".equals(deviceName) &&
                productSecret != null && !"".equals(productSecret) &&
                deviceSecret != null && !"".equals(deviceSecret);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
