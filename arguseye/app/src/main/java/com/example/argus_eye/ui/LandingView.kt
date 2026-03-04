package com.example.argus_eye.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.argus_eye.ui.theme.ArguseyeTheme

@Composable
fun LandingView(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Placeholder for the circular image/icon
        Box(
            modifier = Modifier
                .size(150.dp)
                .border(2.dp, Color.LightGray, CircleShape)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = Color.LightGray
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Welcome to ARgusEye",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            ),
            textAlign = TextAlign.Center
        )

        Text(
            text = "Please Log in to begin",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF37474F)
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))

        // Log in Button
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6750A4)
            ),
            shape = MaterialTheme.shapes.extraSmall
        ) {
            Text(
                text = "Log in",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF6750A4)
            ),
            shape = MaterialTheme.shapes.extraSmall,
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFEADDFF)))
        ) {
            Text(
                text = "Register",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingViewPreview() {
    ArguseyeTheme {
        LandingView(onLoginClick = {}, onRegisterClick = {})
    }
}
