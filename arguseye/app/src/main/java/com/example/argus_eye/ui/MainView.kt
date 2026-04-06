package com.example.argus_eye.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.argus_eye.data.remote.api.MainController
import com.example.argus_eye.data.model.ContactModel
import com.google.firebase.auth.FirebaseUser
import com.example.argus_eye.controller.ConversationHistController
import com.example.argus_eye.data.remote.api.controller.ContactsController
import com.example.argus_eye.data.remote.api.controller.HomeController

enum class Screen {
    Home,
    Contacts,
    Conversations,
    You,
    ContactDetails
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
    var selectedContact by remember { mutableStateOf<ContactModel?>(null) }
    
    val homeController = remember { HomeController() }
    val contactsController = remember { ContactsController() }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (currentScreen != Screen.ContactDetails) {
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
            }
        },
        bottomBar = {
            if (currentScreen != Screen.ContactDetails) {
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
                        selected = currentScreen == Screen.Contacts || currentScreen == Screen.ContactDetails,
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
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(if (currentScreen == Screen.ContactDetails) PaddingValues(0.dp) else innerPadding)) {
            when (currentScreen) {
                Screen.Home -> {
                    LaunchedEffect(Unit) {
                        homeController.fetchUnlabeledPeople()
                    }
                    HomeScreen(
                        unlabeledPeople = homeController.unlabeledPeople.value,
                        isLoading = homeController.isLoading.value,
                        error = homeController.error.value,
                        user = user,
                        onRetry = { homeController.fetchUnlabeledPeople(force = true) },
                        onLabel = { id, name -> homeController.labelPerson(id, name) {} },
                        onDismiss = { id -> homeController.dismissPerson(id) },
                        onRefresh = { homeController.fetchUnlabeledPeople(force = true) }
                    )
                }
                Screen.Contacts -> {
                    LaunchedEffect(Unit) {
                        contactsController.fetchContacts()
                    }
                    ContactsScreen(
                        contactModels = contactsController.contacts.value,
                        isLoading = contactsController.isLoading.value,
                        error = contactsController.error.value,
                        onRetry = { contactsController.fetchContacts(force = true) },
                        onContactClick = { contact ->
                            selectedContact = contact
                            currentScreen = Screen.ContactDetails
                        },
                        onRefresh = { contactsController.fetchContacts(force = true) }
                    )
                }
                Screen.ContactDetails -> {
                    selectedContact?.let { contact ->
                        ContactDetailsScreen(
                            contact = contact,
                            contactsController = contactsController,
                            onBack = { currentScreen = Screen.Contacts }
                        )
                    }
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
