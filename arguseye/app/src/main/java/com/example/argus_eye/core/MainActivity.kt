package com.example.argus_eye.core

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.argus_eye.data.model.MainModel
import com.example.argus_eye.data.remote.api.AuthManager
import com.example.argus_eye.data.remote.api.MainController
import com.example.argus_eye.data.remote.api.RetrofitClient
import com.example.argus_eye.ui.LandingView
import com.example.argus_eye.ui.LoginView
import com.example.argus_eye.ui.RegisterView
import com.example.argus_eye.ui.MainView
import com.example.argus_eye.ui.theme.ArguseyeTheme
import kotlinx.coroutines.launch

enum class Screen {
    Landing,
    Login,
    Register,
    Main
}

class MainActivity : ComponentActivity() {
    private lateinit var controller: MainController
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize RetrofitClient
        RetrofitClient.getApiService(this)

        val model = MainModel()
        controller = MainController(model)
        authManager = AuthManager(this)

        enableEdgeToEdge()
        setContent {
            ArguseyeTheme {
                var currentScreen by remember { 
                    mutableStateOf(if (authManager.getCurrentUser() != null) Screen.Main else Screen.Landing) 
                }

                // Handle back button for top-level authentication screens
                BackHandler(enabled = currentScreen == Screen.Login || currentScreen == Screen.Register) {
                    currentScreen = Screen.Landing
                }

                Scaffold(modifier = Modifier.Companion.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        Screen.Landing -> {
                            LandingView(
                                onLoginClick = { currentScreen = Screen.Login },
                                onRegisterClick = { currentScreen = Screen.Register },
                                modifier = Modifier.Companion.padding(innerPadding)
                            )
                        }
                        Screen.Login -> {
                            LoginView(
                                onGoogleLoginClick = {
                                    lifecycleScope.launch {
                                        val success = authManager.signInWithGoogle("155984001396-fhnbl68l72n01tknl3ffi9cuidesem7o.apps.googleusercontent.com")
                                        if (success) {
                                            currentScreen = Screen.Main
                                        } else {
                                            Toast.makeText(this@MainActivity, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onGithubLoginClick = {
                                    lifecycleScope.launch {
                                        val success = authManager.signInWithGithub(this@MainActivity)
                                        if (success) {
                                            currentScreen = Screen.Main
                                        } else {
                                            Toast.makeText(this@MainActivity, "Github Sign-In Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onRegisterClick = {
                                    currentScreen = Screen.Register
                                },
                                modifier = Modifier.Companion.padding(innerPadding)
                            )
                        }
                        Screen.Register -> {
                            RegisterView(
                                onGoogleRegisterClick = {
                                    lifecycleScope.launch {
                                        val success = authManager.signInWithGoogle("155984001396-fhnbl68l72n01tknl3ffi9cuidesem7o.apps.googleusercontent.com")
                                        if (success) {
                                            currentScreen = Screen.Main
                                        } else {
                                            Toast.makeText(this@MainActivity, "Google Registration Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onGithubRegisterClick = {
                                    lifecycleScope.launch {
                                        val success = authManager.signInWithGithub(this@MainActivity)
                                        if (success) {
                                            currentScreen = Screen.Main
                                        } else {
                                            Toast.makeText(this@MainActivity, "Github Registration Failed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onLoginClick = {
                                    currentScreen = Screen.Login
                                },
                                modifier = Modifier.Companion.padding(innerPadding)
                            )
                        }
                        Screen.Main -> {
                            MainView(
                                controller = controller,
                                user = authManager.getCurrentUser(),
                                onLogoutClick = {
                                    authManager.signOut()
                                    currentScreen = Screen.Landing
                                },
                                modifier = Modifier.Companion.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}