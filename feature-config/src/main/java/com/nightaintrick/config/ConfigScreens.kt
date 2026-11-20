package com.nightaintrick.config

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.nightaintrick.core.*

/** CONFIG MANAGER: criar, editar, ativar e excluir perfis (salvos no aparelho). */
@Composable
fun ConfigScreen(store: ProfileStore) {
    var list by remember { mutableStateOf(store.all()) }
    var editing by remember { mutableStateOf<Profile?>(null) }
    var active by remember { mutableStateOf(store.activeId) }

    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        NightButton("Novo perfil") { editing = Profile() }
        Spacer(Modifier.height(12.dp))
        if (list.isEmpty()) Note("Nenhum perfil salvo ainda.")
        list.forEach { p ->
            Panel(p.name) {
                InfoRow("Jogo", p.gamePackage)
                InfoRow("Geral / Red dot", "${p.general} / ${p.redDot}")
                InfoRow("2x / 4x / AWM", "${p.scope2x} / ${p.scope4x} / ${p.awm}")
                Row {
                    TextButton({ store.activeId = p.id; active = p.id }) { Text(if (active == p.id) "Ativo" else "Ativar") }
                    TextButton({ editing = p }) { Text("Editar") }
                    TextButton({ store.delete(p.id); list = store.all(); active = store.activeId }) { Text("Excluir") }
                }
            }
        }
    }
    editing?.let { cur ->
        ProfileDialog(cur, onDismiss = { editing = null }) { saved ->
            store.save(saved); list = store.all(); editing = null
        }
    }
}

@Composable
private fun ProfileDialog(p: Profile, onDismiss: () -> Unit, onSave: (Profile) -> Unit) {
    var name by remember(p.id) { mutableStateOf(p.name) }
    var pkg by remember(p.id) { mutableStateOf(p.gamePackage) }
    var g by remember(p.id) { mutableIntStateOf(p.general) }
    var r by remember(p.id) { mutableIntStateOf(p.redDot) }
    var s2 by remember(p.id) { mutableIntStateOf(p.scope2x) }
    var s4 by remember(p.id) { mutableIntStateOf(p.scope4x) }
    var awm by remember(p.id) { mutableIntStateOf(p.awm) }
    var notes by remember(p.id) { mutableStateOf(p.notes) }

    @Composable fun slider(label: String, v: Int, set: (Int) -> Unit) {
        Text("$label: $v")
        Slider(v.toFloat(), { set(it.toInt()) }, valueRange = 0f..100f)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Perfil") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(name, { name = it.take(30) }, label = { Text("Nome") }, singleLine = true)
                OutlinedTextField(pkg, { pkg = it.trim() }, label = { Text("Pacote do jogo") }, singleLine = true)
                slider("Geral", g) { g = it }; slider("Red dot", r) { r = it }
                slider("Mira 2x", s2) { s2 = it }; slider("Mira 4x", s4) { s4 = it }; slider("AWM", awm) { awm = it }
                OutlinedTextField(notes, { notes = it.take(200) }, label = { Text("Notas") })
            }
        },
        confirmButton = {
            TextButton(enabled = name.isNotBlank(), onClick = {
                onSave(p.copy(name = name.trim(), gamePackage = pkg, general = g, redDot = r,
                    scope2x = s2, scope4x = s4, awm = awm, notes = notes))
            }) { Text("Salvar") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar") } }
    )
}

/**
 * NO RECOIL: mostra o perfil ativo para você copiar e digitar nas configurações do jogo.
 * Não altera controles, mira, recuo nem nada dentro do jogo.
 */
@Composable
fun NoRecoilScreen(store: ProfileStore) {
    val ctx = LocalContext.current
    val p = remember { store.active() }
    Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        Panel("Perfil ativo") {
            if (p == null) Note("Nenhum perfil ativo. Crie e ative um na aba CONFIG.")
            else {
                InfoRow("Nome", p.name)
                InfoRow("Geral", p.general.toString()); InfoRow("Red dot", p.redDot.toString())
                InfoRow("Mira 2x", p.scope2x.toString()); InfoRow("Mira 4x", p.scope4x.toString())
                InfoRow("AWM", p.awm.toString())
                if (p.notes.isNotBlank()) Note(p.notes)
                NightButton("Copiar valores") {
                    val txt = "${p.name}: geral ${p.general}, red dot ${p.redDot}, 2x ${p.scope2x}, 4x ${p.scope4x}, AWM ${p.awm}"
                    (ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                        .setPrimaryClip(ClipData.newPlainText("perfil", txt))
                    Toast.makeText(ctx, "Copiado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        Panel("Importante") {
            Note("Esta seção só guarda seus números de sensibilidade. O app não reduz recuo, não mexe na mira e não interage com o jogo.")
        }
    }
}
