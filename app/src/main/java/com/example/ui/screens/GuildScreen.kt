package com.example.ui.screens

import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GuildMessage
import com.example.notifications.TrainingNotificationHelper
import com.example.ui.MainViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatMetricBox
import com.example.ui.theme.HavocCrimson
import com.example.ui.theme.HavocGold
import com.example.ui.theme.HavocGreen
import com.example.ui.theme.HavocRedDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GuildScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val guildMessages by viewModel.allGuildMessages.collectAsState()
    val userSession by viewModel.userSession.collectAsState()

    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Chat em Tempo Real, 1: Treinos & Lembretes
    var chatInputText by remember { mutableStateOf("") }
    val chatListState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(guildMessages.size) {
        if (guildMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(guildMessages.size - 1)
        }
    }

    // Permission launcher for POST_NOTIFICATIONS
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.triggerInstantTrainingNotification()
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("guild_screen_content")
    ) {
        // Guild Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, HavocCrimson.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(HavocCrimson, HavocRedDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "HAVOC ESPORTS",
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = HavocCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = "Tag: [HAVOC] • ID: 64920491 • Nível 4 (Top 1%)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatMetricBox(
                        title = "Taxa de Capa",
                        value = "81.2%",
                        subtitle = "Média Guilda",
                        accentColor = HavocCrimson,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricBox(
                        title = "Vitórias",
                        value = "76%",
                        subtitle = "4x4 Apostado",
                        accentColor = HavocGreen,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricBox(
                        title = "K/D Médio",
                        value = "4.85",
                        subtitle = "Ranqueada",
                        accentColor = HavocGold,
                        modifier = Modifier.weight(1f)
                    )
                    StatMetricBox(
                        title = "Membros",
                        value = "48/50",
                        subtitle = "Online: 18",
                        accentColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sub-tabs: Chat em Tempo Real vs Treinos
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = HavocCrimson
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = { Text("Chat da Guilda (${guildMessages.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = { Text("Lembretes de Treino", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedSubTab == 0) {
            // Chat Section
            // Quick Callout Chips
            val quickCallouts = listOf(
                "🎯 X1 dos Crias",
                "🔥 Treino de Capa",
                "🏆 Sala Personalizada Aberta",
                "⚡ Testem a Sensi SMG",
                "⚔️ Bora Ranqueada Squad"
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            ) {
                quickCallouts.forEach { callout ->
                    FilterChip(
                        selected = false,
                        onClick = { viewModel.sendGuildChatMessage(callout, isTactical = true) },
                        label = { Text(callout, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            // Message stream
            LazyColumn(
                state = chatListState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("guild_messages_list")
            ) {
                items(guildMessages, key = { it.id }) { msg ->
                    val isCurrentUser = msg.senderName == userSession.nickname
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val formattedTime = timeFormat.format(Date(msg.timestamp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    msg.isTacticalAlert -> HavocCrimson.copy(alpha = 0.2f)
                                    isCurrentUser -> HavocCrimson
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (msg.isTacticalAlert) HavocCrimson else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.widthIn(max = 280.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = msg.senderName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isCurrentUser) Color.White else HavocCrimson
                                    )
                                    Text(
                                        text = formattedTime,
                                        fontSize = 9.sp,
                                        color = if (isCurrentUser) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = msg.message,
                                    fontSize = 13.sp,
                                    color = if (isCurrentUser) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Chat input field
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = chatInputText,
                    onValueChange = { chatInputText = it },
                    placeholder = { Text("Mensagem tática para a guilda...", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("guild_chat_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HavocCrimson,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (chatInputText.isNotBlank()) {
                            viewModel.sendGuildChatMessage(chatInputText)
                            chatInputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(HavocCrimson)
                        .testTag("send_guild_message_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            // Training Notification Scheduler Tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                SectionHeader(
                    title = "Notificações de Treino",
                    subtitle = "Programe sessões diárias de treino de capa para não perder o ritmo",
                    icon = Icons.Default.Alarm
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🎯 Horários Recomendados de Treino",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val presetSessions = listOf(
                            Triple("Aquecimento Diário", "14:00", "Subida de Capa MP40 & Desert"),
                            Triple("Treino Competitivo Squad", "19:30", "Posicionamento e Mira 2x/4x"),
                            Triple("X1 dos Crias Noturno", "21:30", "Controle de Recoil e Shotgun")
                        )

                        presetSessions.forEach { (name, time, focus) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(text = "Foco: $focus", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Button(
                                    onClick = {
                                        val parts = time.split(":")
                                        val h = parts[0].toInt()
                                        val m = parts[1].toInt()
                                        viewModel.scheduleTrainingReminder(h, m, focus)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = HavocCrimson),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(time, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Test Immediate Notification Button
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HavocCrimson.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, HavocCrimson.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "🔔 Testar Notificação no Sistema",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = HavocCrimson
                        )
                        Text(
                            text = "Envie uma notificação de treino imediata para verificar como ela aparece na barra do seu celular.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    !TrainingNotificationHelper.hasNotificationPermission(context)
                                ) {
                                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.triggerInstantTrainingNotification()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HavocCrimson),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("trigger_test_notification_btn")
                        ) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Disparar Notificação de Teste", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
