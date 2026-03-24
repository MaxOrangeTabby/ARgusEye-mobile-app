package com.example.argus_eye.data.model

import com.google.gson.annotations.SerializedName

data class ContactModel(
    @SerializedName("person_id")
    val id: Int,
    val name: String,
    @SerializedName("thumbnail_url")
    val profileImageUrl: String? = null,
    @SerializedName("is_labeled")
    val isLabeled: Boolean = false,
    @SerializedName("embedding_count")
    val embeddingCount: Int = 0,
    val notes: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("last_seen")
    val lastSeen: String? = null
)
