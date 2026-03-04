package com.example.argus_eye.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argus_eye.controller.ConversationHistController
import com.example.argus_eye.data.model.Conversation

class ConversationHistView : ComponentActivity() {
    private val controller = ConversationHistController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val conversation = controller.getConversation()
        setContent {
            ConversationScreen(
                conversation = conversation,
                onViewTranscription = {
                    val text = controller.getFullTranscription(conversation.uid)
                    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

@Composable
fun ConversationScreen(conversation: Conversation, onViewTranscription: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Conversations",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Divider(
            color = Color(0xFFFFCC00),
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // UID
                Text(
                    text = "UID: ${conversation.uid}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Timestamp
                OutlinedTextField(
                    value = "TIME STAMP: ${conversation.timestamp}",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Participants list with left purple bar
                conversation.participants.forEach { participant ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(20.dp)
                                .padding(end = 0.dp)
                        ) {
                            if (participant == conversation.participants.first()) {
                                Divider(
                                    color = Color(0xFF5C6BC0),
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = participant, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                        .padding(8.dp)
                ) {
                    Column {
                        conversation.notes.forEach { note ->
                            Text(text = "- $note", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Button
                Button(
                    onClick = onViewTranscription,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5C6BC0)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "VIEW FULL TRANSCRIPTION",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConversationScreenPreview() {
    val notes = listOf("NOTE 1", "NOTE 2", "NOTE 3")
    val participants = listOf("Person 1", "Person 2", "Person 3")
    ConversationScreen(
        conversation = Conversation(
            uid = "23857128740",
            timestamp = "17:38PM 9/13/2004",
            participants = participants,
            notes = notes
        ),
        onViewTranscription = {}
    )
}
