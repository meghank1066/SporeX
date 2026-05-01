package com.example.sporex_app.ui.device


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sporex_app.ui.navigation.BottomNavBar
import com.example.sporex_app.ui.navigation.TopBar
import kotlinx.coroutines.launch

enum class DeviceStep {
    ENTER_CODE,
    CONNECTING,
    SUCCESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDeviceScreen(
    onCreateClick: (String) -> Unit
) {
    var step by remember { mutableStateOf(DeviceStep.ENTER_CODE) }
    var deviceCode by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun isValidCode(code: String): Boolean {
        val regex = Regex("^(?=(.*[A-Za-z]){8})(?=(.*\\d){8})[A-Za-z\\d]{16}$")
        return regex.matches(code)
    }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomNavBar(currentScreen = "device") },
        containerColor = MaterialTheme.colorScheme.primary
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = MaterialTheme.shapes.extraLarge,
                color = Color(0xFF121212)
            ) {

                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    when (step) {

                        DeviceStep.ENTER_CODE -> {
                            EnterCodeStep(
                                deviceCode = deviceCode,
                                onCodeChange = {
                                    deviceCode = it
                                    error = null
                                },
                                error = error,
                                onSubmit = {
                                    if (isValidCode(deviceCode)) {
                                        step = DeviceStep.CONNECTING

                                        kotlinx.coroutines.MainScope().launch {
                                            kotlinx.coroutines.delay(1500)
                                            step = DeviceStep.SUCCESS
                                            onCreateClick(deviceCode)
                                        }

                                    } else {
                                        error = "Invalid code. Must be 16 chars (8 letters + 8 digits)."
                                    }
                                }
                            )
                        }

                        DeviceStep.CONNECTING -> {
                            ConnectingStep()
                        }

                        DeviceStep.SUCCESS -> {
                            SuccessStep()
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun EnterCodeStep(
    deviceCode: String,
    onCodeChange: (String) -> Unit,
    error: String?,
    onSubmit: () -> Unit
) {

    Text(
        text = "Link your device",
        style = MaterialTheme.typography.titleLarge,
        color = Color.White
    )

    Spacer(Modifier.height(8.dp))

    Text(
        text = "Enter the 16-character code shown on your device",
        color = Color.LightGray
    )

    Spacer(Modifier.height(20.dp))

    OutlinedTextField(
        value = deviceCode,
        onValueChange = onCodeChange,
        singleLine = true,
        isError = error != null,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        textStyle = LocalTextStyle.current.copy(
            letterSpacing = 2.sp,
            color = Color.White
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Black,
            unfocusedContainerColor = Color.Black,
            focusedBorderColor = Color.Cyan,
            unfocusedBorderColor = Color.DarkGray,
            cursorColor = Color.Cyan
        )
    )

    if (error != null) {
        Spacer(Modifier.height(8.dp))
        Text(error, color = Color.Red)
    }

    Spacer(Modifier.height(20.dp))

    Button(
        onClick = onSubmit,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Connect device")
    }
}

@Composable
fun ConnectingStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        CircularProgressIndicator(color = Color.Cyan)

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Connecting to device...",
            color = Color.White
        )

        Text(
            text = "Syncing secure handshake",
            color = Color.Gray
        )
    }
}

@Composable
fun SuccessStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text("✅", style = MaterialTheme.typography.displayMedium)

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Device connected",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "You're ready to go",
            color = Color.Gray
        )
    }
}