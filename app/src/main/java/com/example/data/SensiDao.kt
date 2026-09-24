package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SensiDao {
    // Sensi Profiles
    @Query("SELECT * FROM sensi_profiles ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllProfiles(): Flow<List<SensiProfile>>

    @Query("SELECT * FROM sensi_profiles WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteProfiles(): Flow<List<SensiProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: SensiProfile): Long

    @Update
    suspend fun updateProfile(profile: SensiProfile)

    @Delete
    suspend fun deleteProfile(profile: SensiProfile)

    @Query("DELETE FROM sensi_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Long)

    // Feedback Reviews
    @Query("SELECT * FROM feedback_reviews ORDER BY timestamp DESC")
    fun getAllFeedbacks(): Flow<List<FeedbackReview>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackReview): Long

    @Query("UPDATE feedback_reviews SET likes = likes + 1 WHERE id = :id")
    suspend fun incrementFeedbackLikes(id: Long)

    // Guild Messages
    @Query("SELECT * FROM guild_messages ORDER BY timestamp ASC")
    fun getAllGuildMessages(): Flow<List<GuildMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuildMessage(message: GuildMessage): Long
}
