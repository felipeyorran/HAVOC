package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.UserSession
import com.example.auth.UserSessionManager
import com.example.checker.DevicePerformanceChecker
import com.example.checker.DeviceSpecs
import com.example.data.AppDatabase
import com.example.data.FeedbackReview
import com.example.data.GuildMessage
import com.example.data.SensiProfile
import com.example.data.SensiRepository
import com.example.engine.SensiGeneratorEngine
import com.example.engine.SensiIntensity
import com.example.engine.WeaponCategory
import com.example.notifications.TrainingNotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SensiRepository
    private val sessionManager = UserSessionManager(application)

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = SensiRepository(database.sensiDao())
    }

    // Navigation & Theme
    private val _currentTab = MutableStateFlow(NavTab.GENERATOR)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Snackbar notifications
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // User Session & Cloud Sync
    val userSession: StateFlow<UserSession> = sessionManager.session

    // Profiles from Room
    val allProfiles: StateFlow<List<SensiProfile>> = repository.allProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteProfiles: StateFlow<List<SensiProfile>> = repository.favoriteProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feedback from Room
    val allFeedbacks: StateFlow<List<FeedbackReview>> = repository.allFeedbacks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Guild Messages from Room
    val allGuildMessages: StateFlow<List<GuildMessage>> = repository.allGuildMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Device Specs
    private val _deviceSpecs = MutableStateFlow<DeviceSpecs?>(null)
    val deviceSpecs: StateFlow<DeviceSpecs?> = _deviceSpecs.asStateFlow()

    // Generator UI State
    val selectedBrand = MutableStateFlow(SensiGeneratorEngine.detectDeviceBrand())
    val deviceModelInput = MutableStateFlow(SensiGeneratorEngine.detectDeviceModel())
    val selectedWeaponCategory = MutableStateFlow(SensiGeneratorEngine.weaponCategories.first())
    val selectedPlayStyle = MutableStateFlow(SensiGeneratorEngine.playStyles.first())
    val isNewScale200 = MutableStateFlow(true) // Nova Atualização (0 - 200)
    val selectedIntensity = MutableStateFlow(SensiIntensity.ALTA) // Sensi Alta por padrão
    val noDpiMode = MutableStateFlow(false)
    val activePresetSlot = MutableStateFlow(1)

    // Active Sensi Controls
    val currentGeral = MutableStateFlow(185)
    val currentRedDot = MutableStateFlow(172)
    val currentMira2x = MutableStateFlow(180)
    val currentMira4x = MutableStateFlow(170)
    val currentMiraAwm = MutableStateFlow(78)
    val currentOlhadinha = MutableStateFlow(175)
    val currentButtonSize = MutableStateFlow(42)
    val currentRecommendedDpi = MutableStateFlow(640)
    val currentPointerSpeed = MutableStateFlow("Máxima (10/10) + Resposta rápida")
    val currentTouchResponse = MutableStateFlow("Curto (0.5s)")
    val currentEstimatedHsRate = MutableStateFlow(94)
    val currentNotes = MutableStateFlow("Puxada rápida em 'J' invertido para curta e média distância.")

    init {
        refreshDeviceSpecs()
        // Generate initial preset based on detected device
        generateNewSensitivity()
    }

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun setScale200(enabled: Boolean) {
        if (isNewScale200.value != enabled) {
            isNewScale200.value = enabled
            if (enabled) {
                // Escala de 100 para 200
                currentGeral.value = (currentGeral.value * 2).coerceIn(0, 200)
                currentRedDot.value = (currentRedDot.value * 2).coerceIn(0, 200)
                currentMira2x.value = (currentMira2x.value * 2).coerceIn(0, 200)
                currentMira4x.value = (currentMira4x.value * 2).coerceIn(0, 200)
                currentMiraAwm.value = (currentMiraAwm.value * 2).coerceIn(0, 200)
                currentOlhadinha.value = (currentOlhadinha.value * 2).coerceIn(0, 200)
            } else {
                // Escala de 200 para 100
                currentGeral.value = (currentGeral.value / 2).coerceIn(0, 100)
                currentRedDot.value = (currentRedDot.value / 2).coerceIn(0, 100)
                currentMira2x.value = (currentMira2x.value / 2).coerceIn(0, 100)
                currentMira4x.value = (currentMira4x.value / 2).coerceIn(0, 100)
                currentMiraAwm.value = (currentMiraAwm.value / 2).coerceIn(0, 100)
                currentOlhadinha.value = (currentOlhadinha.value / 2).coerceIn(0, 100)
            }
            viewModelScope.launch {
                _snackbarMessage.emit(
                    if (enabled) "🚀 Escala Nova Atualização (0 - 200) ativada!"
                    else "⚡ Escala Clássica (0 - 100) ativada!"
                )
            }
        }
    }

    fun setIntensity(intensity: SensiIntensity) {
        selectedIntensity.value = intensity
        generateNewSensitivity()
    }

    fun toggleNoDpiMode() {
        noDpiMode.value = !noDpiMode.value
        generateNewSensitivity()
    }

    fun selectPresetSlot(slot: Int) {
        activePresetSlot.value = slot
        when (slot) {
            1 -> {
                selectedIntensity.value = SensiIntensity.ALTA
                noDpiMode.value = false
            }
            2 -> {
                selectedIntensity.value = SensiIntensity.MEDIA
                noDpiMode.value = false
            }
            3 -> {
                selectedIntensity.value = SensiIntensity.ALTA
                noDpiMode.value = false
            }
            4 -> {
                selectedIntensity.value = SensiIntensity.BAIXA
                noDpiMode.value = false
            }
            5 -> {
                selectedIntensity.value = SensiIntensity.ALTA
                noDpiMode.value = true
            }
        }
        generateNewSensitivity()
    }

    fun refreshDeviceSpecs() {
        viewModelScope.launch {
            try {
                val specs = DevicePerformanceChecker.getDeviceSpecs(getApplication())
                _deviceSpecs.value = specs
            } catch (e: Exception) {
                // Keep default or fallback
            }
        }
    }

    fun generateNewSensitivity() {
        val specs = _deviceSpecs.value
        val profile = SensiGeneratorEngine.generateSensitivity(
            brand = selectedBrand.value,
            deviceModel = deviceModelInput.value,
            weaponCategory = selectedWeaponCategory.value,
            playStyle = selectedPlayStyle.value,
            sensiIntensity = selectedIntensity.value,
            isNewScale200 = isNewScale200.value,
            noDpiMode = noDpiMode.value,
            currentDpi = specs?.densityDpi ?: 392,
            screenRefreshRate = specs?.refreshRate ?: 60f
        )

        currentGeral.value = profile.geral
        currentRedDot.value = profile.redDot
        currentMira2x.value = profile.mira2x
        currentMira4x.value = profile.mira4x
        currentMiraAwm.value = profile.miraAwm
        currentOlhadinha.value = profile.olhadinha
        currentButtonSize.value = profile.buttonSize
        currentRecommendedDpi.value = profile.recommendedDpi
        currentPointerSpeed.value = profile.pointerSpeed
        currentTouchResponse.value = profile.touchResponseTime
        currentEstimatedHsRate.value = profile.headshotRateEstimated
        currentNotes.value = profile.notes

        viewModelScope.launch {
            val scaleLabel = if (isNewScale200.value) "200" else "100"
            _snackbarMessage.emit("🔥 ${selectedIntensity.value.title} gerada para ${selectedWeaponCategory.value.id} (Escala $scaleLabel)!")
        }
    }

    fun saveCurrentConfiguration(customName: String? = null) {
        viewModelScope.launch {
            val name = if (!customName.isNullOrBlank()) {
                customName
            } else {
                "Sensi ${selectedWeaponCategory.value.id} • ${selectedBrand.value}"
            }

            val profile = SensiProfile(
                profileName = name,
                brand = selectedBrand.value,
                deviceModel = deviceModelInput.value,
                weaponType = selectedWeaponCategory.value.name,
                geral = currentGeral.value,
                redDot = currentRedDot.value,
                mira2x = currentMira2x.value,
                mira4x = currentMira4x.value,
                miraAwm = currentMiraAwm.value,
                olhadinha = currentOlhadinha.value,
                buttonSize = currentButtonSize.value,
                recommendedDpi = currentRecommendedDpi.value,
                defaultDpi = _deviceSpecs.value?.densityDpi ?: 392,
                pointerSpeed = currentPointerSpeed.value,
                touchResponseTime = currentTouchResponse.value,
                headshotRateEstimated = currentEstimatedHsRate.value,
                notes = currentNotes.value
            )
            repository.saveProfile(profile)
            _snackbarMessage.emit("✅ Perfil '$name' salvo com sucesso!")
        }
    }

    fun loadProfile(profile: SensiProfile) {
        selectedBrand.value = profile.brand
        deviceModelInput.value = profile.deviceModel
        isNewScale200.value = (profile.geral > 100 || profile.redDot > 100 || profile.mira2x > 100)
        currentGeral.value = profile.geral
        currentRedDot.value = profile.redDot
        currentMira2x.value = profile.mira2x
        currentMira4x.value = profile.mira4x
        currentMiraAwm.value = profile.miraAwm
        currentOlhadinha.value = profile.olhadinha
        currentButtonSize.value = profile.buttonSize
        currentRecommendedDpi.value = profile.recommendedDpi
        currentPointerSpeed.value = profile.pointerSpeed
        currentTouchResponse.value = profile.touchResponseTime
        currentEstimatedHsRate.value = profile.headshotRateEstimated
        currentNotes.value = profile.notes

        _currentTab.value = NavTab.GENERATOR
        viewModelScope.launch {
            _snackbarMessage.emit("📋 Perfil '${profile.profileName}' carregado no gerador!")
        }
    }

    fun toggleFavoriteProfile(profile: SensiProfile) {
        viewModelScope.launch {
            repository.toggleFavorite(profile)
        }
    }

    fun deleteProfile(profile: SensiProfile) {
        viewModelScope.launch {
            repository.deleteProfile(profile)
            _snackbarMessage.emit("🗑️ Perfil removido.")
        }
    }

    // Community Feedback
    fun submitFeedback(
        author: String,
        brand: String,
        deviceModel: String,
        weaponType: String,
        rating: Float,
        effectiveness: String,
        comment: String
    ) {
        viewModelScope.launch {
            val feedback = FeedbackReview(
                authorName = if (author.isNotBlank()) author else userSession.value.nickname,
                brand = brand,
                deviceModel = deviceModel,
                weaponType = weaponType,
                rating = rating,
                headshotEffectiveness = effectiveness,
                comment = comment
            )
            repository.addFeedback(feedback)
            _snackbarMessage.emit("⭐ Obrigado! Sua avaliação foi publicada.")
        }
    }

    fun likeFeedback(id: Long) {
        viewModelScope.launch {
            repository.likeFeedback(id)
        }
    }

    // Guild Chat
    fun sendGuildChatMessage(text: String, isTactical: Boolean = false) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val message = GuildMessage(
                senderName = userSession.value.nickname,
                senderRole = "Membro Pro",
                message = text.trim(),
                isTacticalAlert = isTactical
            )
            repository.sendGuildMessage(message)
        }
    }

    // Social Login & Cloud Sync
    fun performSocialLogin(provider: String, nickname: String, ffId: String) {
        sessionManager.socialLogin(provider, nickname, ffId)
        viewModelScope.launch {
            _snackbarMessage.emit("🎮 Conectado com sucesso via $provider!")
        }
    }

    fun syncDataWithCloud() {
        viewModelScope.launch {
            sessionManager.setSyncing(true)
            _snackbarMessage.emit("☁️ Sincronizando perfis com a nuvem...")
            delay(1500)
            val currentCount = allProfiles.value.size
            sessionManager.setSyncing(false, updatedProfilesCount = currentCount)
            _snackbarMessage.emit("✅ $currentCount perfis sincronizados com sucesso!")
        }
    }

    // Training Reminders
    fun triggerInstantTrainingNotification() {
        TrainingNotificationHelper.sendInstantNotification(
            getApplication(),
            title = "🎯 HAVOC Sensi: Sessão de Treino de Capa",
            message = "Entre no Modo Treinamento do Free Fire! Sensi: Geral ${currentGeral.value}, Botão ${currentButtonSize.value}%, DPI ${currentRecommendedDpi.value}."
        )
        viewModelScope.launch {
            _snackbarMessage.emit("🔔 Notificação enviada para a barra do aparelho!")
        }
    }

    fun scheduleTrainingReminder(hour: Int, minute: Int, focusText: String) {
        TrainingNotificationHelper.scheduleDailyReminder(
            getApplication(),
            hour = hour,
            minute = minute,
            title = "🔥 Treino Agendado HAVOC Sensi",
            message = "Foco de hoje: $focusText. Pratique a subida de mira agora para garantir os capas!"
        )
        viewModelScope.launch {
            val formattedTime = String.format("%02d:%02d", hour, minute)
            _snackbarMessage.emit("⏰ Lembrete diário agendado para $formattedTime!")
        }
    }
}

enum class NavTab(val title: String) {
    GENERATOR("Gerador"),
    PROFILES("Perfis"),
    PERFORMANCE("Desempenho"),
    GUILD("Guilda"),
    FEEDBACK("Avaliações")
}
