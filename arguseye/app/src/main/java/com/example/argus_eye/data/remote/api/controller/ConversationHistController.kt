package com.example.argus_eye.controller

import com.example.argus_eye.data.model.Conversation

class ConversationHistController {

    fun getConversations(): List<Conversation> {
        return listOf(
            Conversation(
                uid = "23857128740",
                timestamp = "17:38PM 9/13/2004",
                participants = listOf("Person 1", "Person 2", "Person 3"),
                notes = listOf("NOTE 1", "NOTE 2", "NOTE 3")
            ),
            Conversation(
                uid = "99912345678",
                timestamp = "09:15AM 3/4/2026",
                participants = listOf("Alice", "Bob"),
                notes = listOf("NOTE A", "NOTE B")
            ),
            Conversation(
                uid = "11122233344",
                timestamp = "14:00PM 1/1/2025",
                participants = listOf("Dan", "Eve", "Frank"),
                notes = listOf("Meeting note 1", "Meeting note 2")
            )
        )
    }

    fun getConversation(): Conversation {
        return Conversation(
            uid = "23857128740",
            timestamp = "17:38PM 9/13/2004",
            participants = listOf("Person 1", "Person 2", "Person 3"),
            notes = listOf("NOTE 1", "NOTE 2", "NOTE 3")
        )
    }

    fun getFullTranscription(uid: String): String {
        return "Full transcription for UID: $uid"
    }
}
