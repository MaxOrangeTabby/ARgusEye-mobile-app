package com.example.argus_eye.data.remote.api.controller

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.argus_eye.data.model.ContactModel
import com.example.argus_eye.data.model.UpdateNotesRequest
import com.example.argus_eye.data.remote.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ContactsController(
    private val ioContext: CoroutineContext = Dispatchers.IO,
    private val mainContext: CoroutineContext = Dispatchers.Main
) {
    private val _contacts = mutableStateOf<List<ContactModel>>(emptyList())
    val contacts: State<List<ContactModel>> = _contacts

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchContacts(force: Boolean = false) {
        if (!force && _contacts.value.isNotEmpty()) return

        _isLoading.value = true
        _error.value = null
        CoroutineScope(ioContext).launch {
            try {
                val response = RetrofitClient.apiService.getContacts()
                _contacts.value = response.sortedBy { it.name }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateNotes(contactId: Int, notes: String, onSuccess: (ContactModel) -> Unit) {
        _isLoading.value = true
        _error.value = null
        CoroutineScope(ioContext).launch {
            try {
                val updatedContact = RetrofitClient.apiService.updateNotes(
                    contactId,
                    UpdateNotesRequest(notes)
                )
                // Update the local list if necessary
                val currentList = _contacts.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == contactId }
                if (index != -1) {
                    currentList[index] = updatedContact
                    _contacts.value = currentList
                }
                
                launch(mainContext) {
                    onSuccess(updatedContact)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update notes"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
