package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "feedback_reviews")
data class FeedbackReview(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorName: String,
    val brand: String,
    val deviceModel: String,
    val weaponType: String,
    val rating: Float, // 1.0 to 5.0
    val headshotEffectiveness: String, // e.g. "Puxa muito capa", "Perfeita pra X1"
    val comment: String,
    val likes: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "guild_messages")
data class GuildMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val senderRole: String, // "Líder", "Capitão", "Membro Pro"
    val message: String,
    val isTacticalAlert: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
