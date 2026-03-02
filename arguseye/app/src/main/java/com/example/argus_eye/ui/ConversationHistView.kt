package com.example.argus_eye.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.argus_eye.data.model.MainModel
import com.example.argus_eye.data.remote.api.MainController
import com.example.argus_eye.ui.theme.ArguseyeTheme

data class Conversation(
    val uid: String,
    val timestamp: String,
    val participants: List<String>,
    val notes: List<String>
)

@Preview(showBackground = true)
@Composable
fun ConverViewPreview() {
    val model = MainModel("Preview Eye", "MVC Preview")
    val controller = MainController(model)
    ArguseyeTheme {
        MainView(controller = controller)
    }
}
