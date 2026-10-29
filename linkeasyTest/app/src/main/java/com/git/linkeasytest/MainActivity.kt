package com.git.linkeasytest

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.os.Message
import android.text.TextUtils

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import com.git.linkeasybase.flow.FlowBus
import com.git.linkeasycommon.bean.DeviceInfo
import com.git.linkeasycommon.constantRes.IntegerConstantResource
import com.git.linkeasycommon.hostBean.HostAlarmInfo
import com.git.linkeasycommon.hostBean.HostPreviewInfo
import com.git.linkeasycommon.hostBean.HostSetInfo
import com.git.linkeasycommon.hostBean.HostShareDeviceInfo
import com.git.linkeasycommon.hostBean.HostUserinfo
import com.git.linkeasycommon.utils.DeviceType
import com.git.linkeasycommon.utils.ScreenUtil
import com.git.linkeasyfeatureadddevice.AddDeviceApi
import com.git.linkeasyfeatureadddevice.AddDeviceContract
import com.git.linkeasyfeatureadddevice.controller.BluetoothController
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
import com.git.linkeasyuibase.util.ToastUtils
import com.git.linkeasyuibase.view.ShareTitleNoCallBackHasDownLine
import com.git.linkeasyuibase.view.TextCenterVertically
import com.git.linkeasyuibase.view.click
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan
import kotlinx.coroutines.launch


/**
 * SDK 宿主测试页：点击按钮拉起添加设备流程。
 * 真正的 SDK 初始化在 [LinkEasyApp] 中完成。
 */
