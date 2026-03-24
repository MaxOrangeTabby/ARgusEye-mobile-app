package com.example.argus_eye.data.remote.api

import com.example.argus_eye.data.model.ContactModel
import retrofit2.http.GET

interface ApiService {
    @GET("api/people")
    suspend fun getContacts(): List<ContactModel>
}
