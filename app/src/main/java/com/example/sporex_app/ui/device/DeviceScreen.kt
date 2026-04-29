package com.example.sporex_app.ui.device

sealed class DeviceScreen(val route: String) {
    object DeviceSetup : DeviceScreen("device_setup")
    object DeviceEdit : DeviceScreen("device_edit")

    object DeviceDashboard : DeviceScreen("device_dashboard")

    object TestConnection : DeviceScreen("test_connection")

    object DeviceDetails : DeviceScreen("device_details")

    object CreateDevice : DeviceScreen("create_device")
}
