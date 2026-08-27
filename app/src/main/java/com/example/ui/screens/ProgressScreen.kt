package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonLime
import com.example.ui.theme.PrGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel
import java.time.LocalDate

@Composable
fun ProgressScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val personalRecords by viewModel.personalRecords.collectAsStateWithLifecycle()
    val workoutSessions by viewModel.workoutSessions.collectAsStateWithLifecycle()

    val completedSessions = remember(workoutSessions) {
        workoutSessions.filter { it.isCompleted }
    }

    val totalWorkouts = completedSessions.size

    // Calculate consistency streak
    val streakDays = remember(completedSessions) {
        if (completedSessions.isEmpty()) 0
        else {
            val dates = completedSessions.map { it.dateIso }.toSet()
            var currentStreak = 0
            var checkDate = LocalDate.now()
            while (dates.contains(checkDate.toString()) || dates.contains(checkDate.minusDays(1).toString())) {
                if (dates.contains(checkDate.toString())) {
                    currentStreak++
                    checkDate = checkDate.minusDays(1)
                } else if (currentStreak == 0 && dates.contains(checkDate.minusDays(1).toString())) {
                    checkDate = checkDate.minusDays(1)
                } else {
                    break
                }
            }
            maxOf(currentStreak, if (completedSessions.isNotEmpty()) 1 else 0)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Consistency & Workouts Stat Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("progress_stat_row"),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Workouts Done",
                    value = "$totalWorkouts",
                    icon = Icons.Default.FitnessCenter,
                    iconTint = FlameOrange
                )

                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Consistency Streak",
                    value = "$streakDays Days",
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = ElectricCyan
                )
            }
        }

        // 2. Four-Month Calisthenics Journey Tracker
        item {
            FourMonthJourneyCard(
                currentMonth = userProfile?.currentMonth ?: 1,
                totalWorkouts = totalWorkouts
            )
        }

        // 3. Exercise PR Breakdown
        item {
            ExercisePrProgressSection(personalRecords = personalRecords)
        }

        // 4. Workout History Log
        item {
            WorkoutHistorySection(sessions = completedSessions)
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconTint.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
            Text(
                text = title,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun FourMonthJourneyCard(
    currentMonth: Int,
    totalWorkouts: Int
) {
    val months = listOf(
        1 to Pair("Month 1: Consistency", "Establish routine habit & strict push/pull/leg baseline."),
        2 to Pair("Month 2: Strength & Control", "Improve concentric explosive power & 2s eccentric control."),
        3 to Pair("Month 3: Progressive Overload", "Gradual rep increments & higher difficulty angles."),
        4 to Pair("Month 4: Compare & Max PRs", "Final assessment vs baseline, PR milestones comparison.")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = "Journey",
                        tint = FlameOrange,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "4-Month Calisthenics Journey",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF381808)
                ) {
                    Text(
                        text = "ACTIVE: M$currentMonth",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FlameOrange,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            months.forEach { (mNum, info) ->
                val (mTitle, mDesc) = info
                val isCurrent = mNum == currentMonth
                val isCompleted = mNum < currentMonth

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                when {
                                    isCompleted -> NeonLime
                                    isCurrent -> FlameOrange
                                    else -> Color(0xFF232738)
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Done",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "$mNum",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) Color.White else TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = mTitle,
                            fontSize = 14.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) TextPrimary else TextSecondary
                        )
                        Text(
                            text = mDesc,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExercisePrProgressSection(
    personalRecords: List<PersonalRecordEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "PR",
                        tint = PrGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Exercise PR Breakdown",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "Confirmed Single Sets",
                    fontSize = 11.sp,
                    color = NeonLime
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (personalRecords.isEmpty()) {
                Text(
                    text = "Not enough data yet.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    personalRecords.forEach { pr ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF11141E), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = pr.exerciseName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${pr.recordValue} ${pr.unit}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrGold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Representative progressive visual bar
                            val progressTarget = when (pr.exerciseName) {
                                "Pull-ups" -> 15f
                                "Chin-ups" -> 15f
                                "Normal push-ups" -> 35f
                                "Diamond push-ups" -> 20f
                                "Plank" -> 120f
                                else -> 30f
                            }
                            val ratio = (pr.recordValue.toFloat() / progressTarget).coerceIn(0.1f, 1f)

                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = PrGold,
                                trackColor = Color(0xFF222738)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutHistorySection(
    sessions: List<WorkoutSessionEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Recent Workout History",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (sessions.isEmpty()) {
                Text(
                    text = "Not enough data yet. Complete workouts to populate history.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    sessions.take(6).forEach { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF11141E), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = session.workoutTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${session.dateIso} (${session.dayOfWeek}) • ${session.durationSeconds / 60} mins",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }

                            if (session.isManualExtra) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF003844)
                                ) {
                                    Text(
                                        text = "EXTRA",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricCyan,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Done",
                                    tint = NeonLime,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
