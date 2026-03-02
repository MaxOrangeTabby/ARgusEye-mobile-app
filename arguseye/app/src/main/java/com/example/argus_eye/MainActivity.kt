package com.example.argus_eye

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.argus_eye.controller.MainController
import com.example.argus_eye.model.MainModel
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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // View: UI layer
                    MainView(
                        controller = controller,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
