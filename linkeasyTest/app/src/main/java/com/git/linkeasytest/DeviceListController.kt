package com.git.linkeasytest

import com.git.linkeasycommon.bean.DeviceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DeviceListController {
    private val _deviceList = MutableStateFlow<List<DeviceInfo>>(emptyList())
    val deviceList: StateFlow<List<DeviceInfo>> = _deviceList.asStateFlow()

    fun updateDeviceList(list: List<DeviceInfo>) {
        _deviceList.value = list
    }
    fun getDeviceList():List<DeviceInfo>{
        return _deviceList.value
    }
}
