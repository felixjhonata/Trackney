package com.felixjhonata.trackney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.felixjhonata.trackney.home.view.HomePage
import com.felixjhonata.trackney.ui.theme.TrackneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackneyTheme {
                HomePage()
            }
        }
    }
}