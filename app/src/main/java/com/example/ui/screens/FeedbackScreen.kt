package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FeedbackReview
import com.example.ui.MainViewModel
import com.example.ui.components.RatingStars
import com.example.ui.components.SectionHeader
import com.example.ui.theme.HavocCrimson
import com.example.ui.theme.HavocGold
import com.example.ui.theme.HavocGreen

@Composable
fun FeedbackScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val feedbacks by viewModel.allFeedbacks.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    var showReviewDialog by remember { mutableStateOf(false) }

    // Form states
    var reviewerName by remember { mutableStateOf(userSession.nickname) }
    var selectedBrand by remember { mutableStateOf("Xiaomi") }
    var selectedWeapon by remember { mutableStateOf("SMG (MP40 / UMP)") }
    var userRating by remember { mutableFloatStateOf(5f) }
    var effectiveness by remember { mutableStateOf("Puxa muito capa") }
    var commentText by remember { mutableStateOf("") }

    val effectivenessOptions = listOf(
        "Puxa muito capa",
        "Perfeita pra X1",
        "Mira não passa da cabeça",
        "Recoil super estável",
        "DPI perfeita para 120Hz"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("feedback_screen_content")
    ) {
        SectionHeader(
            title = "Painel de Eficácia & Feedback",
            subtitle = "Avaliações e relatos da comunidade sobre a taxa de capa das sensibilidades geradas",
            icon = Icons.Default.RateReview,
            badgeText = "COMUNIDADE"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Average Rating Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, HavocCrimson.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "4.9",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            RatingStars(rating = 4.9f, starSize = 16)
                            Text(
                                text = "Baseado em centenas de testes",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "96% de eficácia em subida de capa",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = HavocGreen
                    )
                }

                Button(
                    onClick = { showReviewDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = HavocCrimson),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("open_feedback_dialog_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Avaliar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Feedbacks List
        Text(
            text = "Relatos Recentes de Jogadores (${feedbacks.size})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().testTag("feedbacks_list")
        ) {
            items(feedbacks, key = { it.id }) { review ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(HavocCrimson.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = review.authorName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = HavocCrimson,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = review.authorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${review.brand} • ${review.deviceModel}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            RatingStars(rating = review.rating, starSize = 14)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            color = HavocCrimson.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "🎯 ${review.headshotEffectiveness} • ${review.weaponType}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HavocCrimson,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = review.comment,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { viewModel.likeFeedback(review.id) },
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Curtir",
                                    tint = HavocCrimson,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${review.likes} Útil",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog para submeter feedback
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Avaliar Eficácia da Sensibilidade") },
            text = {
                Column {
                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text("Seu Nick / Nome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HavocCrimson)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Sua Nota de Capa (Estrelas):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (i in 1..5) {
                            IconButton(onClick = { userRating = i.toFloat() }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (userRating >= i) HavocGold else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Eficácia Observada:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Column {
                        effectivenessOptions.take(3).forEach { option ->
                            FilterChip(
                                selected = effectiveness == option,
                                onClick = { effectiveness = option },
                                label = { Text(option, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HavocCrimson,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text("Comentário sobre a sensibilidade") },
                        placeholder = { Text("Ex: A mira subiu muito fácil na MP40 com DPI 640...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = HavocCrimson)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            viewModel.submitFeedback(
                                author = reviewerName,
                                brand = selectedBrand,
                                deviceModel = "Celular do Usuário",
                                weaponType = selectedWeapon,
                                rating = userRating,
                                effectiveness = effectiveness,
                                comment = commentText
                            )
                            commentText = ""
                            showReviewDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HavocCrimson)
                ) {
                    Text("Publicar Avaliação")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
