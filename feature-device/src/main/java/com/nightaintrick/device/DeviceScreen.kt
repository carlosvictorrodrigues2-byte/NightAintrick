package com.nightaintrick.device

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nightaintrick.core.*
import kotlinx.coroutines.delay

@Composable
fun DeviceScreen() {
    val ctx = LocalContext.current
    var s by remember { mutableStateOf(DeviceReader.read(ctx)) }
    LaunchedEffect(Unit) { while (true) { delay(3000); s = DeviceReader.read(ctx) } }

    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        Panel("Aparelho") {
            InfoRow("Modelo", s.model); InfoRow("Sistema", s.android)
            InfoRow("Chip", s.soc); InfoRow("Núcleos", s.cores.toString()); InfoRow("ABIs", s.abis)
        }
        Panel("Memória RAM") {
            InfoRow("Total", "${s.ramTotalMb} MB"); InfoRow("Livre", "${s.ramAvailMb} MB")
            InfoRow("Pouca memória", if (s.lowMemory) "Sim" else "Não")
        }
        Panel("Bateria e temperatura") {
            InfoRow("Carga", s.batteryPct?.let { "$it%" } ?: "Indisponível")
            InfoRow("Carregando", if (s.charging) "Sim" else "Não")
            InfoRow("Temperatura da bateria", s.batteryTempC?.let { "%.1f °C".format(it) } ?: "Indisponível")
            InfoRow("Estado térmico", s.thermal ?: "Indisponível (Android < 10)")
            Note("A temperatura da CPU não é exposta a apps comuns; o app mostra a da bateria e o estado térmico do sistema.")
        }
    }
}
