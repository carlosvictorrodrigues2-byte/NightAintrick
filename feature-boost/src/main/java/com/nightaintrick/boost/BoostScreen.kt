package com.nightaintrick.boost

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.nightaintrick.core.*

@Composable
fun BoostScreen() {
    val ctx = LocalContext.current
    val view = LocalView.current
    val nm = remember { ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    val pm = remember { ctx.getSystemService(Context.POWER_SERVICE) as PowerManager }
    val am = remember { ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }

    var tick by remember { mutableIntStateOf(0) }  // força releitura do status
    var keepOn by remember { mutableStateOf(false) }
    val granted = remember(tick) { nm.isNotificationPolicyAccessGranted }
    val dnd = remember(tick) { nm.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL }
    val ramFree = remember(tick) { ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }.availMem / 1048576 }

    DisposableEffect(keepOn) { view.keepScreenOn = keepOn; onDispose { view.keepScreenOn = false } }

    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        Panel("Não perturbe durante o jogo") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Silenciar notificações", color = NightColors.Text)
                Switch(checked = dnd, onCheckedChange = { on ->
                    if (!granted) {
                        Toast.makeText(ctx, "Conceda o acesso ao Não perturbar para este app.", Toast.LENGTH_LONG).show()
                        ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
                    } else runCatching {
                        nm.setInterruptionFilter(
                            if (on) NotificationManager.INTERRUPTION_FILTER_PRIORITY
                            else NotificationManager.INTERRUPTION_FILTER_ALL)
                        tick++
                    }.onFailure { Toast.makeText(ctx, "Não foi possível alterar.", Toast.LENGTH_SHORT).show() }
                })
            }
            InfoRow("Acesso concedido", if (granted) "Sim" else "Não")
            Note("Permissão especial: abre a tela do sistema onde você libera o app manualmente.")
        }
        Panel("Tela") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Manter tela ligada", color = NightColors.Text)
                Switch(checked = keepOn, onCheckedChange = { keepOn = it })
            }
            Note("Só vale enquanto o NIGHT AINTRICK está aberto.")
        }
        Panel("Recursos") {
            InfoRow("RAM livre", "$ramFree MB")
            InfoRow("Economia de bateria", if (pm.isPowerSaveMode) "Ligada" else "Desligada")
            NightButton("Atualizar") { tick++ }
            NightButton("Abrir economia de bateria") {
                runCatching { ctx.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)) }
            }
            Note("O Android não permite que apps comuns encerrem outros apps. Para liberar RAM, feche-os pelos recentes.")
        }
    }
}
