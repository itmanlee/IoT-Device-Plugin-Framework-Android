package com.git.linkeasytest

import android.app.Application
import android.util.Log
import com.git.linkeasyconfig.config.MyConfig
import com.git.linkeasyconfig.config.ServiceUrl
import com.git.linkeasyfeatureadddevice.AddDeviceApi
import com.git.linkeasyfeatureadddevice.AddDeviceHostCallback
import com.git.linkeasysdk.LinkEasySdk

/**
 * 应用入口。进程启动时初始化 LinkEasy SDK，并注册添加设备相关的宿主回调。
 * 需在 AndroidManifest 的 application 节点中声明 android:name=".LinkEasyApp"。
 */
class LinkEasyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 SDK：应用标识、服务地址、主题色等由宿主传入。
        // XML 主题：宿主需在 themes.xml 定义 Theme.Mytheme（并让 Theme.LinkeasyAar 继承它），
        // 通过 attr primaryThemeColor、logo 覆盖库默认主色和 Logo；Manifest 的 application/activity
        // 使用 @style/Theme.LinkeasyAar。不写这些 attr 时仍用库内默认。
        // MyConfig.primaryThemeColor 是代码侧主色，可与 XML 的 primaryThemeColor 一并配置。
        LinkEasySdk.initSdk(
            applicationContext,
            MyConfig(
                appName = "linkeasy",
                applicationId = packageName,
                appVendor = "linkeasy",
                url = ServiceUrl(baseUrl = "http://ai-cn.yeelinkvision.com"),
                primaryThemeColor = 0xFFFF0000.toInt()
            )
        )
        // 宿主侧校验与结果通知：插件通过这些回调询问设备是否已添加、SN 是否合法，以及添加成功
        AddDeviceApi.setHostCallback(object : AddDeviceHostCallback {
            override fun isDeviceAlreadyAdded(sn: String): Boolean = false

            override fun isSerialNumberValid(sn: String): Boolean = true

            override fun addDeviceSuccess(sn: String) {
                Log.i("AddDevice", "host received added sn=$sn")
            }
        })
    }
}
