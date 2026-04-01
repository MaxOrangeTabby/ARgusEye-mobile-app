package com.example.argus_eye.data.remote.api.controller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.argus_eye.data.model.ContactModel
import com.example.argus_eye.data.model.LabelRequest
import com.example.argus_eye.data.remote.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeController {
    private val _unlabeledPeople = mutableStateOf<List<ContactModel>>(emptyList())
    val unlabeledPeople: State<List<ContactModel>> = _unlabeledPeople

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchUnlabeledPeople(force: Boolean = false) {
        if (!force && _unlabeledPeople.value.isNotEmpty()) return
        
        _isLoading.value = true
        _error.value = null
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getUnlabeledPeople()
                _unlabeledPeople.value = response
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun labelPerson(personId: Int, name: String, onComplete: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                RetrofitClient.apiService.labelPerson(personId, LabelRequest(name))
                // Refresh the list after successful labeling
                fetchUnlabeledPeople(force = true)
                CoroutineScope(Dispatchers.Main).launch {
                    onComplete()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to label person"
            }
        }
    }

    fun dismissPerson(personId: Int) {
        _unlabeledPeople.value = _unlabeledPeople.value.filter { it.id != personId }
    }
}
