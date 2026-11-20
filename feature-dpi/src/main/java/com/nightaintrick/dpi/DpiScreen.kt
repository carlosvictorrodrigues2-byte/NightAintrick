package com.nightaintrick.dpi

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.nightaintrick.core.*

@Composable
fun DpiScreen() {
    val ctx = LocalContext.current
    val dm = ctx.resources.displayMetrics
    val shortSide = minOf(dm.widthPixels, dm.heightPixels)
    var input by remember { mutableStateOf("") }
    val target = input.toIntOrNull()?.takeIf { it in 120..800 }

    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        Panel("Tela atual") {
            InfoRow("Resolução", "${dm.widthPixels} x ${dm.heightPixels} px")
            InfoRow("Densidade (DPI)", dm.densityDpi.toString())
            InfoRow("DPI físico", "%.0f x %.0f".format(dm.xdpi, dm.ydpi))
            InfoRow("Menor largura", "%.0f dp".format(shortSide * 160f / dm.densityDpi))
        }
        Panel("Simular novo DPI") {
            OutlinedTextField(input, { input = it.filter(Char::isDigit).take(3) },
                label = { Text("DPI desejado (120–800)") }, singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            if (input.isNotEmpty() && target == null) Note("Informe um valor entre 120 e 800.")
            target?.let { t ->
                val sw = shortSide * 160f / t
                InfoRow("Menor largura resultante", "%.0f dp".format(sw))
                if (sw < 320f) Note("Abaixo de 320 dp muitos apps quebram o layout. Prefira um DPI menor.")
                Text("adb shell wm density $t", color = NightColors.Accent2)
                Text("adb shell wm density reset", color = NightColors.Mute)
            }
            Note("Apps comuns não podem mudar o DPI. Use os comandos acima via ADB (depuração USB) ou a opção 'Menor largura' nas Opções do desenvolvedor. 'reset' restaura o original.")
        }
        Panel("Atalhos do sistema") {
            NightButton("Configurações de tela") { runCatching { ctx.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS)) } }
            NightButton("Opções do desenvolvedor") { runCatching { ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)) } }
        }
    }
}
