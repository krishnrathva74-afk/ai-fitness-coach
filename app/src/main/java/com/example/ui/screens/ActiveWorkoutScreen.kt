package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.routine.ExercisePlan
import com.example.ui.theme.DarkBg
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

@Composable
fun ActiveWorkoutScreen(
    viewModel: FitnessViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.activeWorkoutState.collectAsStateWithLifecycle()
    val routine = state.routine

    if (!state.isActive || routine == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No workout currently active.", color = TextSecondary)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("Go to Dashboard")
                }
            }
        }
        return
    }

    var showCancelDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }
    var workoutNotes by remember { mutableStateOf("") }

    val currentEx = routine.exercises.getOrNull(state.currentExerciseIndex)

    var inputRepsOrSecs by remember(state.currentExerciseIndex, state.currentSetIndex) {
        mutableIntStateOf(currentEx?.targetRepsOrSecs ?: 10)
    }

    val workoutMinutes = state.workoutDurationSeconds / 60
    val workoutSecs = state.workoutDurationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", workoutMinutes, workoutSecs)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Top Status Header: Title, Active Workout Timer & Exit Button
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("active_workout_header"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = routine.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = ElectricCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Elapsed: $formattedDuration",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricCyan
                            )
                        }
                    }

                    Row {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = RoastRed),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoastRed.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Quit", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showFinishDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Finish", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // PR Celebration Banner (if a set broke a PR)
        if (state.recentPrCelebration != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF382900)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PrGold)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "PR",
                            tint = PrGold,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = state.recentPrCelebration ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrGold
                        )
                    }
                }
            }
        }

        // Next Exercise Prompt / Cue
        if (state.nextExercisePrompt != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF131B2A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Cue",
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.nextExercisePrompt ?: "",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Active Rest Countdown Timer Card (Controlled by user)
        if (state.isRestTimerRunning || state.restTimerSecondsLeft > 0) {
            item {
                RestTimerComponent(
                    secondsLeft = state.restTimerSecondsLeft,
                    totalDuration = state.totalRestDuration,
                    isPaused = state.isRestTimerPaused,
                    onPause = { viewModel.pauseRestTimer() },
                    onResume = { viewModel.resumeRestTimer() },
                    onSkip = { viewModel.skipRestTimer() },
                    onAdjust = { delta -> viewModel.adjustRestTimer(delta) }
                )
            }
        }

        // Current Active Exercise Logger Card
        if (currentEx != null && !state.isWorkoutFinished) {
            item {
                ActiveExerciseLoggerCard(
                    exercise = currentEx,
                    exerciseIndex = state.currentExerciseIndex,
                    totalExercises = routine.exercises.size,
                    currentSet = state.currentSetIndex,
                    inputValue = inputRepsOrSecs,
                    onValueChange = { inputRepsOrSecs = it },
                    onLogSet = {
                        viewModel.recordCompletedSet(inputRepsOrSecs)
                    }
                )
            }
        } else if (state.isWorkoutFinished) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10281E)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonLime)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Complete",
                            tint = NeonLime,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Workout Complete! 🔥",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonLime
                        )
                        Text(
                            text = "Shabash bhai! Sare sets finish kar liye. Ab save karke summarize karo!",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Button(
                            onClick = { showFinishDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonLime),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SAVE & VIEW SUMMARY", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Completed Sets Log for this session
        item {
            Text(
                text = "Completed Sets (${state.completedSets.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (state.completedSets.isEmpty()) {
            item {
                Text(
                    text = "No sets completed yet. Complete your first set above!",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        } else {
            itemsIndexed(state.completedSets) { idx, set ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(if (set.isPersonalRecord) PrGold else NeonLime.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (set.isPersonalRecord) Icons.Default.EmojiEvents else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (set.isPersonalRecord) Color.Black else NeonLime,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "${set.exerciseName} — Set ${set.setNumber}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (set.completedDurationSecs > 0) "${set.completedDurationSecs} seconds hold" else "${set.completedReps} reps completed",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        if (set.isPersonalRecord) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF4A3800)
                            ) {
                                Text(
                                    text = "NEW PR 🔥",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrGold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }

    // Finish Workout Dialog
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Workout Complete!") },
            text = {
                Column {
                    Text("Total Time: $formattedDuration")
                    Text("Sets Logged: ${state.completedSets.size}")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = workoutNotes,
                        onValueChange = { workoutNotes = it },
                        label = { Text("Workout Notes (optional)") },
                        placeholder = { Text("Form feeling, energy level, pump...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = DarkSurfaceBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        viewModel.finishActiveWorkout(workoutNotes)
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("Save & Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Cancel Workout Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Quit Workout?") },
            text = { Text("Are you sure you want to stop this workout session without saving?") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelActiveWorkout()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoastRed)
                ) {
                    Text("Quit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Stay")
                }
            }
        )
    }
}

@Composable
private fun ActiveExerciseLoggerCard(
    exercise: ExercisePlan,
    exerciseIndex: Int,
    totalExercises: Int,
    currentSet: Int,
    inputValue: Int,
    onValueChange: (Int) -> Unit,
    onLogSet: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_exercise_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, FlameOrange)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF381508)
                ) {
                    Text(
                        text = "EXERCISE ${exerciseIndex + 1} OF $totalExercises",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlameOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Rest Target: ${exercise.restRangeDesc}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exercise.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Text(
                text = "Set $currentSet of ${exercise.targetSets} • Target: ${exercise.targetRepsOrSecs} ${if (exercise.isDuration) "seconds" else "reps"}",
                fontSize = 14.sp,
                color = ElectricCyan,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Muscles: ${exercise.musclesWorked}",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stepper for Reps / Duration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF10131C), RoundedCornerShape(14.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onValueChange((inputValue - 1).coerceAtLeast(1)) },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF222636), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$inputValue",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (exercise.isDuration) "Seconds Hold" else "Completed Reps",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }

                IconButton(
                    onClick = { onValueChange(inputValue + 1) },
                    modifier = Modifier
                        .size(44.dp)
                        .background(FlameOrange, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLogSet,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("log_set_button"),
                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Log", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LOG SET $currentSet",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun RestTimerComponent(
    secondsLeft: Int,
    totalDuration: Int,
    isPaused: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSkip: () -> Unit,
    onAdjust: (Int) -> Unit
) {
    val progress = if (totalDuration > 0) (secondsLeft.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f) else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("rest_timer_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1D28)),
        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REST PERIOD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricCyan
                )

                Text(
                    text = if (isPaused) "PAUSED" else "COUNTDOWN ACTIVE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPaused) Color(0xFFFFB74D) else NeonLime
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${secondsLeft}s",
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ElectricCyan,
                trackColor = Color(0xFF1B2D3D)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onAdjust(-15) },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Text("-15s", fontSize = 11.sp, color = TextPrimary)
                }

                Button(
                    onClick = { if (isPaused) onResume() else onPause() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isPaused) NeonLime else Color(0xFF1E3A4D))
                ) {
                    Icon(
                        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = "Pause/Resume",
                        tint = if (isPaused) Color.Black else TextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isPaused) "Resume" else "Pause",
                        fontSize = 12.sp,
                        color = if (isPaused) Color.Black else TextPrimary
                    )
                }

                OutlinedButton(
                    onClick = { onAdjust(15) },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
                ) {
                    Text("+15s", fontSize = 11.sp, color = TextPrimary)
                }

                Button(
                    onClick = onSkip,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Icon(imageVector = Icons.Default.FastForward, contentDescription = "Skip", tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Skip", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}
