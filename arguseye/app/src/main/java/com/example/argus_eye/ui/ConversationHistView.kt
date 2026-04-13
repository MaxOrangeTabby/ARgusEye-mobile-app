package com.example.argus_eye.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argus_eye.data.model.InteractionResponse

@Composable
fun ConversationCard(
    interaction: InteractionResponse,
    onViewTranscription: () -> Unit,
    onPersonClick: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ID: ${interaction.interactionId}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = "TIMESTAMP: ${interaction.timestamp}",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .clickable(enabled = interaction.personId != null) {
                        interaction.personId?.let { onPersonClick(it) }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(20.dp)
                        .background(Color(0xFF5C6BC0))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = interaction.personName ?: "Unknown Person",
                    fontSize = 14.sp,
                    color = if (interaction.personId != null) Color(0xFF5C6BC0) else Color.Black,
                    textDecoration = if (interaction.personId != null) TextDecoration.Underline else TextDecoration.None
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 60.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = interaction.context,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onViewTranscription,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5C6BC0)),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationListScreen(
    interactions: List<InteractionResponse>,
    isLoading: Boolean,
    error: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onRefresh: () -> Unit,
    onViewTranscription: (InteractionResponse) -> Unit,
    onPersonClick: (Int) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search by name or ID") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5C6BC0),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null && interactions.isEmpty()) {
                Text(text = error, color = Color.Red, modifier = Modifier.padding(16.dp))
                Button(onClick = onRefresh) { Text("Retry") }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(interactions) { interaction ->
                        ConversationCard(
                            interaction = interaction,
                            onViewTranscription = { onViewTranscription(interaction) },
                            onPersonClick = onPersonClick
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranscriptionDetailScreen(
    interaction: InteractionResponse,
    onBack: () -> Unit,
    onPersonClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Interaction Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DetailRow(label = "Interaction ID", value = interaction.interactionId.toString())
                    DetailRow(label = "Timestamp", value = interaction.timestamp)
                    DetailRow(
                        label = "Person",
                        value = interaction.personName ?: "Unknown",
                        onClick = interaction.personId?.let { { onPersonClick(it) } },
                        valueColor = if (interaction.personId != null) Color(0xFF5C6BC0) else Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Context:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = interaction.context,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Transcript",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5C6BC0)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 2.dp,
                color = Color(0xFF5C6BC0)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = interaction.transcript,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, onClick: (() -> Unit)? = null, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Text(
            text = value,
            color = valueColor,
            textDecoration = if (onClick != null) TextDecoration.Underline else TextDecoration.None
        )
    }
}