class MainActivity : ComponentActivity(), FlowBus.acceptMessage {
    companion object {
        const val uid = 15772
        const val token = "595d93773dac13f1d38c8c8e3f8e0752"
        const val TAG = "MainActivity"
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
    val showScanCode = mutableStateOf(false)
    val showBluetoothSearch = mutableStateOf(false)
    private lateinit var remoteView: RemoteView
    private lateinit var mBluetoothController: BluetoothController
    private val mList = mutableStateOf<List<BluetoothDevice>>(emptyList())

    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LinkeasyTestTheme {
                val deviceList = DeviceListController.deviceList.collectAsState()
                val scope = rememberCoroutineScope()
                val mShowScanCode = remember { showScanCode }
                val mShowBluetoothSearch = remember { showBluetoothSearch }
                DisposableEffect(Unit) {
                    mBluetoothController = BluetoothController(applicationContext)
                    FlowBus.register(this@MainActivity, TAG, this@MainActivity)
                    scope.launch {
                        val list = LinkEasySdk.requestDeviceList(uid, token)
                        DeviceListController.updateDeviceList(list)
                    }
                    onDispose { FlowBus.unRegister(this@MainActivity, TAG) }
                }

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
                        //AddDeviceApi.start(addDeviceLauncher, HostUserinfo(uid, token))
                        mShowScanCode.value = true
                    }) {
                        Text(text = stringResource(R.string.add_device))
                    }
                    Button(onClick = {
                        mShowBluetoothSearch.value = true
                        mBluetoothController.findBluetooth(TAG)
                    }) {
                        Text(text = stringResource(R.string.bluetooth_search))
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
                if (mShowScanCode.value) {
                    ScanCodeView()
                }
                if (mShowBluetoothSearch.value) {
                    BluetoothList()
                }
            }
        }
    }

    @Composable
    fun ScanCodeView() {
        val currentContext = LocalContext.current
        ConstraintLayout (modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = com.git.linkeasyuibase.R.color.color_fcfcfc))
            .click(cancalBg = true) { }) {
            val (
                title, leftTopIm, rightTopIm, leftBottomIm, rightBottomIm, tv, remoteCl, spacerView,
            ) = createRefs()
            ShareTitleNoCallBackHasDownLine(title = stringResource(id = com.git.linkeasyuibase.R.string.scan_it),
                m = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top,
                            ScreenUtil.getStatusBarHeightToDp(currentContext).dp)
                    }) { type ->
                when (type) {
                    com.git.linkeasyuibase.R.id.back -> {
                        showScanCode.value = false
                    }

                }
            }
            AndroidView(factory = { context ->
                remoteView = RemoteView.Builder().setContext(this@MainActivity).setFormat(HmsScan.ALL_SCAN_TYPE).build()
                remoteView.setOnResultCallback { result ->
                    if (showScanCode.value) {
                        if (result != null && result.size > 0 && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                            showScanCode.value = false
                            AddDeviceApi.addDeviceForQR(result[0].getOriginalValue(), uid, token)
                        } else {
                            ToastUtils.getToastUtils().makeText(ShareDeviceApi.context,
                                ShareDeviceApi.context.resources.getString(com.git.linkeasyuibase.R.string.identification_of_failure))
                        }
                    }
                }
                remoteView.onCreate(null)
                startRemoteViewIfReady(remoteView)
                remoteView
            }, modifier = Modifier.constrainAs(remoteCl) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(title.bottom)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            })
            Spacer(modifier = Modifier
                .height(240.dp)
                .width(240.dp)
                .constrainAs(spacerView) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(remoteCl.top)
                    bottom.linkTo(remoteCl.bottom, margin = 100.dp)
                })
            Image(painter = painterResource(id = com.git.linkeasyuibase.R.mipmap.ic_qr_scan_tl),
                contentDescription = null,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .constrainAs(leftTopIm) {
                        start.linkTo(spacerView.start)
                        top.linkTo(spacerView.top)
                    })
            Image(painter = painterResource(id = com.git.linkeasyuibase.R.mipmap.ic_qr_scan_tr),
                contentDescription = null,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .constrainAs(rightTopIm) {
                        end.linkTo(spacerView.end)
                        top.linkTo(spacerView.top)
                    })
            Image(painter = painterResource(id = com.git.linkeasyuibase.R.mipmap.ic_qr_scan_bl),
                contentDescription = null,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .constrainAs(leftBottomIm) {
                        start.linkTo(spacerView.start)
                        bottom.linkTo(spacerView.bottom)
                    })
            Image(painter = painterResource(id = com.git.linkeasyuibase.R.mipmap.ic_qr_scan_br),
                contentDescription = null,
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .constrainAs(rightBottomIm) {
                        end.linkTo(spacerView.end)
                        bottom.linkTo(spacerView.bottom)
                    })
            TextCenterVertically(m = Modifier
                .wrapContentSize()
                .constrainAs(tv) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(spacerView.bottom, margin = 16.dp)
                },
                textT = stringResource(id = com.git.linkeasyuibase.R.string.qr_describe),
                colorT = com.git.linkeasyuibase.R.color.white,
                fontWeightT = FontWeight.Medium,
                size = 12.sp)
        }
    }

    @SuppressLint("MissingPermission")
    @Composable
    fun BluetoothList() {
        val list = remember { mList }
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = com.git.linkeasyuibase.R.color.color_fcfcfc))
            .click(cancalBg = true) { }) {
            ShareTitleNoCallBackHasDownLine(title = "蓝牙搜索",
                m = Modifier
                    .fillMaxWidth()
                    .height(44.dp)) { type ->
                when (type) {
                    com.git.linkeasyuibase.R.id.back -> {
                        showBluetoothSearch.value = false
                        mBluetoothController.onDestroy()
                    }

                }
            }
            LazyColumn(modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 28.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(list.value) { index: Int, item: BluetoothDevice ->
                    TextCenterVertically(m = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .click {
                            AddDeviceApi.start(addDeviceLauncher, HostUserinfo(uid, token, AddDeviceApi.ADD_TYPE_BLUETOOTH, item))
                        },
                        textT = item.name,
                        size = 14.sp,
                        colorT = colorResource(R.color.purple_200),
                        fontWeightT = FontWeight.Normal)
                }
            }
        }
    }

    private fun startRemoteViewIfReady(scanView: RemoteView) {
        if (remoteView !== scanView) {
            return
        }
        val state = lifecycle.currentState
        if (state.isAtLeast(Lifecycle.State.STARTED)) {
            scanView.onStart()
        }
        if (state.isAtLeast(Lifecycle.State.RESUMED)) {
            scanView.onResume()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            IntegerConstantResource.EVENT_TYPE_BLUETOOTH_FOUND_DEVICE -> {
                if (msg.obj is BluetoothDevice) {
                    (msg.obj as BluetoothDevice).let { device ->
                        if (device.name.contains("bz-")) {
                            if (mList.value.none { it.address == device.address }) {
                                mList.value = mList.value + device
                            }
                        }
                    }
                }

            }
        }
        return true
    }

    override fun onBackPressed() {
        if (showScanCode.value) {
            showScanCode.value = false
            return
        }
        if (showBluetoothSearch.value) {
            showBluetoothSearch.value = false
            mList.value = emptyList()
            return
        }
        super.onBackPressed()
    }
}
