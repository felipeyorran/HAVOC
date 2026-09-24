package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.notifications.TrainingNotificationHelper
import com.example.ui.MainViewModel
import com.example.ui.NavTab
import com.example.ui.components.ProfileDialog
import com.example.ui.screens.FeedbackScreen
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.GuildScreen
import com.example.ui.screens.PerformanceScreen
import com.example.ui.screens.ProfilesScreen
import com.example.ui.theme.HavocCrimson
import com.example.ui.theme.HavocDarkBackground
import com.example.ui.theme.HavocSensiTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup notification channel early
        TrainingNotificationHelper.createNotificationChannel(this)

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            HavocSensiTheme(darkTheme = isDarkMode) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val userSession by viewModel.userSession.collectAsState()
    val allProfiles by viewModel.allProfiles.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showProfileDialog by remember { mutableStateOf(false) }

    // Listen to snackbar messages
    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().testTag("main_scaffold"),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(HavocCrimson),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "HAVOC",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.5.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SENSI",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.5.sp,
                            color = HavocCrimson
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    // Dark / Light theme toggle
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Alternar Modo Noturno",
                            tint = if (isDarkMode) Color(0xFFFFD54F) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Profile / Cloud Sync button
                    IconButton(
                        onClick = { showProfileDialog = true },
                        modifier = Modifier.testTag("profile_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(HavocCrimson.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Perfil Social",
                                tint = HavocCrimson,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // 1. Gerador
                NavigationBarItem(
                    selected = currentTab == NavTab.GENERATOR,
                    onClick = { viewModel.selectTab(NavTab.GENERATOR) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Gerador"
                        )
                    },
                    label = { Text("Gerador", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = HavocCrimson,
                        indicatorColor = HavocCrimson
                    ),
                    modifier = Modifier.testTag("nav_tab_generator")
                )

                // 2. Perfis
                NavigationBarItem(
                    selected = currentTab == NavTab.PROFILES,
                    onClick = { viewModel.selectTab(NavTab.PROFILES) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (allProfiles.isNotEmpty()) {
                                    Badge(
                                        containerColor = HavocCrimson,
                                        contentColor = Color.White
                                    ) {
                                        Text("${allProfiles.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmarks,
                                contentDescription = "Perfis"
                            )
                        }
                    },
                    label = { Text("Perfis", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = HavocCrimson,
                        indicatorColor = HavocCrimson
                    ),
                    modifier = Modifier.testTag("nav_tab_profiles")
                )

                // 3. Desempenho
                NavigationBarItem(
                    selected = currentTab == NavTab.PERFORMANCE,
                    onClick = { viewModel.selectTab(NavTab.PERFORMANCE) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Desempenho"
                        )
                    },
                    label = { Text("Otimizar", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = HavocCrimson,
                        indicatorColor = HavocCrimson
                    ),
                    modifier = Modifier.testTag("nav_tab_performance")
                )

                // 4. Guilda & Chat
                NavigationBarItem(
                    selected = currentTab == NavTab.GUILD,
                    onClick = { viewModel.selectTab(NavTab.GUILD) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Guilda"
                        )
                    },
                    label = { Text("Guilda", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = HavocCrimson,
                        indicatorColor = HavocCrimson
                    ),
                    modifier = Modifier.testTag("nav_tab_guild")
                )

                // 5. Avaliações
                NavigationBarItem(
                    selected = currentTab == NavTab.FEEDBACK,
                    onClick = { viewModel.selectTab(NavTab.FEEDBACK) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = "Avaliações"
                        )
                    },
                    label = { Text("Feedback", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = HavocCrimson,
                        indicatorColor = HavocCrimson
                    ),
                    modifier = Modifier.testTag("nav_tab_feedback")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentTab, label = "tab_crossfade") { tab ->
                when (tab) {
                    NavTab.GENERATOR -> GeneratorScreen(viewModel = viewModel)
                    NavTab.PROFILES -> ProfilesScreen(viewModel = viewModel)
                    NavTab.PERFORMANCE -> PerformanceScreen(viewModel = viewModel)
                    NavTab.GUILD -> GuildScreen(viewModel = viewModel)
                    NavTab.FEEDBACK -> FeedbackScreen(viewModel = viewModel)
                }
            }
        }
    }

    if (showProfileDialog) {
        ProfileDialog(
            viewModel = viewModel,
            onDismiss = { showProfileDialog = false }
        )
    }
}
