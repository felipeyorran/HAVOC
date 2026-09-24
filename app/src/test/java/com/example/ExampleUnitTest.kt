package com.example

import com.example.engine.SensiGeneratorEngine
import com.example.engine.SensiIntensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testSensiGeneratorScale200AndIntensity() {
        val smgWeapon = SensiGeneratorEngine.weaponCategories.first { it.id == "SMG" }

        // Sensi Alta on 200 scale
        val highSensi = SensiGeneratorEngine.generateSensitivity(
            brand = "Samsung",
            deviceModel = "Galaxy S23",
            weaponCategory = smgWeapon,
            playStyle = "Rush Agressivo",
            sensiIntensity = SensiIntensity.ALTA,
            isNewScale200 = true
        )

        assertTrue("Geral should be higher than 150 on scale 200 alta", highSensi.geral in 150..200)
        assertTrue("RedDot should be higher than 130 on scale 200 alta", highSensi.redDot in 130..200)
        assertTrue("Mira 2x should be higher than 130 on scale 200 alta", highSensi.mira2x in 130..200)

        // Sensi Baixa on 200 scale
        val lowSensi = SensiGeneratorEngine.generateSensitivity(
            brand = "Samsung",
            deviceModel = "Galaxy S23",
            weaponCategory = smgWeapon,
            playStyle = "Precisão Cirúrgica",
            sensiIntensity = SensiIntensity.BAIXA,
            isNewScale200 = true
        )

        assertTrue("Low sensi geral should be lower than high sensi geral", lowSensi.geral < highSensi.geral)
        assertTrue("Low sensi redDot should be lower than high sensi redDot", lowSensi.redDot < highSensi.redDot)

        // Classic scale 100
        val classicSensi = SensiGeneratorEngine.generateSensitivity(
            brand = "Xiaomi / POCO",
            deviceModel = "POCO X5",
            weaponCategory = smgWeapon,
            playStyle = "Equilibrado",
            sensiIntensity = SensiIntensity.MEDIA,
            isNewScale200 = false
        )

        assertTrue("Classic sensi geral should be <= 100", classicSensi.geral <= 100)
        assertTrue("Classic sensi redDot should be <= 100", classicSensi.redDot <= 100)
    }
}
