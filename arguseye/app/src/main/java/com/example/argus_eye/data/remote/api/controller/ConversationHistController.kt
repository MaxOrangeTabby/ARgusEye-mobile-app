package com.example.argus_eye.controller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.argus_eye.data.model.InteractionResponse
import com.example.argus_eye.data.remote.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConversationHistController {
    private val _interactions = mutableStateOf<List<InteractionResponse>>(emptyList())
    val interactions: State<List<InteractionResponse>> = _interactions

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchInteractions(force: Boolean = false) {
        if (!force && _interactions.value.isNotEmpty()) return

        _isLoading.value = true
        _error.value = null
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getInteractions()
                _interactions.value = response
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
