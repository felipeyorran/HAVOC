package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.HavocCrimson
import com.example.ui.theme.HavocGold
import com.example.ui.theme.HavocGreen
import com.example.ui.theme.HavocRedDark
import kotlin.math.roundToInt

@Composable
fun PerformanceScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val deviceSpecs by viewModel.deviceSpecs.collectAsState()
    val scrollState = rememberScrollState()

    // Interactive checklist state
    val checkedTasks = remember {
        mutableStateMapOf(
            0 to true,
            1 to false,
            2 to true,
            3 to false,
            4 to true
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("performance_screen_content")
    ) {
        SectionHeader(
            title = "Verificador de Desempenho",
            subtitle = "Diagnóstico do hardware do aparelho para jogabilidade de alto nível no Free Fire",
            icon = Icons.Default.Speed,
            badgeText = "DIAGNÓSTICO REAL"
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (deviceSpecs == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Lendo especificações do sistema...")
            }
        } else {
            val specs = deviceSpecs!!

            // Score Banner Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, HavocCrimson.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Score de Compatibilidade FF",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = specs.optimizationStatus,
                                fontSize = 12.sp,
                                color = if (specs.compatibilityScore >= 80) HavocGreen else HavocGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Circular Score
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(HavocCrimson, HavocRedDark)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${specs.compatibilityScore}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "/100",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val animatedProgress by animateFloatAsState(
                        targetValue = specs.compatibilityScore / 100f,
                        label = "score_progress"
                    )
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = HavocCrimson,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = HavocCrimson.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = HavocCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Capacidade do Sistema: ${specs.maxFpsCapability}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HavocCrimson
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hardware Specs Grid
            Text(
                text = "Telemetria & Especificações do Aparelho",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Taxa de Tela",
                    value = "${specs.refreshRate.roundToInt()} Hz",
                    sub = if (specs.refreshRate >= 90f) "Fluidez Máxima" else "Padrão 60Hz",
                    icon = Icons.Default.Tv,
                    highlight = specs.refreshRate >= 90f,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Memória RAM",
                    value = "${String.format("%.1f", specs.availableRamGb)} GB Livres",
                    sub = "Total: ${String.format("%.1f", specs.totalRamGb)} GB (${specs.ramUsagePercent}% em uso)",
                    icon = Icons.Default.Memory,
                    highlight = specs.ramUsagePercent < 75,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricCard(
                    title = "Bateria & Térmico",
                    value = "${specs.batteryTemperatureCelsius.roundToInt()} °C",
                    sub = "${specs.batteryPercent}% ${if (specs.isCharging) "(Carregando)" else ""}",
                    icon = Icons.Default.Thermostat,
                    highlight = specs.batteryTemperatureCelsius < 38f,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Resolução / DPI",
                    value = "${specs.densityDpi} DPI",
                    sub = specs.screenResolution,
                    icon = Icons.Default.Speed,
                    highlight = false,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Optimization Recommendations
            Text(
                text = "Checklist de Otimização do Sistema para Free Fire",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Marque as ações realizadas para reduzir input lag e eliminar travamentos:",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            val checklistItems = listOf(
                "Ajustar Escalas de Animação para 0.5x nas Opções do Desenvolvedor (toque mais rápido)" to "Aumenta resposta de toque",
                "Limpar memória RAM e fechar apps em segundo plano antes de jogar" to "Previne quedas bruscas de FPS",
                "Ativar taxa de atualização máxima da tela (90Hz ou 120Hz)" to "Mira desliza com maior precisão",
                "Desativar 'Economia de Bateria' durante partidas ranqueadas" to "Evita limitação de CPU/GPU",
                "Configurar a sensibilidade de toque nas opções de Acessibilidade" to "Melhora resposta ao arrastar o botão"
            )

            checklistItems.forEachIndexed { index, item ->
                val isChecked = checkedTasks[index] == true
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isChecked) HavocGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checkedTasks[index] = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = HavocGreen,
                                checkmarkColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.first,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = item.second,
                                fontSize = 10.sp,
                                color = if (isChecked) HavocGreen else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Refresh Diagnostic Button
            Button(
                onClick = { viewModel.refreshDeviceSpecs() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("refresh_specs_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = HavocCrimson)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Recalibrar & Atualizar Diagnóstico", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    sub: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (highlight) HavocGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (highlight) HavocGreen else HavocCrimson,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = sub,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
