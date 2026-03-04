package com.example.argus_eye.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.argus_eye.data.remote.api.MainController
import com.example.argus_eye.data.model.MainModel
import com.example.argus_eye.ui.theme.ArguseyeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(controller: MainController, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Home",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF5A6978)
                            )
                        )
                    }
                )
                HorizontalDivider(color = Color(0xFFFFD54F), thickness = 2.dp)
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF6750A4)
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF6750A4),
                        selectedTextColor = Color(0xFF6750A4),
                        unselectedIconColor = Color(0xFF5A6978),
                        unselectedTextColor = Color(0xFF5A6978),
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.People, contentDescription = "Contacts") },
                    label = { Text("Contacts") },
                    selected = false,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF5A6978),
                        unselectedTextColor = Color(0xFF5A6978)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Conversations") },
                    label = { Text("Conversations") },
                    selected = false,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF5A6978),
                        unselectedTextColor = Color(0xFF5A6978)
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "You") },
                    label = { Text("You") },
                    selected = false,
                    onClick = { },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color(0xFF5A6978),
                        unselectedTextColor = Color(0xFF5A6978)
                    )
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome [name]!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A6978)
                    )
                )
            }

            item {
                HomeCard(
                    topic = "fish",
                    name = "[name]",
                    timestamp = "2/23 1:00 PM"
                )
            }

            item {
                HomeCard(
                    topic = "dog",
                    name = "[name]",
                    timestamp = "2/23 12:00 PM"
                )
            }

            item {
                HomeCard(
                    topic = "dog",
                    name = "[name]",
                    timestamp = "2/23 9:00 AM",
                    showNameInput = true
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun HomeCard(
    topic: String,
    name: String,
    timestamp: String,
    showNameInput: Boolean = false
) {
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
                text = "Was your recent conversation about \"$topic\" with $name?",
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
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Yes", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(4.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFD1D9E0))),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("No", color = Color(0xFF6750A4), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A6978)
                    )
                )
            }

            if (showNameInput) {
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
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Example: Peter", color = Color(0xFFD1D9E0)) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFD1D9E0)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    val model = MainModel("Preview Eye", "MVC Preview")
    val controller = MainController(model)
    ArguseyeTheme {
        MainView(controller = controller)
    }
}
