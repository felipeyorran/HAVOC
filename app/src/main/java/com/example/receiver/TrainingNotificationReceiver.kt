package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.notifications.TrainingNotificationHelper

class TrainingNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "🎯 Hora do Treino HAVOC!"
        val message = intent.getStringExtra("EXTRA_MESSAGE")
            ?: "Participe da sessão de treino de capa para aprimorar sua precisão no Free Fire!"

        TrainingNotificationHelper.sendInstantNotification(context, title, message)
    }
}
