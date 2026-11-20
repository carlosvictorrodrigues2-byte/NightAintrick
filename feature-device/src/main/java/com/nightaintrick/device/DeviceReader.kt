package com.nightaintrick.device

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager

data class DeviceSnapshot(
    val model: String, val android: String, val soc: String, val abis: String, val cores: Int,
    val ramTotalMb: Long, val ramAvailMb: Long, val lowMemory: Boolean,
    val batteryPct: Int?, val batteryTempC: Float?, val charging: Boolean, val thermal: String?
)

object DeviceReader {
    fun read(ctx: Context): DeviceSnapshot {
        val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }

        // Intent "sticky": não exige permissão
        val bat = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = bat?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = bat?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val temp = bat?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
            ?.takeIf { it != Int.MIN_VALUE }?.let { it / 10f }
        val status = bat?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1

        val thermal = runCatching {
            if (Build.VERSION.SDK_INT >= 29) {
                val pm = ctx.getSystemService(Context.POWER_SERVICE) as PowerManager
                when (pm.currentThermalStatus) {
                    PowerManager.THERMAL_STATUS_NONE -> "Normal"
                    PowerManager.THERMAL_STATUS_LIGHT -> "Leve"
                    PowerManager.THERMAL_STATUS_MODERATE -> "Moderado"
                    PowerManager.THERMAL_STATUS_SEVERE -> "Severo"
                    PowerManager.THERMAL_STATUS_CRITICAL -> "Crítico"
                    else -> "Emergência"
                }
            } else null
        }.getOrNull()

        val soc = if (Build.VERSION.SDK_INT >= 31 && Build.SOC_MODEL.isNotBlank() && Build.SOC_MODEL != "unknown")
            "${Build.SOC_MANUFACTURER} ${Build.SOC_MODEL}" else Build.HARDWARE

        return DeviceSnapshot(
            model = "${Build.MANUFACTURER} ${Build.MODEL}",
            android = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            soc = soc, abis = Build.SUPPORTED_ABIS.joinToString(), cores = Runtime.getRuntime().availableProcessors(),
            ramTotalMb = mi.totalMem / 1048576, ramAvailMb = mi.availMem / 1048576, lowMemory = mi.lowMemory,
            batteryPct = if (level >= 0 && scale > 0) level * 100 / scale else null,
            batteryTempC = temp,
            charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL,
            thermal = thermal
        )
    }
}
