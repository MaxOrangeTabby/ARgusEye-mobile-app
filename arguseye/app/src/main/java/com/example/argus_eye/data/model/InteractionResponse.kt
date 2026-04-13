package com.example.argus_eye.data.model

import com.google.gson.annotations.SerializedName

data class InteractionResponse(
    @SerializedName("interaction_id") val interactionId: Int,
    @SerializedName("person_id") val personId: Int?,
    @SerializedName("person_name") val personName: String?,
    val timestamp: String,
    val transcript: String,
    val context: String
)
