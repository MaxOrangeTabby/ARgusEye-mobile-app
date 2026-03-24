package com.example.argus_eye.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.argus_eye.data.remote.api.MainController
import com.example.argus_eye.data.model.MainModel
import com.example.argus_eye.data.model.HomeCardModel
import com.example.argus_eye.ui.theme.ArguseyeTheme
import com.google.firebase.auth.FirebaseUser
import com.example.argus_eye.controller.ConversationHistController
import com.example.argus_eye.data.remote.api.controller.ContactsController

enum class Screen {
    Home,
    Contacts,
    Conversations,
    You
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    controller: MainController,
    user: FirebaseUser?,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf(Screen.Home) }

    val homeCards = remember {
        mutableStateListOf(
            HomeCardModel("fish", "Alice", "2/23 1:00 PM"),
            HomeCardModel("dog", "Bob", "2/23 12:00 PM"),
            HomeCardModel("cats", "Charlie", "2/23 9:00 AM"),
            HomeCardModel("birds", "David", "2/22 4:00 PM"),
            HomeCardModel("lizards", "Eve", "2/22 2:30 PM"),
            HomeCardModel("hamsters", "Frank", "2/22 10:00 AM"),
            HomeCardModel("snakes", "Grace", "2/21 6:00 PM"),
            HomeCardModel("turtles", "Heidi", "2/21 11:00 AM")
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        val title = if (currentScreen == Screen.You) {
                            user?.displayName ?: user?.email?.split("@")?.get(0) ?: "User"
                        } else {
                            currentScreen.name
                        }
                        Text(
                            title,
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
                    selected = currentScreen == Screen.Home,
                    onClick = { currentScreen = Screen.Home },
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
                    selected = currentScreen == Screen.Contacts,
                    onClick = { currentScreen = Screen.Contacts },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF6750A4),
                        selectedTextColor = Color(0xFF6750A4),
                        unselectedIconColor = Color(0xFF5A6978),
                        unselectedTextColor = Color(0xFF5A6978),
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ChatBubble, contentDescription = "Conversations") },
                    label = { Text("Conversations") },
                    selected = currentScreen == Screen.Conversations,
                    onClick = { currentScreen = Screen.Conversations },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF6750A4),
                        selectedTextColor = Color(0xFF6750A4),
                        unselectedIconColor = Color(0xFF5A6978),
                        unselectedTextColor = Color(0xFF5A6978),
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "You") },
                    label = { Text("You") },
                    selected = currentScreen == Screen.You,
                    onClick = { currentScreen = Screen.You },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF6750A4),
                        selectedTextColor = Color(0xFF6750A4),
                        unselectedIconColor = Color(0xFF5A6978),
                        unselectedTextColor = Color(0xFF5A6978),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                Screen.Home -> HomeScreen(homeCards, user)
                Screen.Contacts -> {
                    val contactsController = remember { ContactsController() }
                    LaunchedEffect(Unit) {
                        contactsController.fetchContacts()
                    }
                    ContactsScreen(
                        contactModels = contactsController.contacts.value,
                        isLoading = contactsController.isLoading.value,
                        error = contactsController.error.value,
                        onRetry = { contactsController.fetchContacts() }
                    )
                }
                Screen.Conversations -> {
                    val conversationController = remember { ConversationHistController() }
                    ConversationListScreen(
                        conversations = conversationController.getConversations(),
                        onViewTranscription = { /* Handle transcription view */ }
                    )
                }
                Screen.You -> {
                    ProfileScreen(user = user, onLogoutClick = onLogoutClick)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeCards: MutableList<HomeCardModel>, user: FirebaseUser?) {
    Column {
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

            items(homeCards, key = { it.topic + it.timestamp }) { cardData ->
                HomeCard(
                    data = cardData,
                    onDismiss = { homeCards.remove(cardData) }
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
    data: HomeCardModel,
    onDismiss: () -> Unit
) {
    var selectedOption by remember { mutableStateOf<Boolean?>(false) }
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
                text = "Was your recent conversation about \"${data.topic}\" with ${data.name}?",
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
                Text(
                    text = data.timestamp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5A6978)
                    )
                )
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
                                onDismiss()
                            }
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
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

@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    val model = MainModel("Preview Eye", "MVC Preview")
    val controller = MainController(model)
    ArguseyeTheme {
        MainView(controller = controller, user = null, onLogoutClick = {})
    }
}
