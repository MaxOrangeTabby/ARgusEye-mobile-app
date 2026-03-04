package com.example.argus_eye.core

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.argus_eye.data.model.MainModel
import com.example.argus_eye.data.remote.api.MainController
import com.example.argus_eye.ui.LandingView
import com.example.argus_eye.ui.MainView
import com.example.argus_eye.ui.theme.ArguseyeTheme

class MainActivity : ComponentActivity() {
    private lateinit var controller: MainController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Model: Data layer
        val model = MainModel()
        // Controller: Logic layer
        controller = MainController(model)

        enableEdgeToEdge()
        setContent {
            ArguseyeTheme {
                var showMainView by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    if (showMainView) {
                        // View: UI layer
                        MainView(
                            controller = controller,
                            modifier = Modifier.Companion.padding(innerPadding)
                        )
                    } else {
                        LandingView(
                            onEnterApp = { showMainView = true },
                            modifier = Modifier.Companion.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}