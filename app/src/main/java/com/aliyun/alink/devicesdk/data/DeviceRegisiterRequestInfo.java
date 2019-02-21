package com.aliyun.alink.devicesdk.data;

import com.alibaba.fastjson.JSONObject;

public class DeviceRegisiterRequestInfo {
    public String ProductName;
    public String CardNumber;
    public String MacAddress;
    public String SerialNumber;

    public DeviceRegisiterRequestInfo() {
        // TODO:
        this.ProductName = "72604";
        this.CardNumber = "8100103909543252";
        this.MacAddress = "bc:20:ba:00:00:03";
        this.SerialNumber = "LC8100103909543252";
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
