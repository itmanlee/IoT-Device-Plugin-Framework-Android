package com.git.linkeasytest

import android.text.TextUtils
import android.util.Log
import com.git.linkeasybase.flow.FlowBus
import com.git.linkeasycommon.bean.BatteryManagement
import com.git.linkeasycommon.bean.DeviceInfo
import com.git.linkeasycommon.bean.Extend4g
import com.git.linkeasycommon.utils.JsonConfig
import com.git.linkeasyfeatureadddevice.AddDeviceHostCallback
import com.git.linkeasyfeaturealarm.AlarmHostCallBack
import com.git.linkeasyfeaturemisc.MiscHostCallback
import com.git.linkeasyfeaturepreview.PreviewHostCallback
import com.git.linkeasyfeatureset.SetHostCallback
import com.git.linkeasyfeaturesharedevice.ShareDeviceHostCallBack
import com.git.linkeasyrtm.RtmHostCallback
import com.google.gson.Gson

class SdkCallBack() : AddDeviceHostCallback, RtmHostCallback, PreviewHostCallback, ShareDeviceHostCallBack, AlarmHostCallBack,
    SetHostCallback, MiscHostCallback {
    /**
     * 暂时写死，后面需要确认该数据如何获取
     */
    val ossbatchList = listOf("03d2", "03ad", "33da")
    override fun isDeviceAlreadyAdded(sn: String): Boolean = false

    override fun isSerialNumberValid(sn: String): Boolean = true
    private lateinit var activity: MainActivity
    fun setActivity(activity: MainActivity) {
        this.activity = activity
    }

    override fun addDeviceSuccess(sn: String, did: Int) {
        if (::activity.isInitialized) {
            activity.gotoEditDeviceName(sn, did)
        }
    }

    override fun addDeviceFail(sn: String) {
        Log.i("AddDevice", "host received addDeviceFail sn=$sn")
    }

    override fun addDeviceSuccessForShare() {
        Log.i("AddDevice", "addDeviceSuccessForShare")
    }

    override fun addDeviceFailForShare() {
        Log.i("AddDevice", "addDeviceFailForShare")
    }

    override fun activationScreen(type: Int) {

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
        return if (TextUtils.isEmpty(nickName)) {
            false
        } else {
            val flag = DeviceListController.getDeviceList().firstOrNull {
                it.aliasName == nickName
            }
            flag != null
        }
    }

    override fun changeDeviceNickName() {
//刷新设备列表
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

    override fun getSite(): String {
        return "cn"
    }

    override fun isSupportCloud(sn: String): Boolean {
        return ossbatchList.contains(sn.substring(0, 4))
    }

    override fun gotoChannelSet(sn: String, channel: Int, fromType: Int) {
        if (::activity.isInitialized) {
            activity.gotoChannelSet(sn, channel, fromType)
        }
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