package com.git.linkeasytest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.git.linkeasycommon.bean.DeviceInfo
import com.git.linkeasycommon.hostBean.HostAlarmInfo
import com.git.linkeasycommon.hostBean.HostPreviewInfo
import com.git.linkeasycommon.hostBean.HostSetInfo
import com.git.linkeasycommon.hostBean.HostShareDeviceInfo
import com.git.linkeasycommon.hostBean.HostUserinfo
import com.git.linkeasycommon.utils.DeviceType
import com.git.linkeasyfeatureadddevice.AddDeviceApi
import com.git.linkeasyfeatureadddevice.AddDeviceContract
import com.git.linkeasyfeaturealarm.AlarmApi
import com.git.linkeasyfeaturealarm.AlarmContract
import com.git.linkeasyfeaturepreview.PreviewApi
import com.git.linkeasyfeaturepreview.PreviewContract
import com.git.linkeasyfeatureset.SetApi
import com.git.linkeasyfeatureset.SetContract
import com.git.linkeasyfeaturesharedevice.ShareDeviceApi
import com.git.linkeasyfeaturesharedevice.ShareDeviceContract
import com.git.linkeasysdk.LinkEasySdk
import com.git.linkeasytest.ui.DeviceListController
import com.git.linkeasytest.ui.theme.LinkeasyTestTheme
import com.git.linkeasyuibase.view.click
import kotlinx.coroutines.launch


/**
 * SDK 宿主测试页：点击按钮拉起添加设备流程。
 * 真正的 SDK 初始化在 [LinkEasyApp] 中完成。
 */
class MainActivity : ComponentActivity() {
    companion object {
        const val uid = 15772
        const val token = "595d93773dac13f1d38c8c8e3f8e0752"
    }

    private val addDeviceLauncher = registerForActivityResult(AddDeviceContract()) { finishedAfterSuccess ->
        if (finishedAfterSuccess) {
            Toast.makeText(this, "add-device page closed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "add device canceled", Toast.LENGTH_SHORT).show()
        }
    }
    private val previewDeviceLauncher = registerForActivityResult(PreviewContract()) { finishedAfterSuccess ->
        Toast.makeText(this, "PreviewActivity closed", Toast.LENGTH_SHORT).show()
    }
    private val shareDeviceLauncher = registerForActivityResult(ShareDeviceContract()) { finishedAfterSuccess ->
        Toast.makeText(this, "ShareDeviceActivity closed", Toast.LENGTH_SHORT).show()
    }
    private val alarmContractLauncher = registerForActivityResult(AlarmContract()) { finishedAfterSuccess ->
        Toast.makeText(this, "AlarmActivity closed", Toast.LENGTH_SHORT).show()
    }
    private val setContractLauncher = registerForActivityResult(SetContract()) { finishedAfterSuccess ->
        Toast.makeText(this, "AlarmActivity closed", Toast.LENGTH_SHORT).show()
    }


    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkeasyTestTheme {
                val deviceList = DeviceListController.deviceList.collectAsState()
                val scope = rememberCoroutineScope()
                DisposableEffect(Unit) {
                    scope.launch {
                        val list = LinkEasySdk.requestDeviceList(uid, token)
                        DeviceListController.updateDeviceList(list)
                    }
                    onDispose { }
                }

                Column(modifier = Modifier
                    .padding(top = 44.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                ) {
                    Button(onClick = {
                        AddDeviceApi.start(addDeviceLauncher, HostUserinfo(uid, token))
                    }) {
                        Text(text = stringResource(R.string.add_device))
                    }
                    Button(onClick = {
                        scope.launch {
                            val list = LinkEasySdk.requestDeviceList(uid, token)
                            DeviceListController.updateDeviceList(list)
                        }
                    }) {
                        Text(text = stringResource(R.string.get_device_list))
                    }
                    LazyColumn(modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 28.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        itemsIndexed(deviceList.value) { index: Int, item: DeviceInfo ->
                            Column(Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(colorResource(com.git.linkeasyuibase.R.color.base_gray))
                                .padding(8.dp)) {
                                Text("sn:${item.sn} (${
                                    if (DeviceType.isNvr(item.type)) {
                                        "nvr"
                                    } else {
                                        "ipc"
                                    }
                                }) (${
                                    if (item.state == 1) {
                                        "在线"
                                    } else {
                                        "离线"
                                    }
                                })")
                                FlowRow(modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(stringResource(R.string.start_preview), modifier = Modifier.click {
                                        PreviewApi.start(previewDeviceLauncher,
                                            HostPreviewInfo(uid, token, item.sn))
                                    })
                                    Text(stringResource(R.string.device_share), modifier = Modifier.click {
                                        ShareDeviceApi.start(shareDeviceLauncher,
                                            HostShareDeviceInfo(uid, token, item.sn))
                                    })
                                    Text(stringResource(R.string.device_alarm), modifier = Modifier.click {
                                        AlarmApi.start(alarmContractLauncher,
                                            HostAlarmInfo(uid, token, item.sn))
                                    })
                                    Text(stringResource(R.string.device_set), modifier = Modifier.click {
                                        SetApi.start(setContractLauncher,
                                            HostSetInfo(uid, token, item.sn))
                                    })
                                    Text(stringResource(R.string.device_set3), modifier = Modifier.click {
                                        if (DeviceType.isNvr(item.type)) {
                                            SetApi.start(setContractLauncher,
                                                HostSetInfo(uid, token, item.sn, 1))
                                        } else {
                                            SetApi.start(setContractLauncher,
                                                HostSetInfo(uid, token, item.sn, -1, 1))
                                        }
                                    })

                                }
                            }
                        }
                    }
                }

            }
        }
    }
}
