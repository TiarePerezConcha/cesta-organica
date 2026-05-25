package com.example.cestaOganicaIA

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.cestaOganicaIA.navigation.AppNav
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme
import com.example.cestaOganicaIA.utils.CameraPermissionHelper
import com.example.cestaOganicaIA.viewmodel.QrViewModel

class MainActivity : ComponentActivity() {

    private val qrViewModel: QrViewModel by viewModels()
    private var hasCameraPermission by mutableStateOf(false)

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasCameraPermission = isGranted
            if (isGranted) {
                Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Se necesita permiso de cámara para escanear códigos QR", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hasCameraPermission = CameraPermissionHelper.hasCameraPermission(this)

        setContent {
            HuertoHogarTheme {
                Surface {
                    AppNav(
                        hasCameraPermission = hasCameraPermission,
                        onRequestPermission = {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    )
                }
            }
        }

        qrViewModel.qrResult.observe(this) { qrResult ->
            qrResult?.let { result ->
                Toast.makeText(this, "Código QR detectado: ${result.content}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hasCameraPermission = CameraPermissionHelper.hasCameraPermission(this)
    }
}
