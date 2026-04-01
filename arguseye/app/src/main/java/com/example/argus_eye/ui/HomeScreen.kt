package com.example.argus_eye.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.argus_eye.data.model.ContactModel
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    unlabeledPeople: List<ContactModel>,
    isLoading: Boolean,
    error: String?,
    user: FirebaseUser?,
    onRetry: () -> Unit,
    onLabel: (Int, String) -> Unit,
    onDismiss: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            if (error != null && unlabeledPeople.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Error: $error", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        val displayName = user?.displayName ?: user?.email?.split("@")?.get(0) ?: "User"
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Welcome $displayName!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF5A6978)
                            )
                        )
                    }

                    items(unlabeledPeople, key = { it.id }) { person ->
                        HomeCard(
                            person = person,
                            onLabel = { name -> onLabel(person.id, name) },
                            onDismiss = { onDismiss(person.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HomeCard(
    person: ContactModel,
    onLabel: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<Boolean?>(null) }
    var nameInput by remember { mutableStateOf("") }
    val isEnterEnabled = selectedOption == true || (selectedOption == false && nameInput.isNotBlank())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFFD1D9E0), RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Was your recent conversation with ${person.name}?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5A6978)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Yes Button
                val isYesSelected = selectedOption == true
                Button(
                    onClick = { selectedOption = true },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isYesSelected) Color(0xFF6750A4) else Color.White,
                        contentColor = if (isYesSelected) Color.White else Color(0xFF6750A4)
                    ),
                    border = if (!isYesSelected) ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD1D9E0))) else null,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Yes", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // No Button
                val isNoSelected = selectedOption == false
                Button(
                    onClick = { selectedOption = false },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isNoSelected) Color(0xFF6750A4) else Color.White,
                        contentColor = if (isNoSelected) Color.White else Color(0xFF6750A4)
                    ),
                    border = if (!isNoSelected) ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD1D9E0))) else null,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("No", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.weight(1f))
                person.lastSeen?.let {
                    Text(
                        text = it.split("T")[0], // Simple date display
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5A6978)
                        )
                    )
                }
            }

            if (selectedOption == false) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Enter their name",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5A6978)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    placeholder = { Text("Example: Peter", color = Color(0xFFD1D9E0)) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFD1D9E0)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (isEnterEnabled) {
                                if (selectedOption == true) onLabel(person.name)
                                else onLabel(nameInput)
                            }
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedOption == true) onLabel(person.name)
                    else onLabel(nameInput)
                },
                enabled = isEnterEnabled,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6750A4),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF6750A4).copy(alpha = 0.5f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "ENTER",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
