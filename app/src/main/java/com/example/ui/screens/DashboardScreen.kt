package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.UserProfileEntity
import com.example.data.routine.DayWorkoutRoutine
import com.example.data.routine.DefaultRoutine
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PrGold
import com.example.ui.theme.RoastRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: FitnessViewModel,
    onStartWorkout: (DayWorkoutRoutine, Boolean) -> Unit,
    onNavigateToAiCoach: (String?) -> Unit,
    onNavigateToExerciseGuide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val personalRecords by viewModel.personalRecords.collectAsStateWithLifecycle()
    val workoutSessions by viewModel.workoutSessions.collectAsStateWithLifecycle()
    val todayRoutine = remember { DefaultRoutine.getTodayRoutine() }
    val allRoutines = remember { DefaultRoutine.getAllWorkouts() }

    var showManualWorkoutChooser by remember { mutableStateOf(false) }

    val completedThisWeekDays = remember(workoutSessions) {
        val now = LocalDate.now()
        val startOfWeek = now.minusDays((now.dayOfWeek.value - 1).toLong())
        workoutSessions.filter { it.isCompleted }.map { it.dateIso }.toSet()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Greeting & Roast Mode Bar
        item {
            HeaderSection(
                userProfile = userProfile,
                onToggleRoast = { viewModel.toggleRoastMode(it) }
            )
        }

        // 2. Today's Scheduled Workout Hero Card
        item {
            TodayWorkoutHeroCard(
                routine = todayRoutine,
                onStartWorkout = { onStartWorkout(todayRoutine, false) },
                onOpenManualChooser = { showManualWorkoutChooser = !showManualWorkoutChooser }
            )
        }

        // Manual Extra Workout Chooser (if toggled)
        if (showManualWorkoutChooser) {
            item {
                ManualWorkoutSelectorCard(
                    routines = allRoutines,
                    onSelectRoutine = { routine ->
                        showManualWorkoutChooser = false
                        onStartWorkout(routine, true)
                    }
                )
            }
        }

        // 3. Weekly Consistency Calendar
        item {
            WeeklyConsistencyCard(
                completedDates = completedThisWeekDays
            )
        }

        // 4. Quick Ask Gym Buddy / Coach Pill Bar
        item {
            QuickAskCoachCard(
                onAsk = { query -> onNavigateToAiCoach(query) }
            )
        }

        // 5. Stored Personal Records Spotlight
        item {
            PrSpotlightCard(
                personalRecords = personalRecords,
                onViewGuide = onNavigateToExerciseGuide
            )
        }
    }
}

