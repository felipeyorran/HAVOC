package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.SensiGeneratorEngine
import com.example.engine.SensiIntensity
import com.example.engine.WeaponCategory
import com.example.ui.MainViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.SensiSliderCard
import com.example.ui.theme.HavocCrimson
import com.example.ui.theme.HavocGold
import com.example.ui.theme.HavocGreen
import com.example.ui.theme.HavocRedDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val selectedBrand by viewModel.selectedBrand.collectAsState()
    val deviceModelInput by viewModel.deviceModelInput.collectAsState()
    val selectedWeapon by viewModel.selectedWeaponCategory.collectAsState()
    val selectedPlayStyle by viewModel.selectedPlayStyle.collectAsState()

    val isNewScale200 by viewModel.isNewScale200.collectAsState()
    val selectedIntensity by viewModel.selectedIntensity.collectAsState()
    val noDpiMode by viewModel.noDpiMode.collectAsState()
    val activePresetSlot by viewModel.activePresetSlot.collectAsState()

    val geral by viewModel.currentGeral.collectAsState()
    val redDot by viewModel.currentRedDot.collectAsState()
    val mira2x by viewModel.currentMira2x.collectAsState()
    val mira4x by viewModel.currentMira4x.collectAsState()
    val miraAwm by viewModel.currentMiraAwm.collectAsState()
    val olhadinha by viewModel.currentOlhadinha.collectAsState()
    val buttonSize by viewModel.currentButtonSize.collectAsState()
    val recommendedDpi by viewModel.currentRecommendedDpi.collectAsState()
    val pointerSpeed by viewModel.currentPointerSpeed.collectAsState()
    val touchResponse by viewModel.currentTouchResponse.collectAsState()
    val estimatedHsRate by viewModel.currentEstimatedHsRate.collectAsState()
    val notes by viewModel.currentNotes.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var customProfileName by remember { mutableStateOf("") }
    var flickTestScore by remember { mutableStateOf("Toque e arraste para cima") }
    var flickDragOffsetY by remember { mutableFloatStateOf(0f) }

    val currentMaxSliderValue = if (isNewScale200) 200f else 100f

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("generator_screen_content")
    ) {
        // TOP PRESET & DEVICE CARD (Reference UI Style)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("device_header_card"),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(18.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, HavocCrimson.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Top Device Name & Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HavocCrimson.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = HavocCrimson,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = deviceModelInput.ifBlank { "MEU APARELHO" }.uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "CALIBRAÇÃO HAVOC • FF 2026",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = HavocCrimson
                            )
                        }
                    }

                    // Headshot Rate Badge
                    Surface(
                        color = HavocCrimson.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HavocCrimson)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = HavocCrimson,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$estimatedHsRate% CAPA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = HavocCrimson
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Presets Bar (#1, #2, VIP 3, VIP 4, VIP 5)
                Text(
                    text = "PRESETS RÁPIDOS HAVOC:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SensiGeneratorEngine.presetSlots.forEach { slot ->
                        val isSelected = activePresetSlot == slot.slotNumber
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.selectPresetSlot(slot.slotNumber) }
                                .testTag("preset_slot_${slot.slotNumber}"),
                            color = if (isSelected) HavocCrimson else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                            else if (slot.isVip) androidx.compose.foundation.BorderStroke(1.dp, HavocGold.copy(alpha = 0.5f))
                            else null,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = slot.badge,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) Color.White else if (slot.isVip) HavocGold else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = slot.label,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SEÇÃO: ESCALA DE SENSIBILIDADE (NOVA ATUALIZAÇÃO 0-200 VS 0-100)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Escala Free Fire",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = if (isNewScale200) HavocCrimson else MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (isNewScale200) "NOVA ATUALIZAÇÃO (0 - 200)" else "CLÁSSICA (0 - 100)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isNewScale200) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isNewScale200) "Suporta a nova versão do jogo até 200 nas miras" else "Escala antiga tradicional até 100",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = isNewScale200,
                        onClick = { viewModel.setScale200(true) },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔥 Nova Atualização (0 - 200)", fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HavocCrimson,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("scale_200_chip")
                    )

                    FilterChip(
                        selected = !isNewScale200,
                        onClick = { viewModel.setScale200(false) },
                        label = {
                            Text("Clássica (0 - 100)", fontWeight = FontWeight.Bold)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = HavocCrimson,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f).testTag("scale_100_chip")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SEÇÃO: ESCOLHA DE INTENSIDADE DA SENSI (SENSI BAIXA / SENSI ALTA)
        SectionHeader(
            title = "Tipo de Sensibilidade (Baixa / Alta)",
            subtitle = "Escolha o comportamento da sua mira para o seu estilo de puxada",
            icon = Icons.Default.Tune,
            badgeText = selectedIntensity.title
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SensiIntensity.values().forEach { intensity ->
                val isSelected = selectedIntensity == intensity
                val icon = when (intensity) {
                    SensiIntensity.BAIXA -> Icons.Default.GpsFixed
                    SensiIntensity.MEDIA -> Icons.Default.Scale
                    SensiIntensity.ALTA -> Icons.Default.RocketLaunch
                }

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.setIntensity(intensity) }
                        .testTag("intensity_card_${intensity.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) HavocCrimson.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) HavocCrimson else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) HavocCrimson else MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = intensity.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isSelected) HavocCrimson else MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = intensity.tag,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 9.sp,
                            color = if (isSelected) HavocCrimson else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Descrição da intensidade selecionada
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = HavocCrimson,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = selectedIntensity.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Marca do Celular
        SectionHeader(
            title = "1. Marca & Modelo do Aparelho",
            subtitle = "Ajusta o tempo de resposta e a fricção nativa da tela",
            icon = Icons.Default.PhoneAndroid,
            badgeText = selectedBrand
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SensiGeneratorEngine.supportedBrands.forEach { brand ->
                val isSelected = selectedBrand == brand
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.selectedBrand.value = brand
                    },
                    label = { Text(brand, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HavocCrimson,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("brand_chip_${brand.replace(" ", "_")}")
                )
            }
        }

        OutlinedTextField(
            value = deviceModelInput,
            onValueChange = { viewModel.deviceModelInput.value = it },
            label = { Text("Modelo do Celular") },
            placeholder = { Text("Ex: OnePlus Nord CE 5G, Galaxy S23, Redmi Note 12") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .testTag("device_model_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HavocCrimson,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            trailingIcon = {
                TextButton(
                    onClick = {
                        viewModel.deviceModelInput.value = SensiGeneratorEngine.detectDeviceModel()
                        viewModel.selectedBrand.value = SensiGeneratorEngine.detectDeviceBrand()
                    }
                ) {
                    Text("Detectar", fontSize = 11.sp, color = HavocCrimson, fontWeight = FontWeight.Bold)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Tipo de Arma
        SectionHeader(
            title = "2. Categoria de Arma",
            subtitle = "Calibra a proporção exata da mira 2x, 4x e red dot",
            icon = Icons.Default.MilitaryTech,
            badgeText = selectedWeapon.id
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SensiGeneratorEngine.weaponCategories.forEach { weapon ->
                val isSelected = selectedWeapon.id == weapon.id
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedWeaponCategory.value = weapon },
                    label = {
                        Text(weapon.name, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HavocCrimson,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("weapon_chip_${weapon.id}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BOTÃO DE GERAR SENSIBILIDADE
        Button(
            onClick = { viewModel.generateNewSensitivity() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("generate_sensi_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = HavocCrimson,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Icon(imageVector = Icons.Default.FlashOn, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "GERAR SENSI (${selectedIntensity.title.uppercase()} • ATÉ ${currentMaxSliderValue.toInt()})",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SEÇÃO: SLIDERS CALIBRADOS (0 A 200)
        SectionHeader(
            title = "Sensibilidade Calibrada",
            subtitle = "Ajuste fino no jogo Free Fire (Escala até ${currentMaxSliderValue.toInt()})",
            icon = Icons.Default.Tune,
            badgeText = if (isNewScale200) "0 - 200" else "0 - 100"
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SensiSliderCard(
                label = "Geral",
                value = geral,
                onValueChange = { viewModel.currentGeral.value = it },
                subtext = "Movimentação da câmera e subida rápida sem abrir mira",
                maxValue = currentMaxSliderValue,
                testTag = "slider_geral"
            )

            SensiSliderCard(
                label = "Ponto Vermelho (Red Dot)",
                value = redDot,
                onValueChange = { viewModel.currentRedDot.value = it },
                subtext = "Tiro padrão sem mira telescópica (capa colado no peito)",
                maxValue = currentMaxSliderValue,
                testTag = "slider_red_dot"
            )

            SensiSliderCard(
                label = "Mira 2x",
                value = mira2x,
                onValueChange = { viewModel.currentMira2x.value = it },
                subtext = "Combates de média distância (M4A1, SCAR, Thompson, UMP)",
                maxValue = currentMaxSliderValue,
                testTag = "slider_mira_2x"
            )

            SensiSliderCard(
                label = "Mira 4x",
                value = mira4x,
                onValueChange = { viewModel.currentMira4x.value = it },
                subtext = "Combates de longa distância (AK47, Groza, SVD, Woodpecker)",
                maxValue = currentMaxSliderValue,
                testTag = "slider_mira_4x"
            )

            SensiSliderCard(
                label = "Mira AWM",
                value = miraAwm,
                onValueChange = { viewModel.currentMiraAwm.value = it },
                subtext = "Snipers de precisão (AWM, Barrett M82B, Kar98k)",
                maxValue = currentMaxSliderValue,
                testTag = "slider_mira_awm"
            )

            SensiSliderCard(
                label = "Olhadinha (Free Look)",
                value = olhadinha,
                onValueChange = { viewModel.currentOlhadinha.value = it },
                subtext = "Giro 360° veloz para movimentação e visão periférica",
                maxValue = currentMaxSliderValue,
                testTag = "slider_olhadinha"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SEÇÃO: BOTÃO DE DISPARO & DPI
        SectionHeader(
            title = "Tamanho do Botão & DPI",
            subtitle = "Calibração física para a puxada de capa perfeita",
            icon = Icons.Default.TouchApp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Slider do Botão
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tamanho do Botão de Tiro (HUD)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Recomendado para ${selectedIntensity.title}: $buttonSize%",
                            fontSize = 12.sp,
                            color = HavocCrimson,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(
                        color = HavocCrimson,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "$buttonSize%",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Slider(
                    value = buttonSize.toFloat(),
                    onValueChange = { viewModel.currentButtonSize.value = it.toInt() },
                    valueRange = 28f..75f,
                    colors = SliderDefaults.colors(
                        thumbColor = HavocCrimson,
                        activeTrackColor = HavocCrimson
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("slider_button_size")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Simulador do Botão e Teste de Puxada
                Text(
                    text = "Área de Teste de Puxada de Capa (Arraste para Cima):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, HavocCrimson.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    flickTestScore = if (flickDragOffsetY < -80f) {
                                        "🎯 CAPA VERMELHO PERFEITO! Subida de mira ideal."
                                    } else if (flickDragOffsetY < -30f) {
                                        "⚡ Capa Médio. Puxe um pouco mais rápido para subir."
                                    } else {
                                        "⚠️ Tiro no peito. Puxe o dedo com mais força para cima."
                                    }
                                    flickDragOffsetY = 0f
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    flickDragOffsetY += dragAmount.y
                                }
                            )
                        }
                        .testTag("drag_test_area"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val circlePixelSize = (buttonSize * 1.3f).coerceIn(40f, 90f).dp
                        Box(
                            modifier = Modifier
                                .size(circlePixelSize)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        listOf(HavocCrimson, HavocRedDark)
                                    )
                                )
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = flickTestScore,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (flickTestScore.contains("PERFEITO")) HavocGreen else HavocCrimson
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // DPI Card com botão "SEM DPI"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HavocCrimson.copy(alpha = 0.1f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Menor Largura (DPI)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (noDpiMode) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = HavocGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "SEM DPI ATIVADO",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HavocGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (noDpiMode) "$recommendedDpi DPI (Nativa)" else "$recommendedDpi DPI",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = HavocCrimson
                        )
                        Text(
                            text = if (noDpiMode) "Sensibilidade compensada no jogo sem alterar opções de desenvolvedor"
                            else "Opções do Desenvolvedor > Menor Largura",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        // Toggle Sem DPI
                        TextButton(
                            onClick = { viewModel.toggleNoDpiMode() },
                            modifier = Modifier.testTag("toggle_no_dpi_button")
                        ) {
                            Text(
                                text = if (noDpiMode) "Usar DPI" else "NO DPI ✨",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = HavocCrimson
                            )
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("DPI", recommendedDpi.toString())
                                clipboard.setPrimaryClip(clip)
                            },
                            modifier = Modifier.testTag("copy_dpi_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copiar DPI",
                                tint = HavocCrimson
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dicas do Sistema
                Text(
                    text = "⚙️ Configurações Recomendadas do Aparelho:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "• Velocidade do Ponteiro: $pointerSpeed\n• Atraso ao Manter Pressionado: $touchResponse\n• Escalas de Animação: 0.5x",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BOTÕES DE AÇÃO: Salvar Perfil & Copiar Tudo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    val scaleText = if (isNewScale200) "200" else "100"
                    customProfileName = "Sensi ${selectedIntensity.title} • ${selectedWeapon.id} ($scaleText)"
                    showSaveDialog = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("save_profile_button")
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, tint = HavocCrimson)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Salvar Perfil", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val scaleName = if (isNewScale200) "Nova Atualização (0-200)" else "Clássica (0-100)"
                    val fullConfigText = """
                    🔥 HAVOC SENSI PRO - CONFIGURAÇÃO FREE FIRE 🔥
                    • Modelo: $deviceModelInput
                    • Intensidade: ${selectedIntensity.title}
                    • Escala: $scaleName
                    • Categoria: ${selectedWeapon.name}
                    ---------------------------------------
                    • Geral: $geral
                    • Ponto Vermelho: $redDot
                    • Mira 2x: $mira2x
                    • Mira 4x: $mira4x
                    • Mira AWM: $miraAwm
                    • Olhadinha: $olhadinha
                    • Tamanho do Botão: $buttonSize%
                    • DPI: $recommendedDpi ${if (noDpiMode) "(Sem DPI - Nativa)" else ""}
                    • Velocidade do Ponteiro: $pointerSpeed
                    ---------------------------------------
                    Gerado com HAVOC Sensi Pro
                    """.trimIndent()

                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("HAVOC Sensi", fullConfigText)
                    clipboard.setPrimaryClip(clip)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = HavocCrimson,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("copy_all_button")
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copiar Tudo", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Dialog para Salvar Perfil no Room
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Salvar Perfil de Sensibilidade") },
            text = {
                Column {
                    Text(
                        text = "Dê um nome para salvar essa configuração nos seus perfis salvos locais:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customProfileName,
                        onValueChange = { customProfileName = it },
                        label = { Text("Nome do Perfil") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dialog_profile_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentConfiguration(customProfileName)
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HavocCrimson)
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
