package com.example.sporex_app.ui.device

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sporex_app.ui.theme.SPOREX_AppTheme
import com.example.sporex_app.utils.isDarkMode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class DeviceActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SPOREX_AppTheme {
                val context = this
                var darkTheme by remember { mutableStateOf(isDarkMode(context)) }

                SPOREX_AppTheme(darkTheme = darkTheme) {
                    val navController: NavHostController = rememberNavController()

                    // Shared repository for all screens
                    val repo = DeviceRepository(this)

                    NavHost(
                        navController = navController,
                        startDestination = DeviceScreen.DeviceDashboard.route
                    ) {

                        composable("device") {
                            DeviceDashboardScreen(
                                deviceName = "Living Room Sensor",
                                onManageDeviceClick = {
                                    navController.navigate("manage_device")
                                },
                                onCreateDeviceClick = {
                                    navController.navigate("create_device")
                                }
                            )
                        }


                        composable("create_device") {
                            CreateDeviceScreen(
                                onCreateClick = { deviceName ->
                                    navController.popBackStack()
                                }
                            )
                        }


                        // DASHBOARD SCREEN
                        composable(DeviceScreen.DeviceDashboard.route) {
                            DeviceDashboardScreen(
                                deviceName = repo.getDeviceName(),
                                onManageDeviceClick = {
                                    navController.navigate(DeviceScreen.DeviceEdit.route)
                                },
                                onCreateDeviceClick = {
                                    navController.navigate("create_device")
                                }
                            )
                        }


                        // EDIT SCREEN
                        composable(DeviceScreen.DeviceEdit.route) {
                            EditDeviceScreen(
                                deviceName = repo.getDeviceName(),
                                onRename = { newName ->
                                    repo.setDeviceName(newName)
                                },
                                onBackClick = {
                                    navController.popBackStack()
                                },
                                onTestConnectionClick = {
                                    navController.navigate(DeviceScreen.TestConnection.route)
                                }
                            )
                        }


                        // TEST CONNECTION SCREEN
                        composable(DeviceScreen.TestConnection.route) {
                            TestConnectionRoute(
                                repo = repo,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}