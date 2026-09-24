package com.example.data

import kotlinx.coroutines.flow.Flow

class SensiRepository(private val dao: SensiDao) {
    val allProfiles: Flow<List<SensiProfile>> = dao.getAllProfiles()
    val favoriteProfiles: Flow<List<SensiProfile>> = dao.getFavoriteProfiles()
    val allFeedbacks: Flow<List<FeedbackReview>> = dao.getAllFeedbacks()
    val allGuildMessages: Flow<List<GuildMessage>> = dao.getAllGuildMessages()

    suspend fun saveProfile(profile: SensiProfile): Long {
        return dao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: SensiProfile) {
        dao.updateProfile(profile)
    }

    suspend fun deleteProfile(profile: SensiProfile) {
        dao.deleteProfile(profile)
    }

    suspend fun deleteProfileById(id: Long) {
        dao.deleteProfileById(id)
    }

    suspend fun toggleFavorite(profile: SensiProfile) {
        dao.updateProfile(profile.copy(isFavorite = !profile.isFavorite))
    }

    suspend fun addFeedback(feedback: FeedbackReview): Long {
        return dao.insertFeedback(feedback)
    }

    suspend fun likeFeedback(id: Long) {
        dao.incrementFeedbackLikes(id)
    }

    suspend fun sendGuildMessage(message: GuildMessage): Long {
        return dao.insertGuildMessage(message)
    }
}
