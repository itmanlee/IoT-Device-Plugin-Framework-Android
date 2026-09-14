package com.git.linkeasytest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.git.linkeasycommon.hostBean.HostUserinfo
import com.git.linkeasyfeatureadddevice.AddDeviceApi
import com.git.linkeasyfeatureadddevice.AddDeviceContract
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkeasyTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = {
                            // 拉起添加设备 Activity；HostUserinfo 的 uid / token 需换成当前登录用户
                            AddDeviceApi.start(addDeviceLauncher, HostUserinfo(10296, "a8abe75a2bc8c8bd2b8fd5822f10754d"))
                        }) {
                            Text(text = stringResource(R.string.add_device))
                        }
                    }
                }
            }
        }
    }
}

