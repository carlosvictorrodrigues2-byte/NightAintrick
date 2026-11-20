package com.nightaintrick.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nightaintrick.boost.BoostScreen
import com.nightaintrick.config.ConfigScreen
import com.nightaintrick.config.NoRecoilScreen
import com.nightaintrick.core.*
import com.nightaintrick.device.DeviceReader
import com.nightaintrick.device.DeviceScreen
import com.nightaintrick.dpi.DpiScreen
import com.nightaintrick.launcher.LauncherScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NightTheme { App() } }
    }
}

@Composable
fun App() {
    val ctx = LocalContext.current
    val store = remember { ProfileStore(ctx) }
    val tabs = listOf("NIGHT", "BOOST", "NO RECOIL", "DPI", "DEVICE", "CONFIG", "LAUNCHER")
    var sel by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = NightColors.Bg,
        topBar = {
            Column(Modifier.statusBarsPadding()) {
                Text("NIGHT AINTRICK", color = NightColors.Accent, fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp, modifier = Modifier.padding(start = 16.dp, top = 12.dp))
                ScrollableTabRow(sel, containerColor = NightColors.Bg, contentColor = NightColors.Accent2, edgePadding = 8.dp) {
                    tabs.forEachIndexed { i, t -> Tab(sel == i, { sel = i }, text = { Text(t) }) }
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad)) {
            when (sel) {
                0 -> Home(store) { sel = it }
                1 -> BoostScreen()
                2 -> key(sel) { NoRecoilScreen(store) }
                3 -> DpiScreen()
                4 -> DeviceScreen()
                5 -> ConfigScreen(store)
                else -> LauncherScreen()
            }
        }
    }
}

@Composable
fun Home(store: ProfileStore, go: (Int) -> Unit) {
    val ctx = LocalContext.current
    val s = remember { DeviceReader.read(ctx) }
    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        Panel("Resumo") {
            InfoRow("RAM livre", "${s.ramAvailMb} / ${s.ramTotalMb} MB")
            InfoRow("Bateria", s.batteryPct?.let { "$it%" } ?: "—")
            InfoRow("Temperatura", s.batteryTempC?.let { "%.1f °C".format(it) } ?: "—")
            InfoRow("Perfil ativo", store.active()?.name ?: "Nenhum")
        }
        Panel("Atalhos") {
            NightButton("Preparar para jogar (Boost)") { go(1) }
            NightButton("Abrir jogos") { go(6) }
        }
    }
}
