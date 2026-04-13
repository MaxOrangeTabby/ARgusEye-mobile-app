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
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
class HomeControllerTest {

    private val apiService: ApiService = mock()
    private lateinit var controller: HomeController
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Use reflection to inject mock ApiService into RetrofitClient object
        val apiServiceField: Field = RetrofitClient::class.java.getDeclaredField("_apiService")
        apiServiceField.isAccessible = true
        apiServiceField.set(null, apiService)
        
        // Pass the test dispatcher to the controller to ensure immediate execution in tests
        controller = HomeController(ioContext = testDispatcher, mainContext = testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `fetchUnlabeledPeople success updates state`() = runTest {
        val people = listOf(ContactModel(id = 1, name = "Unknown"))
        whenever(apiService.getUnlabeledPeople()).thenReturn(people)

        controller.fetchUnlabeledPeople()
        
        // With UnconfinedTestDispatcher, the coroutine executes immediately up to the first suspension point.
        // Since we mocked the API call to return immediately, the state should be updated.

        assertEquals(people, controller.unlabeledPeople.value)
        assertFalse(controller.isLoading.value)
        assertEquals(null, controller.error.value)
    }

    @Test
    fun `fetchUnlabeledPeople failure updates error state`() = runTest {
        val errorMessage = "Network Error"
        whenever(apiService.getUnlabeledPeople()).thenThrow(RuntimeException(errorMessage))

        controller.fetchUnlabeledPeople()

        assertTrue(controller.unlabeledPeople.value.isEmpty())
        assertFalse(controller.isLoading.value)
        assertEquals(errorMessage, controller.error.value)
    }

    @Test
    fun `dismissPerson removes person from list`() = runTest {
        val people = listOf(
            ContactModel(id = 1, name = "P1"),
            ContactModel(id = 2, name = "P2")
        )
        whenever(apiService.getUnlabeledPeople()).thenReturn(people)
        
        controller.fetchUnlabeledPeople()
        
        assertEquals(2, controller.unlabeledPeople.value.size)

        controller.dismissPerson(1)

        assertEquals(1, controller.unlabeledPeople.value.size)
        assertEquals(2, controller.unlabeledPeople.value[0].id)
    }
}
