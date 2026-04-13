package com.example.argus_eye.controller

import com.example.argus_eye.data.model.InteractionResponse
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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
class ConversationHistControllerTest {

    private val apiService: ApiService = mock()
    private lateinit var controller: ConversationHistController
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Use reflection to inject mock ApiService into RetrofitClient object
        val apiServiceField: Field = RetrofitClient::class.java.getDeclaredField("_apiService")
        apiServiceField.isAccessible = true
        apiServiceField.set(null, apiService)
        
        controller = ConversationHistController(ioContext = testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchInteractions success updates state`() = runTest {
        val interactions = listOf(
            InteractionResponse(
                interactionId = 1,
                personId = 10,
                personName = "Alice",
                timestamp = "2023-10-27T10:00:00Z",
                transcript = "Hello",
                context = "Room"
            )
        )
        whenever(apiService.getInteractions()).thenReturn(interactions)

        controller.fetchInteractions()

        assertEquals(interactions, controller.interactions.value)
        assertFalse(controller.isLoading.value)
        assertEquals(null, controller.error.value)
    }

    @Test
    fun `fetchInteractions failure updates error state`() = runTest {
        val errorMessage = "Network Error"
        whenever(apiService.getInteractions()).thenThrow(RuntimeException(errorMessage))

        controller.fetchInteractions()

        assertTrue(controller.interactions.value.isEmpty())
        assertFalse(controller.isLoading.value)
        assertEquals(errorMessage, controller.error.value)
    }

    @Test
    fun `filteredInteractions filters by person name`() = runTest {
        val interactions = listOf(
            InteractionResponse(1, 10, "Alice", "t1", "hi", "c1"),
            InteractionResponse(2, 11, "Bob", "t2", "bye", "c2")
        )
        whenever(apiService.getInteractions()).thenReturn(interactions)
        controller.fetchInteractions()

        controller.searchQuery.value = "ali"

        assertEquals(1, controller.filteredInteractions.value.size)
        assertEquals("Alice", controller.filteredInteractions.value[0].personName)
    }

    @Test
    fun `filteredInteractions filters by interaction id`() = runTest {
        val interactions = listOf(
            InteractionResponse(123, 10, "Alice", "t1", "hi", "c1"),
            InteractionResponse(456, 11, "Bob", "t2", "bye", "c2")
        )
        whenever(apiService.getInteractions()).thenReturn(interactions)
        controller.fetchInteractions()

        controller.searchQuery.value = "123"

        assertEquals(1, controller.filteredInteractions.value.size)
        assertEquals(123, controller.filteredInteractions.value[0].interactionId)
    }

    @Test
    fun `filteredInteractions returns all when query is empty`() = runTest {
        val interactions = listOf(
            InteractionResponse(1, 10, "Alice", "t1", "hi", "c1"),
            InteractionResponse(2, 11, "Bob", "t2", "bye", "c2")
        )
        whenever(apiService.getInteractions()).thenReturn(interactions)
        controller.fetchInteractions()

        controller.searchQuery.value = ""

        assertEquals(2, controller.filteredInteractions.value.size)
    }
}
