package com.example.argus_eye.data.model

import com.google.gson.annotations.SerializedName

data class LabelResponse(
    @SerializedName("person_id")
    val personId: Int,
    val name: String,
    @SerializedName("is_labeled")
    val isLabeled: Boolean,
    val action: String,
    val details: String? = null
)
