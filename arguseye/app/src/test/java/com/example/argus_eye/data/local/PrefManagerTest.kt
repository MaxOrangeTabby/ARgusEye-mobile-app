package com.example.argus_eye.data.local

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*

class PrefManagerTest {

    private val context: Context = mock()
    private val sharedPreferences: SharedPreferences = mock()
    private val editor: SharedPreferences.Editor = mock()
    private lateinit var prefManager: PrefManager

    @Before
    fun setup() {
        whenever(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)
        whenever(sharedPreferences.edit()).thenReturn(editor)
        whenever(editor.putString(anyString(), anyString())).thenReturn(editor)
        
        prefManager = PrefManager(context)
    }

    @Test
    fun `getBaseUrl returns default URL when no value is saved`() {
        whenever(sharedPreferences.getString(eq("base_url"), any())).thenReturn(null)
        
        val baseUrl = prefManager.getBaseUrl()
        
        assertEquals("http://10.0.2.2:5000/", baseUrl)
    }

    @Test
    fun `getBaseUrl returns saved URL when value exists`() {
        val savedUrl = "http://192.168.1.100:5000/"
        whenever(sharedPreferences.getString(eq("base_url"), any())).thenReturn(savedUrl)
        
        val baseUrl = prefManager.getBaseUrl()
        
        assertEquals(savedUrl, baseUrl)
    }

    @Test
    fun `setBaseUrl adds trailing slash if missing`() {
        val urlWithoutSlash = "http://myserver.com:8000"
        val expectedUrl = "http://myserver.com:8000/"
        
        prefManager.setBaseUrl(urlWithoutSlash)
        
        verify(editor).putString("base_url", expectedUrl)
        verify(editor).apply()
    }

    @Test
    fun `setBaseUrl does not add duplicate trailing slash`() {
        val urlWithSlash = "http://myserver.com:8000/"
        
        prefManager.setBaseUrl(urlWithSlash)
        
        verify(editor).putString("base_url", urlWithSlash)
        verify(editor).apply()
    }
}
