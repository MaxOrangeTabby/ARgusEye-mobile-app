package com.example.argus_eye.data.model

import android.util.Base64
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockedStatic
import org.mockito.Mockito
import org.mockito.Mockito.mockStatic
import org.mockito.junit.MockitoJUnitRunner

class ContactModelTest {

    @Test
    fun `profileImageBytes returns decoded bytes when thumbnail is valid base64`() {
        // Since android.util.Base64 is part of the Android SDK, it's not available in local JVM tests.
        // We must mock it.
        val input = "SGVsbG8=" // "Hello" in Base64
        val expected = "Hello".toByteArray()
        
        mockStatic(Base64::class.java).use { mockedBase64 ->
            mockedBase64.`when`<ByteArray> { Base64.decode(input, Base64.DEFAULT) }
                .thenReturn(expected)
            
            val contact = ContactModel(id = 1, name = "Test", thumbnail = input)
            
            assertArrayEquals(expected, contact.profileImageBytes)
        }
    }

    @Test
    fun `profileImageBytes returns null when thumbnail is null`() {
        val contact = ContactModel(id = 1, name = "Test", thumbnail = null)
        assertNull(contact.profileImageBytes)
    }

    @Test
    fun `profileImageBytes returns null when decoding throws exception`() {
        val input = "invalid-base64"
        
        mockStatic(Base64::class.java).use { mockedBase64 ->
            mockedBase64.`when`<ByteArray> { Base64.decode(anyString(), anyInt()) }
                .thenThrow(RuntimeException("Decoding failed"))
            
            val contact = ContactModel(id = 1, name = "Test", thumbnail = input)
            
            assertNull(contact.profileImageBytes)
        }
    }
}
