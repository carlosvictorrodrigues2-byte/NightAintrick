package com.nightaintrick.core

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** Perfil de configuração (valores de sensibilidade que VOCÊ digita manualmente no jogo). */
data class Profile(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val gamePackage: String = "com.dts.freefireth",
    val general: Int = 50, val redDot: Int = 50, val scope2x: Int = 50,
    val scope4x: Int = 50, val awm: Int = 50, val notes: String = ""
)

class ProfileStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("night_profiles", Context.MODE_PRIVATE)

    var activeId: String?
        get() = prefs.getString("active", null)
        set(v) { prefs.edit().putString("active", v).apply() }

    fun all(): List<Profile> = try {
        val arr = JSONArray(prefs.getString("list", "[]"))
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            Profile(o.getString("id"), o.getString("name"), o.optString("pkg"),
                o.optInt("g", 50), o.optInt("r", 50), o.optInt("s2", 50),
                o.optInt("s4", 50), o.optInt("awm", 50), o.optString("n"))
        }
    } catch (e: Exception) { emptyList() }  // dados corrompidos não derrubam o app

    fun save(p: Profile) {
        val list = all().filter { it.id != p.id } + p
        write(list)
    }

    fun delete(id: String) {
        write(all().filter { it.id != id })
        if (activeId == id) activeId = null
    }

    fun active(): Profile? = all().firstOrNull { it.id == activeId }

    private fun write(list: List<Profile>) {
        val arr = JSONArray()
        list.forEach { p ->
            arr.put(JSONObject().put("id", p.id).put("name", p.name).put("pkg", p.gamePackage)
                .put("g", p.general).put("r", p.redDot).put("s2", p.scope2x)
                .put("s4", p.scope4x).put("awm", p.awm).put("n", p.notes))
        }
        prefs.edit().putString("list", arr.toString()).apply()
    }
}
