package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [SensiProfile::class, FeedbackReview::class, GuildMessage::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sensiDao(): SensiDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "havoc_sensi_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.sensiDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: SensiDao) {
                // Starter Profiles
                dao.insertProfile(
                    SensiProfile(
                        profileName = "HAVOC Pro - X1 MP40 & Desert",
                        brand = "Xiaomi",
                        deviceModel = "Redmi Note 12 / Pro",
                        weaponType = "SMG (MP40 / UMP)",
                        geral = 98,
                        redDot = 96,
                        mira2x = 92,
                        mira4x = 88,
                        miraAwm = 50,
                        olhadinha = 75,
                        buttonSize = 46,
                        recommendedDpi = 640,
                        defaultDpi = 392,
                        pointerSpeed = "Velocidade máxima (10/10)",
                        touchResponseTime = "Curto (0.5s)",
                        headshotRateEstimated = 92,
                        isFavorite = true,
                        notes = "Puxada em 'J' invertido. Perfeita para curta e média distância com mira limpa."
                    )
                )

                dao.insertProfile(
                    SensiProfile(
                        profileName = "Competitivo LBFF - AR 2x/4x",
                        brand = "Samsung",
                        deviceModel = "Galaxy S23 / S24 Ultra",
                        weaponType = "AR (AK47 / SCAR / M4A1)",
                        geral = 94,
                        redDot = 92,
                        mira2x = 90,
                        mira4x = 86,
                        miraAwm = 45,
                        olhadinha = 65,
                        buttonSize = 52,
                        recommendedDpi = 580,
                        defaultDpi = 411,
                        pointerSpeed = "Velocidade média-rápida (8/10)",
                        touchResponseTime = "Curto (0.5s)",
                        headshotRateEstimated = 87,
                        isFavorite = true,
                        notes = "Estabilidade máxima sem tremer a mira no peito do oponente."
                    )
                )

                dao.insertProfile(
                    SensiProfile(
                        profileName = "Instaplayer Shotgun - M1014 / Mag-7",
                        brand = "iPhone",
                        deviceModel = "iPhone 13 / 14 / 15",
                        weaponType = "Escopeta (M1014 / Mag-7)",
                        geral = 100,
                        redDot = 100,
                        mira2x = 95,
                        mira4x = 92,
                        miraAwm = 58,
                        olhadinha = 85,
                        buttonSize = 42,
                        recommendedDpi = 120, // iOS cursor scale
                        defaultDpi = 100,
                        pointerSpeed = "Ciclos 120 / Velocidade 100%",
                        touchResponseTime = "Rápido / 3D Touch",
                        headshotRateEstimated = 94,
                        isFavorite = false,
                        notes = "Puxada rápida com pulo de emulador. Capa vermelho garantido."
                    )
                )

                // Starter Feedbacks
                dao.insertFeedback(
                    FeedbackReview(
                        authorName = "Gabriel 'Nobru' F.",
                        brand = "Samsung",
                        deviceModel = "Galaxy S21 FE",
                        weaponType = "SMG (MP40 / UMP)",
                        rating = 5.0f,
                        headshotEffectiveness = "Puxa muito capa",
                        comment = "A sensibilidade com DPI 600 e botão 48% deixou a MP40 grudando só vermelho! Mudou minha taxa de HS na ranqueada.",
                        likes = 34
                    )
                )

                dao.insertFeedback(
                    FeedbackReview(
                        authorName = "Lucas 'Ghost' FF",
                        brand = "Xiaomi",
                        deviceModel = "POCO X5 Pro 5G",
                        weaponType = "Pistola (X1 Desert)",
                        rating = 4.8f,
                        headshotEffectiveness = "Perfeita pra X1",
                        comment = "Para o X1 dos crias com Desert Eagle essa sensi não passa da cabeça. Recomendo usar a velocidade do ponteiro no máximo.",
                        likes = 21
                    )
                )

                dao.insertFeedback(
                    FeedbackReview(
                        authorName = "Kauã 'SniperGod'",
                        brand = "Motorola",
                        deviceModel = "Moto G84 5G",
                        weaponType = "Sniper (AWM / Barret)",
                        rating = 4.7f,
                        headshotEffectiveness = "Mira não passa da cabeça",
                        comment = "A mira AWM em 48% me deu a precisão exata para fazer a troca rápida de tiro no 4x4.",
                        likes = 15
                    )
                )

                // Starter Guild Messages
                val now = System.currentTimeMillis()
                dao.insertGuildMessage(
                    GuildMessage(
                        senderName = "HAVOC | Zaphod",
                        senderRole = "Líder",
                        message = "Bem-vindos à guilda oficial HAVOC ESPORTS! Treino diário às 20h focado em movimentação e taxa de capa.",
                        isTacticalAlert = true,
                        timestamp = now - 3600000 * 5
                    )
                )
                dao.insertGuildMessage(
                    GuildMessage(
                        senderName = "HAVOC | D4rk",
                        senderRole = "Capitão",
                        message = "Galera, testem a nova configuração de DPI 640 para SMG gerada pelo app, está surreal nos 4x4 apostados.",
                        isTacticalAlert = false,
                        timestamp = now - 3600000 * 2
                    )
                )
                dao.insertGuildMessage(
                    GuildMessage(
                        senderName = "HAVOC | Raven",
                        senderRole = "Membro Pro",
                        message = "Alguém online pra fechar squad na ranqueada Mestre?",
                        isTacticalAlert = false,
                        timestamp = now - 1800000
                    )
                )
            }
        }
    }
}
