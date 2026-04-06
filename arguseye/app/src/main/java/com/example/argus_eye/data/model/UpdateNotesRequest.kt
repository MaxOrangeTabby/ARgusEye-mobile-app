package com.example.argus_eye.data.model

import com.google.gson.annotations.SerializedName

data class UpdateNotesRequest(
    @SerializedName("notes")
    val notes: String
)
