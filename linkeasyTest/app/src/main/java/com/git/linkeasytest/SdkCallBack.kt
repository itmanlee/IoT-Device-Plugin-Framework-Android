package com.git.linkeasytest

import android.util.Log
import com.git.linkeasycommon.bean.BatteryManagement
import com.git.linkeasycommon.bean.DeviceInfo
import com.git.linkeasycommon.utils.JsonConfig
import com.git.linkeasyfeatureadddevice.AddDeviceHostCallback
import com.git.linkeasyfeaturepreview.PreviewHostCallback
import com.git.linkeasyrtm.RtmHostCallback
import com.google.gson.Gson

class SdkCallBack() : AddDeviceHostCallback, RtmHostCallback, PreviewHostCallback {
    val list = mutableListOf<DeviceInfo>()

    init {
        val string =
            "{\"id\":20993,\"sn\":\"03a3000200a96889de71\",\"type\":13,\"model\":\"\",\"region\":\"cn-north-1\",\"vendor\":\"HDT-NEUTRAL\",\"chNum\":1,\"created\":1789442342,\"updated\":1789442342307,\"firmwareVersion\":\"V4.3\",\"firmwareDate\":20250811,\"p2pVersion\":\"V1.0.72.2026091518\",\"netInfo\":{\"type\":0,\"ethInfo\":{\"ip\":\"192.168.100.78\",\"netMask\":\"255.255.255.0\",\"gateway\":\"192.168.100.1\"}},\"otherInfo\":{\"disk\":1,\"status\":1,\"pushLive\":1,\"pushPlayback\":1,\"oneArming\":0,\"upgrade\":0},\"iotPlatformId\":1,\"ipAddress\":{\"ip\":\"117.152.146.180\",\"country\":\"China\",\"countryCode\":\"CN\",\"city\":\"Wuhan\",\"cityCode\":\"HB\"},\"state\":1,\"time\":1791049357,\"did\":10910,\"uid\":10296,\"isAdmin\":2,\"aliasName\":\"\",\"nickname\":\"\",\"stateTime\":1791049357,\"wakeupState\":2,\"wakeupStateTime\":0,\"adminUid\":10048,\"adminUser\":{\"id\":10048,\"openId\":\"86e846566fb2685bc8ddbc6bf6f6ff8b\",\"account\":\"15827757734\",\"picture\":\"http://bucket-data.yeelinkvision.com/account-images/1778746878183_mfp5yl.jpg\",\"sex\":0,\"nickName\":\"15827757734\"},\"rule\":{\"timeSpan\":[0,2359],\"week\":127,\"power\":-1,\"channel\":[\"CH01\"]}}"
        val device = Gson().fromJson<DeviceInfo>(string, DeviceInfo::class.java)
        list.add(device)
    }

    override fun isDeviceAlreadyAdded(sn: String): Boolean = false

    override fun isSerialNumberValid(sn: String): Boolean = true

    override fun addDeviceSuccess(sn: String) {
        Log.i("AddDevice", "host received added sn=$sn")
    }

    override fun addDeviceSuccessForShare() {
        Log.i("AddDevice", "addDeviceSuccessForShare")
    }

    override fun getDeviceInfo(sn: String): DeviceInfo? {
        return list.firstOrNull {
            it.sn.equals(sn)
        }
    }

    override fun getDataFrontOrBackgroundDesk(): Boolean {
       return true
    }

    override fun getDeviceList(): List<DeviceInfo>? {
       return list.toList()
    }

    override fun updataOnlineUsersAovPowerInfo(sn: String, mOnlineUsers: Int, mBatteryManagement: BatteryManagement) {
        TODO("Not yet implemented")
    }

    override fun getUserId(): Int {
        return 10296
    }

    override fun getUserToken(): String {
       return "9f1012e59c08df66b471cb4080c6fd01"
    }
}