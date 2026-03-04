package com.example.argus_eye.controller

import com.example.argus_eye.data.model.Conversation

class ConversationHistController {

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
