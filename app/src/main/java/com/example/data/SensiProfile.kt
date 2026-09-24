package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sensi_profiles")
data class SensiProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileName: String,
    val brand: String,
    val deviceModel: String,
    val weaponType: String,
    val geral: Int,
    val redDot: Int,
    val mira2x: Int,
    val mira4x: Int,
    val miraAwm: Int,
    val olhadinha: Int,
    val buttonSize: Int, // e.g. 48%
    val recommendedDpi: Int,
    val defaultDpi: Int,
    val pointerSpeed: String,
    val touchResponseTime: String,
    val headshotRateEstimated: Int, // e.g. 88%
    val isFavorite: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
