package com.example.argus_eye.data.remote.api

import com.example.argus_eye.data.model.ContactModel
import com.example.argus_eye.data.model.InteractionResponse
import com.example.argus_eye.data.model.LabelRequest
import com.example.argus_eye.data.model.LabelResponse
import com.example.argus_eye.data.model.UpdateNotesRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("api/people")
    suspend fun getContacts(): List<ContactModel>

    @GET("api/people/unlabeled")
    suspend fun getUnlabeledPeople(): List<ContactModel>

    @POST("api/people/{id}/label")
    suspend fun labelPerson(
        @Path("id") personId: Int,
        @Body request: LabelRequest
    ): LabelResponse

    @POST("api/people/{id}/notes")
    suspend fun updateNotes(
        @Path("id") personId: Int,
        @Body request: UpdateNotesRequest
    ): ContactModel

    @GET("api/interactions")
    suspend fun getInteractions(): List<InteractionResponse>
}
