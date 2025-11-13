package com.example.gps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.gps.ui.AppNavigation
import com.example.gps.ui.theme.GpsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GpsTheme {
                AppNavigation()
            }
        }
    }
}
