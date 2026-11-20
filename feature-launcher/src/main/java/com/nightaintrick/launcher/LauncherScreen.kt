package com.nightaintrick.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nightaintrick.core.*

data class GameEntry(val label: String, val pkg: String)

/** Os mesmos pacotes precisam estar em <queries> no AndroidManifest (Android 11+). */
val KnownGames = listOf(
    GameEntry("Free Fire", "com.dts.freefireth"),
    GameEntry("Free Fire MAX", "com.dts.freefiremax"),
    GameEntry("Call of Duty Mobile", "com.activision.callofduty.shooter"),
    GameEntry("PUBG Mobile", "com.tencent.ig")
)

fun launchGame(ctx: Context, g: GameEntry) {
    val intent = ctx.packageManager.getLaunchIntentForPackage(g.pkg)
    try {
        if (intent != null) ctx.startActivity(intent)
        else {
            Toast.makeText(ctx, "${g.label} não está instalado.", Toast.LENGTH_SHORT).show()
            try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${g.pkg}"))) }
            catch (e: ActivityNotFoundException) {
                ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${g.pkg}")))
            }
        }
    } catch (e: Exception) {
        Toast.makeText(ctx, "Não foi possível abrir ${g.label}.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun LauncherScreen() {
    val ctx = LocalContext.current
    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        KnownGames.forEach { g ->
            val installed = ctx.packageManager.getLaunchIntentForPackage(g.pkg) != null
            Panel(g.label) {
                InfoRow("Instalado", if (installed) "Sim" else "Não")
                NightButton(if (installed) "Abrir" else "Ver na Play Store") { launchGame(ctx, g) }
            }
        }
        Note("Para incluir outro jogo, adicione-o em KnownGames e no <queries> do manifesto.")
    }
}