@Composable
private fun HeaderSection(
    userProfile: UserProfileEntity?,
    onToggleRoast: (Boolean) -> Unit
) {
    val name = userProfile?.name ?: "Bhai"
    val nickname = userProfile?.nickname ?: "Gym Bro"
    val isRoastOn = userProfile?.roastModeEnabled ?: true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_header_card"),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isRoastOn) RoastRed.copy(alpha = 0.5f) else DarkSurfaceBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Namaste, $name",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "($nickname)",
                            fontSize = 14.sp,
                            color = FlameOrange,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Text(
                        text = "Calisthenics Journey • Month ${userProfile?.currentMonth ?: 1}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                // Roast Mode Toggle Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isRoastOn) RoastRed.copy(alpha = 0.15f) else Color(0xFF1B1E2B),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isRoastOn) RoastRed else DarkSurfaceBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("roast_mode_toggle_row"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Roast Mode",
                            tint = if (isRoastOn) RoastRed else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isRoastOn) "ROAST ON" else "ROAST OFF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRoastOn) RoastRed else TextMuted
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Switch(
                            checked = isRoastOn,
                            onCheckedChange = onToggleRoast,
                            modifier = Modifier.testTag("roast_mode_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = RoastRed,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkSurfaceCard
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic status quote
            val statusQuote = if (isRoastOn) {
                "🔥 Roast Mode ON: Gym buddy full gaali aur raw motivation ke sath ready hai!"
            } else {
                "💪 Supportive Mode: Pure technique, encouragement, and structured progression."
            }

            Text(
                text = statusQuote,
                fontSize = 12.sp,
                color = if (isRoastOn) Color(0xFFFF8A80) else ElectricCyan,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun TodayWorkoutHeroCard(
    routine: DayWorkoutRoutine,
    onStartWorkout: () -> Unit,
    onOpenManualChooser: () -> Unit
) {
    val today = LocalDate.now()
    val isRest = routine.isRestDay

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("today_workout_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(FlameOrange, ElectricCyan)
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isRest) Color(0xFF1A3828) else Color(0xFF3D160A)
                ) {
                    Text(
                        text = "TODAY: ${today.dayOfWeek.name}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRest) NeonLime else FlameOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                TextButton(
                    text = "Pick Extra Workout",
                    onClick = onOpenManualChooser
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = routine.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Text(
                text = routine.hindiTagline,
                fontSize = 14.sp,
                color = ElectricCyan,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (isRest) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF131722), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = "Rest",
                        tint = NeonLime,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Recovery Day: Muscle growth happens during rest! Sleep 8h, stay hydrated & do light stretching.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF12141F), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    routine.exercises.forEachIndexed { index, ex ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(Color(0xFF232738), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ex.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                text = "${ex.targetSets} × ${ex.targetRepsOrSecs} ${if (ex.isDuration) "sec" else "reps"}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = FlameOrange
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onStartWorkout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("start_workout_button"),
                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Workout",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRest) "Start Active Recovery / Stretch" else "START TODAY'S WORKOUT",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TextButton(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = ElectricCyan,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    )
}

@Composable
private fun ManualWorkoutSelectorCard(
    routines: List<DayWorkoutRoutine>,
    onSelectRoutine: (DayWorkoutRoutine) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF191D2B)),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Manual / Extra Workout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF003844)
                ) {
                    Text(
                        text = "MANUAL EXTRA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricCyan,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = "Track an extra session without overwriting your standard weekly routine schedule.",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            routines.filter { !it.isRestDay }.forEach { routine ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onSelectRoutine(routine) },
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${routine.dayOfWeek.name}: ${routine.title}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${routine.exercises.size} Exercises • ${routine.workoutType}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Select",
                            tint = FlameOrange,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyConsistencyCard(
    completedDates: Set<String>
) {
    val days = listOf(
        DayOfWeek.MONDAY to "M",
        DayOfWeek.TUESDAY to "T",
        DayOfWeek.WEDNESDAY to "W",
        DayOfWeek.THURSDAY to "Th",
        DayOfWeek.FRIDAY to "F",
        DayOfWeek.SATURDAY to "Sa",
        DayOfWeek.SUNDAY to "Su"
    )

    val todayDay = LocalDate.now().dayOfWeek

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly Training Schedule",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "4-Month Protocol",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { (day, label) ->
                    val isToday = day == todayDay
                    val isRest = day == DayOfWeek.THURSDAY || day == DayOfWeek.SUNDAY

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) FlameOrange else TextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isToday -> FlameOrange.copy(alpha = 0.25f)
                                        isRest -> Color(0xFF171B26)
                                        else -> Color(0xFF1C2130)
                                    }
                                )
                                .border(
                                    width = if (isToday) 1.5.dp else 1.dp,
                                    color = if (isToday) FlameOrange else DarkSurfaceBorder,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (day) {
                                    DayOfWeek.MONDAY -> "Push"
                                    DayOfWeek.TUESDAY -> "Pull"
                                    DayOfWeek.WEDNESDAY -> "Legs"
                                    DayOfWeek.THURSDAY -> "Rest"
                                    DayOfWeek.FRIDAY -> "Push"
                                    DayOfWeek.SATURDAY -> "Mix"
                                    DayOfWeek.SUNDAY -> "Rest"
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRest) TextMuted else TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickAskCoachCard(
    onAsk: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI Coach",
                    tint = ElectricCyan,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Quick Ask AI Gym Buddy",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Text(
                text = "Instant precise answers with memory & zero unsolicited fluff.",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            val quickQuestions = listOf(
                "Aaj kya workout hai?",
                "Pull-up vs chin-up mein difference kya hai?",
                "Mera confirmed PR record kya hai?",
                "Kitne reps karu push-ups ke?",
                "Creatine lena zaroori hai kya?"
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickQuestions.forEach { q ->
                    Surface(
                        modifier = Modifier.clickable { onAsk(q) },
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF1A1E2C),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                    ) {
                        Text(
                            text = q,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrSpotlightCard(
    personalRecords: List<PersonalRecordEntity>,
    onViewGuide: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "PRs",
                        tint = PrGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirmed Personal Records",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "Exercise Guide",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FlameOrange,
                    modifier = Modifier
                        .clickable(onClick = onViewGuide)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (personalRecords.isEmpty()) {
                Text(
                    text = "No confirmed personal records yet. Log your first workout to establish your baseline!",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    personalRecords.take(4).forEach { pr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF11141E), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = pr.exerciseName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Single set • ${pr.status}",
                                    fontSize = 11.sp,
                                    color = if (pr.status == "CONFIRMED") NeonLime else Color(0xFFFFB74D)
                                )
                            }

                            Text(
                                text = "${pr.recordValue} ${pr.unit}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PrGold
                            )
                        }
                    }
                }
            }
        }
    }
}
