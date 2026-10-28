package com.git.linkeasytest

import android.util.Log
import com.git.linkeasycommon.bean.BatteryManagement
import com.git.linkeasycommon.bean.DeviceInfo
import com.git.linkeasycommon.bean.Extend4g
import com.git.linkeasycommon.utils.JsonConfig
import com.git.linkeasyfeatureadddevice.AddDeviceHostCallback
import com.git.linkeasyfeaturealarm.AlarmHostCallBack
import com.git.linkeasyfeaturepreview.PreviewHostCallback
import com.git.linkeasyfeatureset.SetHostCallback
import com.git.linkeasyfeaturesharedevice.ShareDeviceHostCallBack
import com.git.linkeasyrtm.RtmHostCallback
import com.git.linkeasytest.ui.DeviceListController
import com.google.gson.Gson

class SdkCallBack() : AddDeviceHostCallback, RtmHostCallback, PreviewHostCallback, ShareDeviceHostCallBack, AlarmHostCallBack,
    SetHostCallback {

    override fun isDeviceAlreadyAdded(sn: String): Boolean = false

    override fun isSerialNumberValid(sn: String): Boolean = true

    override fun addDeviceSuccess(sn: String) {
        Log.i("AddDevice", "host received added sn=$sn")
    }

    override fun addDeviceSuccessForShare() {
        Log.i("AddDevice", "addDeviceSuccessForShare")
    }

    override fun getDeviceInfo(sn: String): DeviceInfo? {
        return DeviceListController.getDeviceList().firstOrNull {
            it.sn.equals(sn)
        }
    }

    override fun deleteDevice(sn: String) {

    }

    override fun updateDeviceOtherInfoExtend4g(sn: String, e: Extend4g) {

    }

    override fun checkDeviceNickNameDuplicate(nickName: String): Boolean {
        return false
    }

    override fun updataDeviceName(sn: String, nickName: String) {

    }

    override fun getAccount(): String {
        return ""
    }

    override fun getAccountType(): Boolean {
        return true
    }

    override fun exitShare(sn: String) {

    }

    override fun getDataFrontOrBackgroundDesk(): Boolean {
        return true
    }

    override fun getDeviceList(): List<DeviceInfo>? {
        return DeviceListController.getDeviceList()
    }

    override fun updataOnlineUsersAovPowerInfo(sn: String, mOnlineUsers: Int, mBatteryManagement: BatteryManagement) {

    }

    override fun getUserId(): Int {
        return MainActivity.uid
    }

    override fun getUserToken(): String {
        return MainActivity.token
    }
}