package com.example.cestaOganicaIA

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.cestaOganicaIA.navigation.AppNav
import com.example.cestaOganicaIA.ui.shared.HuertoHogarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HuertoHogarTheme {
                Surface {
                    AppNav()
                }
            }
        }
    }
}
