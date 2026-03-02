package com.example.argus_eye.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.argus_eye.controller.MainController
import com.example.argus_eye.model.MainModel
import com.example.argus_eye.ui.theme.ArguseyeTheme

@Composable
fun MainView(controller: MainController, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = controller.getAppName(),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = controller.getAppDescription(),
            style = MaterialTheme.typography.bodyLarge
        )
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
