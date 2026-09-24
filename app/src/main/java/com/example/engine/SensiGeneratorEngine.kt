package com.example.engine

import android.os.Build
import com.example.data.SensiProfile
import kotlin.random.Random

enum class SensiIntensity(
    val id: String,
    val title: String,
    val tag: String,
    val description: String
) {
    BAIXA(
        id = "BAIXA",
        title = "Sensi Baixa",
        tag = "🎯 CONTROLE",
        description = "Mira firme e estável. Ideal para quem passa da cabeça ou tem tela muito rápida."
    ),
    MEDIA(
        id = "MEDIA",
        title = "Sensi Média",
        tag = "⚖️ EQUILIBRADA",
        description = "Configuração coringa para ranqueada mestre e todas as distâncias de combate."
    ),
    ALTA(
        id = "ALTA",
        title = "Sensi Alta",
        tag = "🚀 RUSH 360",
        description = "Subida rápida de capa, giro 360° veloz e movimentação agressiva para X1."
    )
}

data class PresetSlot(
    val slotNumber: Int,
    val badge: String,
    val label: String,
    val isVip: Boolean = false
)

object SensiGeneratorEngine {

    val supportedBrands = listOf(
        "Samsung",
        "Xiaomi / POCO",
        "Motorola",
        "Apple (iPhone)",
        "Realme",
        "ASUS (ROG)",
        "Infinix",
        "Outro Android"
    )

    val weaponCategories = listOf(
        WeaponCategory(
            id = "SMG",
            name = "SMG (Submetralhadora)",
            description = "MP40, UMP, Thompson, MP5 - Subida rápida de capa e velocidade",
            defaultButton = 44,
            iconName = "speed"
        ),
        WeaponCategory(
            id = "AR",
            name = "AR (Fuzil de Assalto)",
            description = "AK47, SCAR, M4A1, Groza - Controle de recoil e média distância",
            defaultButton = 50,
            iconName = "track_changes"
        ),
        WeaponCategory(
            id = "SHOTGUN",
            name = "Escopeta / Shotgun",
            description = "M1014, M1887 (Bau-Bau), Mag-7 - Puxada vertical agressiva e salto",
            defaultButton = 38,
            iconName = "flash_on"
        ),
        WeaponCategory(
            id = "SNIPER",
            name = "Sniper (Precisão)",
            description = "AWM, Barrett M82B, Kar98k - Troca rápida e micro-ajuste",
            defaultButton = 56,
            iconName = "my_location"
        ),
        WeaponCategory(
            id = "PISTOL",
            name = "Pistola (X1 dos Crias)",
            description = "Desert Eagle, M500 - Tiro único perfeito na cabeça",
            defaultButton = 40,
            iconName = "military_tech"
        ),
        WeaponCategory(
            id = "BALANCED",
            name = "Equilibrada (Todas as Armas)",
            description = "Configuração coringa para ranqueada com qualquer loot",
            defaultButton = 48,
            iconName = "tune"
        )
    )

    val playStyles = listOf(
        "Rush Agressivo (Puxada Rápida)",
        "Equilibrado (Ranqueada Mestre)",
        "Precisão Cirúrgica (Controle)"
    )

    val presetSlots = listOf(
        PresetSlot(1, "1", "Recomendada"),
        PresetSlot(2, "# 2", "Alternativa"),
        PresetSlot(3, "VIP 3", "Sensi Alta 360", isVip = true),
        PresetSlot(4, "VIP 4", "Sensi Baixa Foco", isVip = true),
        PresetSlot(5, "VIP 5", "Sem DPI (Nativa)", isVip = true)
    )

