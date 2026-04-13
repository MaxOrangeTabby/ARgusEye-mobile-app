package com.example.argus_eye.data.remote.api

import com.example.argus_eye.data.model.LabelRequest
import com.example.argus_eye.data.model.UpdateNotesRequest
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    // --- Success Tests ---

    @Test
    fun `getContacts returns list of contacts on success`() = runTest {
        val jsonResponse = """
            [
                {
                    "person_id": 1,
                    "name": "John Doe",
                    "thumbnail": null,
                    "is_labeled": true,
                    "embedding_count": 5,
                    "notes": "Test notes",
                    "created_at": "2023-10-27T10:00:00Z",
                    "last_seen": "2023-10-27T11:00:00Z"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val contacts = apiService.getContacts()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/api/people/labeled", recordedRequest.path)
        assertEquals(1, contacts.size)
        assertEquals("John Doe", contacts[0].name)
    }

    @Test
    fun `getUnlabeledPeople returns list of contacts on success`() = runTest {
        val jsonResponse = """
            [
                {
                    "person_id": 3,
                    "name": "Unknown Person",
                    "is_labeled": false
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val people = apiService.getUnlabeledPeople()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/api/people/unlabeled", recordedRequest.path)
        assertEquals(1, people.size)
        assertEquals(false, people[0].isLabeled)
    }

    @Test
    fun `labelPerson sends POST request and returns success response`() = runTest {
        val personId = 123
        val labelRequest = LabelRequest(name = "New Name")
        val jsonResponse = """
            {
                "person_id": 123,
                "name": "New Name",
                "is_labeled": true,
                "action": "labeled",
                "details": "Success"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val response = apiService.labelPerson(personId, labelRequest)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/api/people/123/label", recordedRequest.path)
        assertEquals(personId, response.personId)
        assertEquals("New Name", response.name)
        assertEquals(true, response.isLabeled)
    }

    @Test
    fun `updateNotes sends POST request and returns updated contact`() = runTest {
        val personId = 1
        val updateRequest = UpdateNotesRequest(notes = "Updated note content")
        val jsonResponse = """
            {
                "person_id": 1,
                "name": "John Doe",
                "notes": "Updated note content",
                "is_labeled": true
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val contact = apiService.updateNotes(personId, updateRequest)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/api/people/1/notes", recordedRequest.path)
        assertEquals("Updated note content", contact.notes)
    }

    @Test
    fun `getInteractions returns list of interactions`() = runTest {
        val jsonResponse = """
            [
                {
                    "interaction_id": 10,
                    "person_id": 1,
                    "person_name": "John Doe",
                    "timestamp": "2023-10-27T12:00:00Z",
                    "transcript": "Hello there",
                    "context": "Meeting"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val interactions = apiService.getInteractions()

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("GET", recordedRequest.method)
        assertEquals("/api/interactions/labeled", recordedRequest.path)
        assertEquals(1, interactions.size)
        assertEquals("Hello there", interactions[0].transcript)
    }

    // --- Error Scenario Tests ---

    @Test(expected = HttpException::class)
    fun `getContacts throws exception on 404 error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        apiService.getContacts()
    }

    @Test(expected = HttpException::class)
    fun `getContacts throws exception on 500 error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        apiService.getContacts()
    }

    @Test
    fun `getContacts returns empty list when API returns empty list`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("[]"))
        val contacts = apiService.getContacts()
        assertTrue(contacts.isEmpty())
    }

    @Test(expected = Exception::class)
    fun `getContacts throws exception on malformed json`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("{ malformed }"))
        apiService.getContacts()
    }

    @Test
    fun `labelPerson verifies request body content`() = runTest {
        val personId = 456
        val labelRequest = LabelRequest(name = "Target Name")
        val jsonResponse = """
            {
                "person_id": 456,
                "name": "Target Name",
                "is_labeled": true,
                "action": "labeled"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        apiService.labelPerson(personId, labelRequest)

        val recordedRequest = mockWebServer.takeRequest()
        val bodyText = recordedRequest.body.readUtf8()
        assertTrue(bodyText.contains("\"name\":\"Target Name\""))
    }
}
