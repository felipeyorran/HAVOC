package com.example.auth

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSession(
    val isLoggedIn: Boolean = true,
    val authProvider: String = "Google Play Games",
    val nickname: String = "HAVOC | K1LL€R",
    val ffId: String = "481903721",
    val rankTier: String = "Mestre ★★★★",
    val kdRate: Float = 4.82f,
    val headshotRate: Float = 81.5f,
    val guildName: String = "HAVOC ESPORTS",
    val cloudProfilesCount: Int = 3,
    val lastSyncTimestamp: Long = System.currentTimeMillis() - 1800000,
    val isSyncing: Boolean = false
)

class UserSessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("havoc_user_prefs", Context.MODE_PRIVATE)

    private val _session = MutableStateFlow(
        UserSession(
            isLoggedIn = prefs.getBoolean("is_logged_in", true),
            authProvider = prefs.getString("auth_provider", "Google Play Games") ?: "Google Play Games",
            nickname = prefs.getString("nickname", "HAVOC | K1LL€R") ?: "HAVOC | K1LL€R",
            ffId = prefs.getString("ff_id", "481903721") ?: "481903721",
            rankTier = prefs.getString("rank_tier", "Mestre ★★★★") ?: "Mestre ★★★★",
            kdRate = prefs.getFloat("kd_rate", 4.82f),
            headshotRate = prefs.getFloat("hs_rate", 81.5f),
            guildName = prefs.getString("guild_name", "HAVOC ESPORTS") ?: "HAVOC ESPORTS",
            cloudProfilesCount = prefs.getInt("cloud_profiles_count", 3),
            lastSyncTimestamp = prefs.getLong("last_sync_time", System.currentTimeMillis() - 3600000)
        )
    )
    val session: StateFlow<UserSession> = _session.asStateFlow()

    fun updateProfile(nickname: String, ffId: String, rankTier: String) {
        prefs.edit()
            .putString("nickname", nickname)
            .putString("ff_id", ffId)
            .putString("rank_tier", rankTier)
            .apply()

        _session.value = _session.value.copy(
            nickname = nickname,
            ffId = ffId,
            rankTier = rankTier
        )
    }

    fun socialLogin(provider: String, nickname: String, ffId: String) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("auth_provider", provider)
            .putString("nickname", nickname)
            .putString("ff_id", ffId)
            .apply()

        _session.value = _session.value.copy(
            isLoggedIn = true,
            authProvider = provider,
            nickname = nickname,
            ffId = ffId
        )
    }

    fun logout() {
        prefs.edit().putBoolean("is_logged_in", false).apply()
        _session.value = _session.value.copy(isLoggedIn = false)
    }

    fun setSyncing(syncing: Boolean, updatedProfilesCount: Int = _session.value.cloudProfilesCount) {
        val now = System.currentTimeMillis()
        if (!syncing) {
            prefs.edit()
                .putLong("last_sync_time", now)
                .putInt("cloud_profiles_count", updatedProfilesCount)
                .apply()
        }
        _session.value = _session.value.copy(
            isSyncing = syncing,
            lastSyncTimestamp = if (!syncing) now else _session.value.lastSyncTimestamp,
            cloudProfilesCount = updatedProfilesCount
        )
    }
}
