package com.example.argus_eye.data.model

import android.util.Base64
import com.google.gson.annotations.SerializedName

data class ContactModel(
    @SerializedName("person_id")
    val id: Int,
    val name: String,
    @SerializedName("thumbnail")
    val thumbnail: String? = null,
    @SerializedName("is_labeled")
    val isLabeled: Boolean = false,
    @SerializedName("embedding_count")
    val embeddingCount: Int = 0,
    val notes: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("last_seen")
    val lastSeen: String? = null
) {
    val profileImageBytes: ByteArray?
        get() = thumbnail?.let {
            try {
                Base64.decode(it, Base64.DEFAULT)
            } catch (e: Exception) {
                null
            }
        }
}
