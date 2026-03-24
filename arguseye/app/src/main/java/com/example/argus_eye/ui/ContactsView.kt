package com.example.argus_eye.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argus_eye.data.model.ContactModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    contactModels: List<ContactModel>,
    isLoading: Boolean = false,
    error: String? = null,
    onRetry: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredContacts = contactModels.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }
    
    val groupedContacts = filteredContacts.groupBy { it.name.firstOrNull()?.uppercaseChar() ?: ' ' }
    val alphabet = ('A'..'Z').toList()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Enter a name, e.g. John", color = Color(0xFFD1D9E0)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF5A6978)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFD1D9E0),
                focusedBorderColor = Color(0xFF6750A4)
            ),
            singleLine = true
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
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
                Row(modifier = Modifier.fillMaxSize()) {
                    // Contacts List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        groupedContacts.keys.sorted().forEach { initial ->
                            item {
                                Column {
                                    Text(
                                        text = initial.toString(),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF5A6978)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                    HorizontalDivider(color = Color(0xFFD1D9E0), thickness = 1.dp)
                                }
                            }
                            items(groupedContacts[initial]!!) { contact ->
                                ContactItem(contact)
                            }
                        }
                    }

                    // Alphabet Index
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(24.dp)
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        alphabet.forEach { letter ->
                            Text(
                                text = letter.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF5A6978)
                                ),
                                modifier = Modifier.clickable {
                                    val sortedKeys = groupedContacts.keys.sorted()
                                    val key = sortedKeys.find { it >= letter }
                                    if (key != null) {
                                        val index = sortedKeys.indexOf(key)
                                        var itemIndex = 0
                                        for (i in 0 until index) {
                                            itemIndex += 1 // Header
                                            itemIndex += groupedContacts[sortedKeys[i]]!!.size
                                        }
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(itemIndex)
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactItem(contactModel: ContactModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle contact click */ }
            .padding(horizontal = 32.dp, vertical = 12.dp)
    ) {
        Text(
            text = contactModel.name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5A6978)
            )
        )
    }
}