    fun detectDeviceBrand(): String {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer.contains("samsung") -> "Samsung"
            manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains("poco") -> "Xiaomi / POCO"
            manufacturer.contains("motorola") || manufacturer.contains("moto") -> "Motorola"
            manufacturer.contains("apple") -> "Apple (iPhone)"
            manufacturer.contains("realme") -> "Realme"
            manufacturer.contains("asus") -> "ASUS (ROG)"
            manufacturer.contains("infinix") || manufacturer.contains("transsion") -> "Infinix"
            else -> "Outro Android"
        }
    }

    fun detectDeviceModel(): String {
        return "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
    }

    fun generateSensitivity(
        brand: String,
        deviceModel: String,
        weaponCategory: WeaponCategory,
        playStyle: String,
        sensiIntensity: SensiIntensity = SensiIntensity.ALTA,
        isNewScale200: Boolean = true,
        noDpiMode: Boolean = false,
        currentDpi: Int = 392,
        screenRefreshRate: Float = 60f
    ): SensiProfile {
        val rng = Random(System.currentTimeMillis())

        val (rawGeral, rawRedDot, raw2x, raw4x, rawAwm, rawOlhadinha, baseButton) = if (isNewScale200) {
            // Nova Atualização Free Fire (0 - 200)
            when (sensiIntensity) {
                SensiIntensity.ALTA -> {
                    when (weaponCategory.id) {
                        "SMG" -> Tuple7(192, 178, 185, 172, 75, 185, 42)
                        "SHOTGUN" -> Tuple7(198, 188, 192, 182, 85, 195, 36)
                        "AR" -> Tuple7(182, 168, 175, 165, 70, 170, 48)
                        "PISTOL" -> Tuple7(195, 182, 178, 168, 68, 188, 38)
                        "SNIPER" -> Tuple7(176, 160, 155, 145, 95, 160, 52)
                        else -> Tuple7(185, 172, 180, 170, 78, 175, 44)
                    }
                }
                SensiIntensity.MEDIA -> {
                    when (weaponCategory.id) {
                        "SMG" -> Tuple7(165, 148, 152, 142, 60, 150, 46)
                        "SHOTGUN" -> Tuple7(172, 158, 160, 150, 68, 160, 42)
                        "AR" -> Tuple7(158, 142, 145, 138, 55, 140, 50)
                        "PISTOL" -> Tuple7(168, 152, 148, 140, 52, 155, 44)
                        "SNIPER" -> Tuple7(150, 136, 132, 125, 75, 130, 55)
                        else -> Tuple7(160, 145, 148, 138, 58, 145, 48)
                    }
                }
                SensiIntensity.BAIXA -> {
                    when (weaponCategory.id) {
                        "SMG" -> Tuple7(132, 118, 122, 112, 45, 115, 52)
                        "SHOTGUN" -> Tuple7(138, 124, 128, 118, 50, 120, 46)
                        "AR" -> Tuple7(125, 110, 114, 105, 42, 105, 54)
                        "PISTOL" -> Tuple7(135, 120, 118, 110, 40, 118, 48)
                        "SNIPER" -> Tuple7(120, 105, 102, 94, 60, 95, 58)
                        else -> Tuple7(128, 114, 116, 108, 44, 110, 52)
                    }
                }
            }
        } else {
            // Escala Clássica Free Fire (0 - 100)
            when (sensiIntensity) {
                SensiIntensity.ALTA -> {
                    when (weaponCategory.id) {
                        "SMG" -> Tuple7(98, 95, 92, 88, 48, 85, 44)
                        "SHOTGUN" -> Tuple7(100, 98, 95, 90, 52, 92, 38)
                        "AR" -> Tuple7(94, 91, 88, 84, 45, 80, 50)
                        "PISTOL" -> Tuple7(99, 97, 91, 87, 46, 88, 40)
                        "SNIPER" -> Tuple7(90, 85, 82, 78, 58, 75, 54)
                        else -> Tuple7(96, 93, 90, 86, 50, 82, 46)
                    }
                }
                SensiIntensity.MEDIA -> {
                    when (weaponCategory.id) {
                        "SMG" -> Tuple7(88, 82, 80, 76, 40, 72, 48)
                        "SHOTGUN" -> Tuple7(92, 86, 84, 78, 44, 76, 44)
                        "AR" -> Tuple7(84, 78, 76, 72, 38, 68, 52)
                        "PISTOL" -> Tuple7(90, 84, 80, 75, 36, 74, 46)
                        "SNIPER" -> Tuple7(80, 74, 70, 65, 48, 62, 56)
                        else -> Tuple7(85, 80, 78, 72, 40, 70, 50)
                    }
                }
                SensiIntensity.BAIXA -> {
                    when (weaponCategory.id) {
                        "SMG" -> Tuple7(74, 68, 66, 62, 32, 58, 54)
                        "SHOTGUN" -> Tuple7(78, 72, 70, 64, 35, 62, 50)
                        "AR" -> Tuple7(70, 64, 62, 58, 30, 55, 56)
                        "PISTOL" -> Tuple7(75, 70, 65, 60, 28, 60, 52)
                        "SNIPER" -> Tuple7(68, 60, 56, 50, 40, 50, 60)
                        else -> Tuple7(72, 66, 64, 58, 32, 58, 54)
                    }
                }
            }
        }

        // Slight brand and touch sampling calibration
        val brandOffset = when (brand) {
            "Apple (iPhone)" -> if (isNewScale200) 4 else 2
            "Xiaomi / POCO" -> if (isNewScale200) 2 else 1
            "Samsung" -> if (isNewScale200) 0 else 0
            "Motorola" -> if (isNewScale200) 3 else 2
            "ASUS (ROG)" -> if (isNewScale200) -4 else -2
            else -> 0
        }

        // If playing "NO DPI", increase in-game Geral and RedDot slightly to compensate
        val noDpiBoost = if (noDpiMode) (if (isNewScale200) 6 else 3) else 0

        val maxScale = if (isNewScale200) 200 else 100
        val minScale = 0

        val geral = (rawGeral + brandOffset + noDpiBoost + rng.nextInt(-2, 3)).coerceIn(minScale, maxScale)
        val redDot = (rawRedDot + brandOffset + noDpiBoost + rng.nextInt(-2, 3)).coerceIn(minScale, maxScale)
        val mira2x = (raw2x + rng.nextInt(-2, 3)).coerceIn(minScale, maxScale)
        val mira4x = (raw4x + rng.nextInt(-2, 3)).coerceIn(minScale, maxScale)
        val miraAwm = (rawAwm + rng.nextInt(-3, 4)).coerceIn(minScale, maxScale)
        val olhadinha = (rawOlhadinha + rng.nextInt(-3, 4)).coerceIn(minScale, maxScale)
        val buttonSize = (baseButton + rng.nextInt(-1, 2)).coerceIn(28, 68)

        // Recommended DPI calculation
        val recommendedDpi = if (noDpiMode) {
            currentDpi
        } else {
            when (sensiIntensity) {
                SensiIntensity.ALTA -> when (brand) {
                    "Apple (iPhone)" -> 120
                    "Xiaomi / POCO" -> 640
                    "Samsung" -> 620
                    "Motorola" -> 580
                    "ASUS (ROG)" -> 680
                    "Realme" -> 600
                    else -> (currentDpi + 180).coerceIn(520, 720)
                }
                SensiIntensity.MEDIA -> when (brand) {
                    "Apple (iPhone)" -> 100
                    "Xiaomi / POCO" -> 540
                    "Samsung" -> 520
                    "Motorola" -> 480
                    "ASUS (ROG)" -> 560
                    "Realme" -> 500
                    else -> (currentDpi + 100).coerceIn(440, 580)
                }
                SensiIntensity.BAIXA -> when (brand) {
                    "Apple (iPhone)" -> 80
                    "Xiaomi / POCO" -> 440
                    "Samsung" -> 420
                    "Motorola" -> 411
                    "ASUS (ROG)" -> 460
                    "Realme" -> 420
                    else -> currentDpi.coerceIn(360, 440)
                }
            }
        }

        val pointerSpeed = when (sensiIntensity) {
            SensiIntensity.ALTA -> "Máxima (10/10) + Resposta de toque rápida"
            SensiIntensity.MEDIA -> "Equilibrada (8/10 passos)"
            SensiIntensity.BAIXA -> "Controlada (6/10 passos para evitar pular da cabeça)"
        }

        val touchResponseTime = if (screenRefreshRate >= 90f) {
            "Mínimo (0.5s - Resposta rápida ${screenRefreshRate.toInt()}Hz)"
        } else {
            "Curto (0.5s nas Configurações de Acessibilidade)"
        }

        val estimatedHsRate = when (sensiIntensity) {
            SensiIntensity.ALTA -> (88 + rng.nextInt(0, 8)).coerceIn(85, 98)
            SensiIntensity.MEDIA -> (85 + rng.nextInt(0, 7)).coerceIn(82, 94)
            SensiIntensity.BAIXA -> (83 + rng.nextInt(0, 6)).coerceIn(80, 92)
        }

        val scaleTag = if (isNewScale200) "Nova Atualização (200)" else "Clássica (100)"
        val profileName = "Sensi ${weaponCategory.id} • ${sensiIntensity.title} • ${brand.split(" ").first()}"

        val dpiAdvice = if (noDpiMode) {
            "Configuração calibrada SEM DPI ($currentDpi nativa). Não precisa ativar opções de desenvolvedor."
        } else {
            "DPI Recomendada: $recommendedDpi (Subida fluida sem passar do crânio)."
        }

        val notes = when (sensiIntensity) {
            SensiIntensity.ALTA -> "⚡ Sensi Alta (Escala $maxScale): Puxada rápida com arraste curto. Ótima para X1, movimentação rápida e capa colado no salto. $dpiAdvice"
            SensiIntensity.MEDIA -> "⚖️ Sensi Média (Escala $maxScale): Equilíbrio perfeito entre tiro à queima-roupa e média distância. $dpiAdvice"
            SensiIntensity.BAIXA -> "🎯 Sensi Baixa (Escala $maxScale): Foco em firmeza e precisão cirúrgica. A mira trava na cabeça sem passar do capacete. $dpiAdvice"
        }

        return SensiProfile(
            profileName = profileName,
            brand = brand,
            deviceModel = if (deviceModel.isNotBlank()) deviceModel else detectDeviceModel(),
            weaponType = "${weaponCategory.name} [${sensiIntensity.title}]",
            geral = geral,
            redDot = redDot,
            mira2x = mira2x,
            mira4x = mira4x,
            miraAwm = miraAwm,
            olhadinha = olhadinha,
            buttonSize = buttonSize,
            recommendedDpi = recommendedDpi,
            defaultDpi = currentDpi,
            pointerSpeed = pointerSpeed,
            touchResponseTime = touchResponseTime,
            headshotRateEstimated = estimatedHsRate,
            notes = notes
        )
    }

    private data class Tuple7<A, B, C, D, E, F, G>(
        val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G
    )
}

data class WeaponCategory(
    val id: String,
    val name: String,
    val description: String,
    val defaultButton: Int,
    val iconName: String
)
