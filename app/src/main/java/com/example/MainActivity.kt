package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.AppDatabase
import com.example.data.repository.FitnessRepository
import com.example.ui.screens.ActiveWorkoutScreen
import com.example.ui.screens.AiCoachScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ExerciseGuideScreen
import com.example.ui.screens.ProfileMemoryScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonLime
import com.example.ui.theme.RoastRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel

enum class NavigationScreen(val title: String, val icon: ImageVector, val tag: String) {
    DASHBOARD("Home", Icons.Default.Home, "nav_dashboard"),
    AI_COACH("AI Coach", Icons.Default.Psychology, "nav_ai_coach"),
    ACTIVE_WORKOUT("Workout", Icons.Default.FitnessCenter, "nav_active_workout"),
    PROGRESS("Progress", Icons.Default.Timeline, "nav_progress"),
    GUIDE("Guides", Icons.Default.MenuBook, "nav_guide"),
    PROFILE("Memory", Icons.Default.Person, "nav_profile")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val database = remember { AppDatabase.getDatabase(context) }
                val repository = remember { FitnessRepository(database) }
                val viewModel: FitnessViewModel = viewModel(
                    factory = FitnessViewModel.provideFactory(repository)
                )

                MainAppScaffold(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScaffold(viewModel: FitnessViewModel) {
    var currentScreen by remember { mutableStateOf(NavigationScreen.DASHBOARD) }
    var initialAiPrompt by remember { mutableStateOf<String?>(null) }

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val activeWorkoutState by viewModel.activeWorkoutState.collectAsStateWithLifecycle()
    val isRoastOn = userProfile?.roastModeEnabled ?: true

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBg,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("bottom_nav_bar"),
                containerColor = DarkSurfaceCard,
                tonalElevation = 8.dp
            ) {
                NavigationScreen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    val isWorkoutScreen = screen == NavigationScreen.ACTIVE_WORKOUT
                    val isAiScreen = screen == NavigationScreen.AI_COACH

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (screen == NavigationScreen.AI_COACH) {
                                initialAiPrompt = null
                            }
                            currentScreen = screen
                        },
                        modifier = Modifier.testTag(screen.tag),
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (isWorkoutScreen && activeWorkoutState.isActive) {
                                        Badge(
                                            containerColor = NeonLime,
                                            modifier = Modifier.size(8.dp)
                                        )
                                    } else if (isAiScreen && isRoastOn) {
                                        Badge(
                                            containerColor = RoastRed,
                                            modifier = Modifier.size(6.dp)
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isAiScreen && isRoastOn) Icons.Default.LocalFireDepartment else screen.icon,
                                    contentDescription = screen.title,
                                    tint = when {
                                        isSelected -> FlameOrange
                                        isAiScreen && isRoastOn -> RoastRed
                                        isWorkoutScreen && activeWorkoutState.isActive -> NeonLime
                                        else -> TextMuted
                                    }
                                )
                            }
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) FlameOrange else TextSecondary
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = FlameOrange.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            when (currentScreen) {
                NavigationScreen.DASHBOARD -> DashboardScreen(
                    viewModel = viewModel,
                    onStartWorkout = { routine, isManual ->
                        viewModel.startWorkout(routine, isManual)
                        currentScreen = NavigationScreen.ACTIVE_WORKOUT
                    },
                    onNavigateToAiCoach = { prompt ->
                        initialAiPrompt = prompt
                        currentScreen = NavigationScreen.AI_COACH
                    },
                    onNavigateToExerciseGuide = {
                        currentScreen = NavigationScreen.GUIDE
                    }
                )

                NavigationScreen.AI_COACH -> AiCoachScreen(
                    viewModel = viewModel,
                    initialPrompt = initialAiPrompt
                )

                NavigationScreen.ACTIVE_WORKOUT -> ActiveWorkoutScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        currentScreen = NavigationScreen.DASHBOARD
                    }
                )

                NavigationScreen.PROGRESS -> ProgressScreen(
                    viewModel = viewModel
                )

                NavigationScreen.GUIDE -> ExerciseGuideScreen()

                NavigationScreen.PROFILE -> ProfileMemoryScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
