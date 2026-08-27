package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessageEntity
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.NeonLime
import com.example.ui.theme.RoastRed
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.FitnessViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiCoachScreen(
    viewModel: FitnessViewModel,
    initialPrompt: String? = null,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    val isRoastOn = userProfile?.roastModeEnabled ?: true
    val userName = userProfile?.name ?: "Bhai"

    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Handle initial prompt passed from dashboard chips
    LaunchedEffect(initialPrompt) {
        if (!initialPrompt.isNullOrBlank()) {
            viewModel.sendAiMessage(initialPrompt)
        }
    }

    // Scroll to bottom on new messages
    LaunchedEffect(chatMessages.size, isAiLoading) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // AI Coach Header & Roast Mode Controller
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_coach_header_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isRoastOn) RoastRed.copy(alpha = 0.6f) else DarkSurfaceBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (isRoastOn) Brush.linearGradient(listOf(RoastRed, FlameOrange))
                                else Brush.linearGradient(listOf(ElectricCyan, NeonLime)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isRoastOn) Icons.Default.LocalFireDepartment else Icons.Default.Psychology,
                            contentDescription = "Coach Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isRoastOn) "Gaali Baaz Gym Buddy 🔥" else "AI Fitness Coach 🏋️",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isRoastOn) "Answers first • Full savage roast mode" else "Strict calisthenics guidance • Supportive",
                            fontSize = 11.sp,
                            color = if (isRoastOn) Color(0xFFFF8A80) else ElectricCyan
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = isRoastOn,
                        onCheckedChange = { viewModel.toggleRoastMode(it) },
                        modifier = Modifier.testTag("ai_roast_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = RoastRed,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkSurfaceCard
                        )
                    )

                    IconButton(
                        onClick = { viewModel.clearChatHistory() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Quick Prompt Pills
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val chips = listOf(
                "Aaj kya workout hai?",
                "Pull-up vs Chin-up diff?",
                "Mera confirmed PR kya hai?",
                "Kitne reps karu?",
                "Creatine lena zaroori hai?"
            )
            chips.forEach { chipText ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF181B28),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                    modifier = Modifier.clickable {
                        viewModel.sendAiMessage(chipText)
                    }
                ) {
                    Text(
                        text = chipText,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (chatMessages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsGymnastics,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Bhai koi bhi sawal pooch workout ya PR ke baare mein!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "AI exact sawal ka pehle seedha answer dega bina subject change kiye.",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(chatMessages) { msg ->
                    ChatBubble(message = msg, userName = userName)
                }
            }

            if (isAiLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = FlameOrange,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Coach is thinking...",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // Input Field and Send Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 85.dp, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                placeholder = { Text("Pooch bhai... (e.g. 'Aaj kya workout hai?')", fontSize = 13.sp, color = TextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlameOrange,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedContainerColor = DarkSurfaceCard,
                    unfocusedContainerColor = DarkSurfaceCard,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputMessage.isNotBlank()) {
                            viewModel.sendAiMessage(inputMessage)
                            inputMessage = ""
                        }
                    }
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputMessage.isNotBlank()) {
                        viewModel.sendAiMessage(inputMessage)
                        inputMessage = ""
                    }
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(FlameOrange, CircleShape)
                    .testTag("ai_chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessageEntity,
    userName: String
) {
    val isUser = message.sender == "USER"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                text = if (isUser) userName else if (message.isRoast) "Gym Buddy (Roast 🔥)" else "AI Coach",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isUser) ElectricCyan else if (message.isRoast) RoastRed else NeonLime
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) Color(0xFF1E283A) else if (message.isRoast) Color(0xFF2B1218) else DarkSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) ElectricCyan.copy(alpha = 0.4f)
                else if (message.isRoast) RoastRed.copy(alpha = 0.4f)
                else DarkSurfaceBorder
            ),
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Text(
                text = message.message,
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 20.sp,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}
