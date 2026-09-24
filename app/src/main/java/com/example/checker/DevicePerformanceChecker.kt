package com.example.checker

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.BatteryManager
import android.os.Build
import android.view.Display
import android.view.WindowManager
import kotlin.math.roundToInt

data class DeviceSpecs(
    val brand: String,
    val model: String,
    val androidVersion: String,
    val apiLevel: Int,
    val screenResolution: String,
    val refreshRate: Float,
    val densityDpi: Int,
    val totalRamGb: Float,
    val availableRamGb: Float,
    val ramUsagePercent: Int,
    val batteryPercent: Int,
    val batteryTemperatureCelsius: Float,
    val isCharging: Boolean,
    val compatibilityScore: Int, // 0 - 100
    val maxFpsCapability: String, // "60 FPS", "90 FPS Ultra", "120 FPS Max"
    val optimizationStatus: String,
    val recommendations: List<OptimizationTip>
)

data class OptimizationTip(
    val title: String,
    val description: String,
    val isCritical: Boolean,
    val actionLabel: String
)

object DevicePerformanceChecker {

    fun getDeviceSpecs(context: Context): DeviceSpecs {
        // Memory info
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRamGb = memoryInfo.totalMem / (1024f * 1024f * 1024f)
        val availableRamGb = memoryInfo.availMem / (1024f * 1024f * 1024f)
        val usedRamGb = totalRamGb - availableRamGb
        val ramUsagePercent = if (totalRamGb > 0) ((usedRamGb / totalRamGb) * 100).toInt() else 50

        // Display info
        var refreshRate = 60f
        try {
            val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
            val display = displayManager?.getDisplay(Display.DEFAULT_DISPLAY)
            if (display != null) {
                refreshRate = display.refreshRate
            } else {
                @Suppress("DEPRECATION")
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
                refreshRate = windowManager?.defaultDisplay?.refreshRate ?: 60f
            }
        } catch (_: Throwable) {
            refreshRate = 60f
        }

        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val densityDpi = metrics.densityDpi

        // Battery info
        val batteryIntent = try {
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        } catch (_: Throwable) {
            null
        }
        val batteryLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 85
        val batteryScale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPercent = if (batteryLevel >= 0 && batteryScale > 0) {
            ((batteryLevel / batteryScale.toFloat()) * 100).toInt()
        } else 85

        val rawTemp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 320) ?: 320
        val batteryTempCelsius = rawTemp / 10f
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        // Free Fire Compatibility Score Calculation
        var score = 65
        if (totalRamGb >= 6.0f) score += 15 else if (totalRamGb >= 4.0f) score += 10
        if (refreshRate >= 115f) score += 15 else if (refreshRate >= 85f) score += 10
        if (ramUsagePercent < 75) score += 5
        if (batteryTempCelsius < 38f) score += 5
        val finalScore = score.coerceIn(50, 100)

        val fpsCapability = when {
            refreshRate >= 115f && totalRamGb >= 6f -> "120 FPS (Ultra Alto Desempenho)"
            refreshRate >= 85f || totalRamGb >= 4f -> "90 FPS (Alto Desempenho Suave)"
            else -> "60 FPS (Padrão Estável)"
        }

        val optimizationStatus = when {
            finalScore >= 90 -> "Sistema Pronto para X1 & Competitivo (Status Excelente)"
            finalScore >= 75 -> "Bom Desempenho (Otimizações Recomendadas)"
            else -> "Atenção: Alto Uso de Recursos (Ajustes Necessários)"
        }

        val tips = mutableListOf<OptimizationTip>()

        if (refreshRate < 90f) {
            tips.add(
                OptimizationTip(
                    title = "Taxa de Atualização da Tela",
                    description = "Verifique em 'Configurações > Tela' se há opção de 90Hz ou 120Hz para maior fluidez e resposta ao puxar a mira.",
                    isCritical = false,
                    actionLabel = "Configurar Tela"
                )
            )
        }

        if (ramUsagePercent > 70) {
            tips.add(
                OptimizationTip(
                    title = "Uso Elevado de Memória RAM ($ramUsagePercent%)",
                    description = "Feche apps em segundo plano como redes sociais antes de abrir o Free Fire para evitar engasgos e quedas de FPS.",
                    isCritical = true,
                    actionLabel = "Liberar RAM"
                )
            )
        }

        tips.add(
            OptimizationTip(
                title = "Escalas de Animação do Desenvolvedor",
                description = "Mude 'Escala de janela, transição e duração' para 0.5x nas Opções do Desenvolvedor para resposta de toque instantânea.",
                isCritical = false,
                actionLabel = "Guia DPI"
            )
        )

        tips.add(
            OptimizationTip(
                title = "Velocidade do Ponteiro & Atraso de Toque",
                description = "Configure a 'Velocidade do ponteiro' no máximo e 'Tempo de permanência do clique' como curto nas opções de acessibilidade.",
                isCritical = false,
                actionLabel = "Dica Pro"
            )
        )

        if (batteryTempCelsius > 39f) {
            tips.add(
                OptimizationTip(
                    title = "Temperatura do Dispositivo (${batteryTempCelsius.roundToInt()}°C)",
                    description = "O aparelho está aquecendo. Evite jogar com o carregador conectado para evitar thermal throttling.",
                    isCritical = true,
                    actionLabel = "Resfriar"
                )
            )
        }

        return DeviceSpecs(
            brand = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE ?: "14",
            apiLevel = Build.VERSION.SDK_INT,
            screenResolution = "${width}x${height} px",
            refreshRate = refreshRate,
            densityDpi = densityDpi,
            totalRamGb = totalRamGb,
            availableRamGb = availableRamGb,
            ramUsagePercent = ramUsagePercent,
            batteryPercent = batteryPercent,
            batteryTemperatureCelsius = batteryTempCelsius,
            isCharging = isCharging,
            compatibilityScore = finalScore,
            maxFpsCapability = fpsCapability,
            optimizationStatus = optimizationStatus,
            recommendations = tips
        )
    }
}
