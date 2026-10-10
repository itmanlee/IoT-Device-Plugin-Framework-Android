package com.git.linkeasytest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.git.linkeasycommon.hostBean.HostPreviewInfo
import com.git.linkeasycommon.hostBean.HostUserinfo
import com.git.linkeasyfeatureadddevice.AddDeviceApi
import com.git.linkeasyfeatureadddevice.AddDeviceContract
import com.git.linkeasyfeaturepreview.PreviewApi
import com.git.linkeasyfeaturepreview.PreviewContract
import com.git.linkeasyrtc.RtcModule
import com.git.linkeasyrtm.RtmModule
import com.git.linkeasytest.ui.theme.LinkeasyTestTheme


/**
 * SDK 宿主测试页：点击按钮拉起添加设备流程。
 * 真正的 SDK 初始化在 [LinkEasyApp] 中完成。
 */
class MainActivity : ComponentActivity() {
    /**
     * 添加设备页的 Activity Result 回调。
     * [finishedAfterSuccess] 为 true 表示添加成功后关闭页面，false 表示用户取消或未完成。
     */
    private val addDeviceLauncher = registerForActivityResult(AddDeviceContract()) { finishedAfterSuccess ->
        if (finishedAfterSuccess) {
            Toast.makeText(this, "add-device page closed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "add device canceled", Toast.LENGTH_SHORT).show()
        }
    }
    /**
     * 预览页的 Activity Result 回调。
     * [finishedAfterSuccess] 为 true 表示添加成功后关闭页面，false 表示用户取消或未完成。
     */
    private val previewDeviceLauncher = registerForActivityResult(PreviewContract()) { finishedAfterSuccess ->
        Toast.makeText(this, "PreviewActivity closed", Toast.LENGTH_SHORT).show()
    }
    /**
     * 用于刷新RTC/RTM的token，建议用户登录后调用
     */
    fun loginRtcRtm() {
        RtcModule.startMonitor()
        RtcModule.getNewToken()
        RtmModule.getNewToken()
    }

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkeasyTestTheme {
                DisposableEffect(Unit) {
                    loginRtcRtm()
                    onDispose { }
                }
                FlowRow(modifier = Modifier
                    .padding(top = 44.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = {
                        AddDeviceApi.start(addDeviceLauncher, HostUserinfo(10296, "9f1012e59c08df66b471cb4080c6fd01"))
                    }) {
                        Text(text = stringResource(R.string.add_device))
                    }
                    Button(onClick = {
                        PreviewApi.start(previewDeviceLauncher,
                            HostPreviewInfo(10296, "9f1012e59c08df66b471cb4080c6fd01", "03a3000200a96889de71"))
                    }) {
                        Text(text = stringResource(R.string.start_preview))
                    }
                }


            }
        }
    }
}

