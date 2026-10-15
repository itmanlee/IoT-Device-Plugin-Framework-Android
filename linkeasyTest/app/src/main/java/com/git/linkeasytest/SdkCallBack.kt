package com.git.linkeasytest

import android.util.Log
import com.git.linkeasycommon.bean.BatteryManagement
import com.git.linkeasycommon.bean.DeviceInfo
import com.git.linkeasycommon.utils.JsonConfig
import com.git.linkeasyfeatureadddevice.AddDeviceHostCallback
import com.git.linkeasyfeaturealarm.AlarmHostCallBack
import com.git.linkeasyfeaturepreview.PreviewHostCallback
import com.git.linkeasyfeaturesharedevice.ShareDeviceHostCallBack
import com.git.linkeasyrtm.RtmHostCallback
import com.google.gson.Gson

class SdkCallBack() : AddDeviceHostCallback, RtmHostCallback, PreviewHostCallback, ShareDeviceHostCallBack, AlarmHostCallBack {
    val list = mutableListOf<DeviceInfo>()

    init {
        val string1 =
            "{\"id\":22297,\"sn\":\"03a7000200d83e51deef\",\"type\":13,\"model\":\"\",\"region\":\"cn-north-1\",\"vendor\":\"HDT-NEUTRAL\",\"chNum\":1,\"created\":1792046379,\"updated\":1792046379150,\"firmwareVersion\":\"V4.3\",\"firmwareDate\":20260303,\"p2pVersion\":\"V1.0.72.2026091518\",\"netInfo\":{\"type\":0,\"ethInfo\":{\"ip\":\"10.0.0.221\",\"netMask\":\"255.0.0.0\",\"gateway\":\"10.0.0.1\"}},\"otherInfo\":{\"disk\":0,\"status\":-1,\"pushLive\":1,\"pushPlayback\":1,\"oneArming\":1,\"upgrade\":0},\"iotPlatformId\":1,\"ipAddress\":{\"ip\":\"59.175.8.170\",\"country\":\"China\",\"countryCode\":\"CN\",\"city\":\"\",\"cityCode\":\"\"},\"state\":1,\"time\":1791995963,\"did\":20829,\"uid\":10296,\"isAdmin\":2,\"aliasName\":\"\",\"nickname\":\"\",\"stateTime\":1791995963,\"wakeupState\":2,\"wakeupStateTime\":0,\"adminUid\":10048,\"adminUser\":{\"id\":10048,\"openId\":\"86e846566fb2685bc8ddbc6bf6f6ff8b\",\"account\":\"15827757734\",\"picture\":\"http://bucket-data.yeelinkvision.com/account-images/1778746878183_mfp5yl.jpg\",\"sex\":0,\"nickName\":\"15827757734\"},\"rule\":{\"timeSpan\":[0,2359],\"week\":127,\"power\":-1,\"channel\":[\"CH01\"]}}"
        val string =
            "{\"id\":11323,\"sn\":\"03eb0002003955462fbe\",\"type\":21,\"model\":\"\",\"region\":\"cn-north-1\",\"vendor\":\"HDT-NEUTRAL\",\"chNum\":64,\"created\":1752132969,\"updated\":1752132984968,\"firmwareVersion\":\"V2.3.0\",\"firmwareDate\":20250512,\"p2pVersion\":\"V1.0.69.2026011118\",\"netInfo\":{\"type\":0,\"ethInfo\":{\"ip\":\"10.1.0.143\",\"netMask\":\"255.0.0.0\",\"gateway\":\"10.0.0.1\"}},\"otherInfo\":{\"disk\":0,\"status\":-1,\"pushLive\":1,\"pushPlayback\":1,\"oneArming\":1},\"iotPlatformId\":1,\"ipAddress\":{\"ip\":\"219.140.6.117\",\"country\":\"China\",\"countryCode\":\"CN\",\"city\":\"\",\"cityCode\":\"\"},\"state\":2,\"time\":1768557282,\"did\":10868,\"uid\":10048,\"isAdmin\":1,\"aliasName\":\"rk3568\",\"nickname\":\"\",\"stateTime\":1768557282,\"wakeupState\":2,\"wakeupStateTime\":0}"
        val string2 =
            "{\"id\":22299,\"sn\":\"1398000200edfb724bbf\",\"type\":21,\"model\":\"\",\"region\":\"cn-north-1\",\"vendor\":\"HDT-NEUTRAL\",\"chNum\":36,\"created\":1792049323,\"updated\":1792049323842,\"firmwareVersion\":\"V2.3.0\",\"firmwareDate\":20261009,\"p2pVersion\":\"V1.0.92.2026090118\",\"netInfo\":{\"type\":0,\"ethInfo\":{\"ip\":\"192.168.3.43\",\"netMask\":\"255.255.255.0\",\"gateway\":\"192.168.3.1\"}},\"otherInfo\":{\"disk\":1,\"status\":1,\"pushLive\":1,\"pushPlayback\":1,\"oneArming\":1,\"upgrade\":0},\"iotPlatformId\":1,\"ipAddress\":{\"ip\":\"111.172.124.249\",\"country\":\"China\",\"countryCode\":\"CN\",\"city\":\"\",\"cityCode\":\"\"},\"state\":1,\"time\":1791875470,\"did\":29367,\"uid\":10296,\"isAdmin\":2,\"aliasName\":\"\",\"nickname\":\"\",\"stateTime\":1791875470,\"wakeupState\":2,\"wakeupStateTime\":0,\"adminUid\":10244,\"adminUser\":{\"id\":10244,\"openId\":\"81a681bf5f174636996838cf54c704e2\",\"account\":\"13337485315\",\"picture\":\"\",\"sex\":0,\"nickName\":\"13337485315\"},\"rule\":{\"timeSpan\":[0,2359],\"week\":127,\"power\":-1,\"channel\":[\"*\"]}}"
        val device = Gson().fromJson<DeviceInfo>(string, DeviceInfo::class.java)
        val device1 = Gson().fromJson<DeviceInfo>(string1, DeviceInfo::class.java)
        val device2 = Gson().fromJson<DeviceInfo>(string2, DeviceInfo::class.java)
        list.add(device)
        list.add(device1)
        list.add(device2)
    }

    override fun isDeviceAlreadyAdded(sn: String): Boolean = false

    override fun isSerialNumberValid(sn: String): Boolean = true

    override fun addDeviceSuccess(sn: String) {
        Log.i("AddDevice", "host received added sn=$sn")
    }

    override fun addDeviceSuccessForShare() {
        Log.i("AddDevice", "addDeviceSuccessForShare")
    }

    override fun getAccount(): String {
        return ""
    }

    override fun getAccountType(): Boolean {
        return true
    }

    override fun exitShare(sn: String) {

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

    }

    override fun getUserId(): Int {
        return 10296
    }

    override fun getUserToken(): String {
       return "9f1012e59c08df66b471cb4080c6fd01"
    }
}