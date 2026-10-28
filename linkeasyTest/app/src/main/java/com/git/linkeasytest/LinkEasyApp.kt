package com.git.linkeasytest

import android.app.Application
import android.util.Log
import com.git.linkeasyconfig.config.MyConfig
import com.git.linkeasyconfig.config.ServiceUrl
import com.git.linkeasyfeatureadddevice.AddDeviceApi
import com.git.linkeasyfeatureadddevice.AddDeviceHostCallback
import com.git.linkeasyfeaturealarm.AlarmApi
import com.git.linkeasyfeaturepreview.PreviewApi
import com.git.linkeasyfeatureset.SetApi
import com.git.linkeasyfeaturesharedevice.ShareDeviceApi
import com.git.linkeasyrtc.RtcModule
import com.git.linkeasyrtm.RtmModule
import com.git.linkeasysdk.LinkEasySdk

/**
 * 应用入口。进程启动时初始化 LinkEasy SDK，并注册添加设备相关的宿主回调。
 * 需在 AndroidManifest 的 application 节点中声明 android:name=".LinkEasyApp"。
 */
class LinkEasyApp : Application() {
    val callback = SdkCallBack()
    override fun onCreate() {
        super.onCreate()
        LinkEasySdk.initSdk(
            applicationContext,
            MyConfig(
                appName = "linkeasy",
                applicationId = packageName,
                appVendor = "LINKEASY",
                url = ServiceUrl(baseUrl = "http://ai-cn.yeelinkvision.com"),
                primaryThemeColor = 0xFFFF0000.toInt(),
                appRtcId = "e5948fec52584be4a29543a66c458bd2"
            )
        )
        ShareDeviceApi.init(applicationContext)
        PreviewApi.init(applicationContext)
        AlarmApi.init(applicationContext)
        SetApi.init(applicationContext)
        AddDeviceApi.setHostCallback(callback)
        PreviewApi.setHostCallback(callback)
        RtmModule.setHostCallback(callback)
        ShareDeviceApi.setHostCallback(callback)
        AlarmApi.setHostCallback(callback)
        SetApi.setHostCallback(callback)
        loginRtcRtm()
    }
    /**
     * 用于刷新RTC/RTM的token，建议用户登录后调用
     */
    fun loginRtcRtm() {
        RtcModule.startMonitor()
        RtcModule.getNewToken()
        RtmModule.getNewToken()
    }

}
