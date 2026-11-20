package com.nightaintrick.core

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object NightColors {
    val Bg = Color(0xFF07060F)
    val Panel = Color(0xFF110E24)
    val Line = Color(0xFF2B2352)
    val Accent = Color(0xFF9B5CFF)
    val Accent2 = Color(0xFF35E0FF)
    val Text = Color(0xFFECE8FF)
    val Mute = Color(0xFF8D86B8)
    val Ok = Color(0xFF3DDC97)
}

@Composable
fun NightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = NightColors.Accent, secondary = NightColors.Accent2,
            background = NightColors.Bg, surface = NightColors.Panel,
            onBackground = NightColors.Text, onSurface = NightColors.Text
        ),
        content = content
    )
}

@Composable
fun Panel(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = NightColors.Panel),
        border = BorderStroke(1.dp, NightColors.Line)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, color = NightColors.Accent2, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = NightColors.Mute)
        Text(value, color = NightColors.Text)
    }
}

@Composable
fun NightButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled,
        colors = ButtonDefaults.buttonColors(containerColor = NightColors.Accent)) { Text(text) }
}

@Composable
fun Note(text: String) = Text(text, color = NightColors.Mute, fontSize = 12.sp)
