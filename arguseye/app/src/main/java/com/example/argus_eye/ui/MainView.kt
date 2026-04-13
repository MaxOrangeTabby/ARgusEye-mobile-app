package com.example.argus_eye.ui

import androidx.activity.compose.BackHandler
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
import com.example.argus_eye.data.model.InteractionResponse
import com.example.argus_eye.data.remote.api.controller.ContactsController
import com.example.argus_eye.data.remote.api.controller.HomeController
import java.util.Stack

enum class Screen {
    Home,
    Contacts,
    Conversations,
    ConversationDetails,
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
    var navigationStack by remember { mutableStateOf(listOf(Screen.Home)) }
    val currentScreen = navigationStack.last()
    
    var selectedContact by remember { mutableStateOf<ContactModel?>(null) }
    var selectedInteraction by remember { mutableStateOf<InteractionResponse?>(null) }
    
    val homeController = remember { HomeController() }
    val contactsController = remember { ContactsController() }
    val conversationController = remember { ConversationHistController() }

    fun navigateTo(screen: Screen) {
        if (screen in listOf(Screen.Home, Screen.Contacts, Screen.Conversations, Screen.You)) {
            navigationStack = listOf(screen)
        } else {
            navigationStack = navigationStack + screen
        }
    }

    fun navigateBack() {
        if (navigationStack.size > 1) {
            navigationStack = navigationStack.dropLast(1)
        }
    }

    BackHandler(enabled = navigationStack.size > 1) {
        navigateBack()
    }

    val navigateToContact: (Int) -> Unit = { personId ->
        val contact = contactsController.contacts.value.find { it.id == personId }
        if (contact != null) {
            selectedContact = contact
            navigateTo(Screen.ContactDetails)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (currentScreen != Screen.ContactDetails && currentScreen != Screen.ConversationDetails) {
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
            if (currentScreen != Screen.ContactDetails && currentScreen != Screen.ConversationDetails) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6750A4)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentScreen == Screen.Home,
                        onClick = { navigateTo(Screen.Home) },
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
                        onClick = { navigateTo(Screen.Contacts) },
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
                        selected = currentScreen == Screen.Conversations || currentScreen == Screen.ConversationDetails,
                        onClick = { navigateTo(Screen.Conversations) },
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
                        onClick = { navigateTo(Screen.You) },
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
        val hidePadding = currentScreen == Screen.ContactDetails || currentScreen == Screen.ConversationDetails
        Box(modifier = Modifier.padding(if (hidePadding) PaddingValues(0.dp) else innerPadding)) {
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
                            navigateTo(Screen.ContactDetails)
                        },
                        onRefresh = { contactsController.fetchContacts(force = true) }
                    )
                }
                Screen.ContactDetails -> {
                    selectedContact?.let { contact ->
                        ContactDetailsScreen(
                            contact = contact,
                            contactsController = contactsController,
                            conversationController = conversationController,
                            onBack = { navigateBack() },
                            onInteractionClick = { interaction ->
                                selectedInteraction = interaction
                                navigateTo(Screen.ConversationDetails)
                            }
                        )
                    }
                }
                Screen.Conversations -> {
                    LaunchedEffect(Unit) {
                        conversationController.fetchInteractions()
                        contactsController.fetchContacts()
                    }
                    ConversationListScreen(
                        interactions = conversationController.filteredInteractions.value,
                        isLoading = conversationController.isLoading.value,
                        error = conversationController.error.value,
                        searchQuery = conversationController.searchQuery.value,
                        onSearchQueryChange = { conversationController.searchQuery.value = it },
                        onRefresh = { conversationController.fetchInteractions(force = true) },
                        onViewTranscription = { interaction ->
                            selectedInteraction = interaction
                            navigateTo(Screen.ConversationDetails)
                        },
                        onPersonClick = { personId -> navigateToContact(personId) }
                    )
                }
                Screen.ConversationDetails -> {
                    selectedInteraction?.let { interaction ->
                        TranscriptionDetailScreen(
                            interaction = interaction,
                            onBack = { navigateBack() },
                            onPersonClick = { personId -> navigateToContact(personId) }
                        )
                    }
                }
                Screen.You -> {
                    ProfileScreen(user = user, onLogoutClick = onLogoutClick)
                }
            }
        }
    }
}
