package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.routine.DefaultRoutine
import com.example.data.routine.ExercisePlan
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonLime
import com.example.ui.theme.RoastRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExerciseGuideScreen(
    modifier: Modifier = Modifier
) {
    val allExercises = remember { DefaultRoutine.ALL_EXERCISE_GUIDES.values.toList() }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val filteredExercises = remember(selectedCategory) {
        when (selectedCategory) {
            "PUSH" -> allExercises.filter { it.name.contains("push", ignoreCase = true) }
            "PULL" -> allExercises.filter { it.name.contains("pull", ignoreCase = true) || it.name.contains("chin", ignoreCase = true) || it.name.contains("hang", ignoreCase = true) }
            "LEGS" -> allExercises.filter { it.name.contains("squat", ignoreCase = true) || it.name.contains("lunge", ignoreCase = true) || it.name.contains("calf", ignoreCase = true) }
            "CORE" -> allExercises.filter { it.name.contains("plank", ignoreCase = true) || it.name.contains("raise", ignoreCase = true) }
            else -> allExercises
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Calisthenics Form Library",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Strict posture, hand placement, breathing and common mistakes.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                val categories = listOf("ALL", "PUSH", "PULL", "LEGS", "CORE")
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier.clickable { selectedCategory = cat },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) FlameOrange else DarkSurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) FlameOrange else DarkSurfaceBorder
                        )
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else TextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        items(filteredExercises) { exercise ->
            ExpandableExerciseGuideCard(exercise = exercise)
        }
    }
}

@Composable
private fun ExpandableExerciseGuideCard(exercise: ExercisePlan) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .testTag("exercise_card_${exercise.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isExpanded) ElectricCyan.copy(alpha = 0.6f) else DarkSurfaceBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = exercise.hindiName,
                        fontSize = 12.sp,
                        color = ElectricCyan
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF131F2A)
                    ) {
                        Text(
                            text = "Rest: ${exercise.restRangeDesc}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElectricCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        tint = TextSecondary
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = DarkSurfaceBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    FormDetailRow(title = "Muscles Worked", content = exercise.musclesWorked, highlightColor = NeonLime)
                    FormDetailRow(title = "Hand Position", content = exercise.handPosition)
                    FormDetailRow(title = "Body Position", content = exercise.bodyPosition)
                    FormDetailRow(title = "Movement Execution", content = exercise.movement)
                    FormDetailRow(title = "Basic Form Cues", content = exercise.basicForm)

                    Spacer(modifier = Modifier.height(6.dp))

                    // Common Mistakes Callout
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF381218),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoastRed.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Mistakes",
                                tint = RoastRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Common Mistakes to Avoid",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoastRed
                                )
                                Text(
                                    text = exercise.commonMistakes,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormDetailRow(
    title: String,
    content: String,
    highlightColor: Color = TextPrimary
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = FlameOrange
        )
        Text(
            text = content,
            fontSize = 13.sp,
            color = highlightColor
        )
    }
}
