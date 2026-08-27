package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MemoryEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.UserProfileEntity
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
fun ProfileMemoryScreen(
    viewModel: FitnessViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val memories by viewModel.memories.collectAsStateWithLifecycle()
    val personalRecords by viewModel.personalRecords.collectAsStateWithLifecycle()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAddMemoryDialog by remember { mutableStateOf(false) }
    var showAddPrDialog by remember { mutableStateOf(false) }
    var showClearMemoriesDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. User Profile Card
        item {
            UserProfileCard(
                profile = userProfile,
                onEdit = { showEditProfileDialog = true },
                onToggleRoast = { viewModel.toggleRoastMode(it) }
            )
        }

        // 2. Personal Records Manager
        item {
            PersonalRecordsManagerCard(
                personalRecords = personalRecords,
                onAddPr = { showAddPrDialog = true },
                onDeletePr = { viewModel.deletePersonalRecord(it) }
            )
        }

        // 3. Stored Memories Manager
        item {
            StoredMemoriesCard(
                memories = memories,
                onAddMemory = { showAddMemoryDialog = true },
                onDeleteMemory = { viewModel.deleteMemory(it) },
                onClearAll = { showClearMemoriesDialog = true }
            )
        }

        // 4. API Key & Settings Card
        item {
            ApiKeySettingsCard(
                currentKey = userProfile?.apiKeyOverride ?: "",
                onConfigure = { showApiKeyDialog = true }
            )
        }
    }

    // Edit Profile Dialog
    if (showEditProfileDialog && userProfile != null) {
        var editName by remember { mutableStateOf(userProfile!!.name) }
        var editNickname by remember { mutableStateOf(userProfile!!.nickname) }
        var editGoal by remember { mutableStateOf(userProfile!!.fitnessGoal) }
        var editExperience by remember { mutableStateOf(userProfile!!.trainingExperience) }
        var editWeight by remember { mutableStateOf(userProfile!!.weightKg.toString()) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit User Profile") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editNickname,
                        onValueChange = { editNickname = it },
                        label = { Text("Nickname") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editGoal,
                        onValueChange = { editGoal = it },
                        label = { Text("Goal") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editExperience,
                        onValueChange = { editExperience = it },
                        label = { Text("Experience Level") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editWeight,
                        onValueChange = { editWeight = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = userProfile!!.copy(
                            name = editName.ifBlank { "Bhai" },
                            nickname = editNickname.ifBlank { "Gym Bro" },
                            fitnessGoal = editGoal,
                            trainingExperience = editExperience,
                            weightKg = editWeight.toFloatOrNull() ?: userProfile!!.weightKg
                        )
                        viewModel.saveUserProfile(updated)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("Save Profile")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add PR Dialog
    if (showAddPrDialog) {
        var prExercise by remember { mutableStateOf("Pull-ups") }
        var prValue by remember { mutableStateOf("10") }
        var prUnit by remember { mutableStateOf("reps") }
        var prNotes by remember { mutableStateOf("Single set max clean reps") }

        AlertDialog(
            onDismissRequest = { showAddPrDialog = false },
            title = { Text("Add / Update Personal Record") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = prExercise,
                        onValueChange = { prExercise = it },
                        label = { Text("Exercise Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = prValue,
                        onValueChange = { prValue = it },
                        label = { Text("Record Value (reps or seconds)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = prUnit,
                        onValueChange = { prUnit = it },
                        label = { Text("Unit (reps/seconds)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = prNotes,
                        onValueChange = { prNotes = it },
                        label = { Text("Form Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = prValue.toIntOrNull() ?: 1
                        viewModel.addOrUpdatePersonalRecord(
                            exerciseName = prExercise.trim(),
                            value = num,
                            unit = prUnit.trim(),
                            status = "CONFIRMED",
                            notes = prNotes
                        )
                        showAddPrDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrGold)
                ) {
                    Text("Save Record", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPrDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Memory Dialog
    if (showAddMemoryDialog) {
        var memCategory by remember { mutableStateOf("PERSONAL") }
        var memContent by remember { mutableStateOf("") }
        var memStatus by remember { mutableStateOf("CONFIRMED") }

        AlertDialog(
            onDismissRequest = { showAddMemoryDialog = false },
            title = { Text("Add Stored Memory") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("PERSONAL", "FITNESS", "PREFERENCE").forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (memCategory == cat) FlameOrange else DarkSurfaceCard,
                                modifier = Modifier.clickable { memCategory = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (memCategory == cat) Color.White else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = memContent,
                        onValueChange = { memContent = it },
                        label = { Text("Memory Content") },
                        placeholder = { Text("e.g. Prefers strict pull-ups without swinging") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Status: $memStatus", fontSize = 12.sp, color = if (memStatus == "CONFIRMED") NeonLime else Color(0xFFFFB74D))
                        TextButton(
                            onClick = {
                                memStatus = if (memStatus == "CONFIRMED") "UNCERTAIN" else "CONFIRMED"
                            }
                        ) {
                            Text("Toggle Status")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (memContent.isNotBlank()) {
                            viewModel.addMemory(memCategory, memContent.trim(), memStatus)
                            showAddMemoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("Save Memory")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMemoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Clear All Memories Confirmation Dialog
    if (showClearMemoriesDialog) {
        AlertDialog(
            onDismissRequest = { showClearMemoriesDialog = false },
            title = { Text("Clear All Stored Memories?") },
            text = { Text("This will erase all learned personal memories from the database. Confirmed PR records will be kept.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllMemories()
                        showClearMemoriesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoastRed)
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearMemoriesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // API Key Dialog
    if (showApiKeyDialog) {
        var keyInput by remember { mutableStateOf(userProfile?.apiKeyOverride ?: "") }

        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = { Text("Gemini API Key Setup") },
            text = {
                Column {
                    Text(
                        text = "Enter a custom Gemini API key or use the default environment configuration.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        label = { Text("API Key") },
                        placeholder = { Text("AIzaSy...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateApiKey(keyInput.trim())
                        showApiKeyDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Save Key", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiKeyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun UserProfileCard(
    profile: UserProfileEntity?,
    onEdit: () -> Unit,
    onToggleRoast: (Boolean) -> Unit
) {
    val isRoast = profile?.roastModeEnabled ?: true

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
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(FlameOrange.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = FlameOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "${profile?.name ?: "Bhai"} (${profile?.nickname ?: "Gym Bro"})",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Goal: ${profile?.fitnessGoal ?: "Calisthenics"}",
                            fontSize = 12.sp,
                            color = ElectricCyan
                        )
                    }
                }

                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = DarkSurfaceBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            ProfileDetailRow(label = "Language & Style", value = "${profile?.preferredLanguage} • ${profile?.communicationStyle}")
            ProfileDetailRow(label = "Experience", value = "${profile?.trainingExperience} • ${profile?.weightKg} kg")
            ProfileDetailRow(label = "Abilities", value = "${profile?.currentAbilities}")

            Spacer(modifier = Modifier.height(10.dp))

            // Roast Mode Switch in Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isRoast) Color(0xFF381218) else Color(0xFF131B2A), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Roast",
                        tint = if (isRoast) RoastRed else ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isRoast) "Roast Mode Enabled (Gaali 🔥)" else "Supportive Coaching Mode",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isRoast) "Authentic savage gym buddy personality" else "Polite, focused technique advice",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Switch(
                    checked = isRoast,
                    onCheckedChange = onToggleRoast,
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
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Text(text = value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun PersonalRecordsManagerCard(
    personalRecords: List<PersonalRecordEntity>,
    onAddPr: () -> Unit,
    onDeletePr: (Long) -> Unit
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
                        imageVector = Icons.Default.Star,
                        contentDescription = "PR",
                        tint = PrGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirmed Personal Records",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                OutlinedButton(
                    onClick = onAddPr,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrGold)
                ) {
                    Text("+ Add PR", fontSize = 11.sp, color = PrGold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (personalRecords.isEmpty()) {
                Text("No confirmed records stored.", fontSize = 13.sp, color = TextMuted)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    personalRecords.forEach { pr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF11141E), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pr.exerciseName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${pr.recordValue} ${pr.unit} • ${pr.status} • ${pr.notes}",
                                    fontSize = 12.sp,
                                    color = PrGold
                                )
                            }

                            IconButton(onClick = { onDeletePr(pr.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
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
private fun StoredMemoriesCard(
    memories: List<MemoryEntity>,
    onAddMemory: () -> Unit,
    onDeleteMemory: (Long) -> Unit,
    onClearAll: () -> Unit
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
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Memory",
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Memory System",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Row {
                    OutlinedButton(
                        onClick = onAddMemory,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElectricCyan)
                    ) {
                        Text("+ Memory", fontSize = 11.sp, color = ElectricCyan)
                    }

                    if (memories.isNotEmpty()) {
                        IconButton(onClick = onClearAll) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear",
                                tint = RoastRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = "Learned facts, user preferences, and fitness notes with confirmed validation.",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (memories.isEmpty()) {
                Text("No stored memories.", fontSize = 13.sp, color = TextMuted)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    memories.forEach { mem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF11141E), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (mem.status == "CONFIRMED") Color(0xFF143818) else Color(0xFF382E12)
                                    ) {
                                        Text(
                                            text = "${mem.category} • ${mem.status}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (mem.status == "CONFIRMED") NeonLime else Color(0xFFFFD54F),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = mem.content,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                            }

                            IconButton(onClick = { onDeleteMemory(mem.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Forget",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
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
private fun ApiKeySettingsCard(
    currentKey: String,
    onConfigure: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "Key",
                    tint = ElectricCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Gemini API Configuration",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (currentKey.isNotBlank()) "Custom API Key Active" else "Default Engine Active",
                        fontSize = 12.sp,
                        color = if (currentKey.isNotBlank()) NeonLime else TextSecondary
                    )
                }
            }

            Button(
                onClick = onConfigure,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2838))
            ) {
                Text("Configure", fontSize = 12.sp, color = ElectricCyan)
            }
        }
    }
}
