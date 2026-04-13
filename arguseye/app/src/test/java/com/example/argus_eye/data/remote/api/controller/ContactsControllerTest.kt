package com.example.argus_eye.data.remote.api.controller

import com.example.argus_eye.data.model.ContactModel
import com.example.argus_eye.data.remote.api.ApiService
import com.example.argus_eye.data.remote.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
class ContactsControllerTest {

    private val apiService: ApiService = mock()
    private lateinit var controller: ContactsController
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Use reflection to inject mock ApiService into RetrofitClient object
        val apiServiceField: Field = RetrofitClient::class.java.getDeclaredField("_apiService")
        apiServiceField.isAccessible = true
        apiServiceField.set(null, apiService)
        
        controller = ContactsController(ioContext = testDispatcher, mainContext = testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchContacts success updates state and sorts by name`() = runTest {
        val contacts = listOf(
            ContactModel(id = 2, name = "Zebra"),
            ContactModel(id = 1, name = "Apple")
        )
        whenever(apiService.getContacts()).thenReturn(contacts)

        controller.fetchContacts()

        assertEquals(2, controller.contacts.value.size)
        assertEquals("Apple", controller.contacts.value[0].name)
        assertEquals("Zebra", controller.contacts.value[1].name)
        assertFalse(controller.isLoading.value)
        assertEquals(null, controller.error.value)
    }

    @Test
    fun `fetchContacts failure updates error state`() = runTest {
        val errorMessage = "API Error"
        whenever(apiService.getContacts()).thenThrow(RuntimeException(errorMessage))

        controller.fetchContacts()

        assertTrue(controller.contacts.value.isEmpty())
        assertFalse(controller.isLoading.value)
        assertEquals(errorMessage, controller.error.value)
    }

    @Test
    fun `updateNotes success updates local contact list`() = runTest {
        val initialContacts = listOf(ContactModel(id = 1, name = "User", notes = "Old Note"))
        whenever(apiService.getContacts()).thenReturn(initialContacts)
        
        val updatedContact = ContactModel(id = 1, name = "User", notes = "New Note")
        whenever(apiService.updateNotes(any(), any())).thenReturn(updatedContact)

        controller.fetchContacts()
        
        var callbackCalled = false
        controller.updateNotes(1, "New Note") {
            callbackCalled = true
            assertEquals("New Note", it.notes)
        }

        assertTrue(callbackCalled)
        assertEquals("New Note", controller.contacts.value[0].notes)
    }
}
