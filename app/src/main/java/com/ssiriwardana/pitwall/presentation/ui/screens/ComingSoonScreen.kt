package com.ssiriwardana.pitwall.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ComingSoonScreen(modifier: Modifier = Modifier, screenName: String = "This") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(
            text = "$screenName screen is coming soon",
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}