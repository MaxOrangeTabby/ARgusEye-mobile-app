package com.example.argus_eye.controller

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import com.example.argus_eye.data.model.InteractionResponse
import com.example.argus_eye.data.remote.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ConversationHistController(
    private val ioContext: CoroutineContext = Dispatchers.IO
) {
    private val _interactions = mutableStateOf<List<InteractionResponse>>(emptyList())
    val interactions: State<List<InteractionResponse>> = _interactions

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    val searchQuery = mutableStateOf("")

    val filteredInteractions: State<List<InteractionResponse>> = derivedStateOf {
        val query = searchQuery.value.trim().lowercase()
        if (query.isEmpty()) {
            _interactions.value
        } else {
            _interactions.value.filter {
                it.personName?.lowercase()?.contains(query) == true ||
                        it.interactionId.toString().contains(query)
            }
        }
    }

    fun fetchInteractions(force: Boolean = false) {
        if (!force && _interactions.value.isNotEmpty()) return

        _isLoading.value = true
        _error.value = null
        CoroutineScope(ioContext).launch {
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
