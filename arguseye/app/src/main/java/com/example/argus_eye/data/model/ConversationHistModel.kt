package com.example.argus_eye.data.model

data class Conversation(
    val uid: String,
    val timestamp: String,
    val participants: List<String>,
    val notes: List<String>
)
