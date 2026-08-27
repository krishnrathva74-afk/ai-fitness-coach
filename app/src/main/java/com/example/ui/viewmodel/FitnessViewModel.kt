package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MemoryEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.data.model.WorkoutSetEntity
import com.example.data.repository.FitnessRepository
import com.example.data.routine.DayWorkoutRoutine
import com.example.data.routine.DefaultRoutine
import com.example.data.routine.ExercisePlan
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ActiveWorkoutUiState(
    val isActive: Boolean = false,
    val sessionId: Long = 0,
    val routine: DayWorkoutRoutine? = null,
    val currentExerciseIndex: Int = 0,
    val currentSetIndex: Int = 1,
    val completedSets: List<WorkoutSetEntity> = emptyList(),
    val workoutDurationSeconds: Int = 0,
    val restTimerSecondsLeft: Int = 0,
    val totalRestDuration: Int = 60,
    val isRestTimerRunning: Boolean = false,
    val isRestTimerPaused: Boolean = false,
    val recentPrCelebration: String? = null,
    val lastCompletedSetSummary: String? = null,
    val nextExercisePrompt: String? = null,
    val isWorkoutFinished: Boolean = false
)

class FitnessViewModel(
    private val repository: FitnessRepository,
    private val aiService: GeminiAiService = GeminiAiService()
) : ViewModel() {

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val memories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personalRecords: StateFlow<List<PersonalRecordEntity>> = repository.allPersonalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutSessions: StateFlow<List<WorkoutSessionEntity>> = repository.allWorkoutSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessageEntity>> = repository.chatMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeWorkoutState = MutableStateFlow(ActiveWorkoutUiState())
    val activeWorkoutState: StateFlow<ActiveWorkoutUiState> = _activeWorkoutState.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeDefaultsIfNeeded()
        }
    }

    fun toggleRoastMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleRoastMode(enabled)
        }
    }

    fun updateApiKey(apiKey: String) {
        viewModelScope.launch {
            repository.updateApiKey(apiKey)
        }
    }

    fun saveUserProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }

    fun addMemory(category: String, content: String, status: String = "CONFIRMED") {
        viewModelScope.launch {
            repository.addMemory(category, content, status)
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            repository.clearAllMemories()
        }
    }

    fun addOrUpdatePersonalRecord(
        exerciseName: String,
        value: Int,
        unit: String,
        status: String = "CONFIRMED",
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.insertOrUpdatePersonalRecord(exerciseName, value, unit, status, notes)
        }
    }

    fun deletePersonalRecord(id: Long) {
        viewModelScope.launch {
            repository.deletePersonalRecord(id)
        }
    }

    fun clearAllPersonalRecords() {
        viewModelScope.launch {
            repository.clearAllRecords()
        }
    }

    // --- WORKOUT TRACKING ENGINE ---

    fun startWorkout(routine: DayWorkoutRoutine, isManualExtra: Boolean = false) {
        viewModelScope.launch {
            val dateIso = LocalDate.now().toString()
            val dayOfWeek = LocalDate.now().dayOfWeek.name
            val sessionId = repository.startWorkoutSession(
                dateIso = dateIso,
                dayOfWeek = dayOfWeek,
                workoutType = routine.workoutType,
                workoutTitle = routine.title,
                isManualExtra = isManualExtra
            )

            _activeWorkoutState.value = ActiveWorkoutUiState(
                isActive = true,
                sessionId = sessionId,
                routine = routine,
                currentExerciseIndex = 0,
                currentSetIndex = 1,
                completedSets = emptyList(),
                workoutDurationSeconds = 0,
                nextExercisePrompt = if (routine.exercises.isNotEmpty()) {
                    "Set 1: ${routine.exercises[0].name} (${routine.exercises[0].targetRepsOrSecs} ${if (routine.exercises[0].isDuration) "sec" else "reps"})"
                } else null
            )

            startWorkoutDurationTimer()
        }
    }

    private fun startWorkoutDurationTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _activeWorkoutState.value = _activeWorkoutState.value.copy(
                    workoutDurationSeconds = _activeWorkoutState.value.workoutDurationSeconds + 1
                )
            }
        }
    }

    fun recordCompletedSet(repsOrDuration: Int, notes: String = "") {
        val state = _activeWorkoutState.value
        val routine = state.routine ?: return
        if (state.currentExerciseIndex >= routine.exercises.size) return

        val currentExercise = routine.exercises[state.currentExerciseIndex]
        val currentSet = state.currentSetIndex

        viewModelScope.launch {
            val isPr = repository.recordSet(
                sessionId = state.sessionId,
                exerciseName = currentExercise.name,
                setNumber = currentSet,
                targetReps = currentExercise.targetRepsOrSecs,
                completedReps = if (currentExercise.isDuration) 0 else repsOrDuration,
                completedDurationSecs = if (currentExercise.isDuration) repsOrDuration else 0,
                restSecondsRecorded = currentExercise.restSecondsDefault,
                notes = notes
            )

            val newSetEntity = WorkoutSetEntity(
                sessionId = state.sessionId,
                exerciseName = currentExercise.name,
                setNumber = currentSet,
                targetReps = currentExercise.targetRepsOrSecs,
                completedReps = if (currentExercise.isDuration) 0 else repsOrDuration,
                completedDurationSecs = if (currentExercise.isDuration) repsOrDuration else 0,
                restSecondsRecorded = currentExercise.restSecondsDefault,
                isPersonalRecord = isPr,
                notes = notes
            )

            val updatedSets = state.completedSets + newSetEntity
            val prCelebration = if (isPr) "🔥 NEW PERSONAL RECORD! $repsOrDuration ${if (currentExercise.isDuration) "sec" else "reps"} in ${currentExercise.name}!" else null

            // Determine next set or next exercise
            var nextExerciseIdx = state.currentExerciseIndex
            var nextSetIdx = currentSet + 1
            var isFinished = false
            var nextPrompt = ""

            if (nextSetIdx > currentExercise.targetSets) {
                nextExerciseIdx += 1
                nextSetIdx = 1
                if (nextExerciseIdx >= routine.exercises.size) {
                    isFinished = true
                    nextPrompt = "All exercises completed! Ready to finish workout."
                } else {
                    val nextEx = routine.exercises[nextExerciseIdx]
                    nextPrompt = "Next Exercise: ${nextEx.name} — Set 1 (${nextEx.targetRepsOrSecs} ${if (nextEx.isDuration) "sec" else "reps"}). Rest ${nextEx.restRangeDesc}"
                }
            } else {
                nextPrompt = "Next: ${currentExercise.name} Set $nextSetIdx — ${currentExercise.targetRepsOrSecs} reps. Rest ${currentExercise.restRangeDesc}"
            }

            val summaryText = "${currentExercise.name} Set $currentSet complete: $repsOrDuration ${if (currentExercise.isDuration) "sec" else "reps"} ✅"

            _activeWorkoutState.value = state.copy(
                currentExerciseIndex = nextExerciseIdx,
                currentSetIndex = nextSetIdx,
                completedSets = updatedSets,
                recentPrCelebration = prCelebration,
                lastCompletedSetSummary = summaryText,
                nextExercisePrompt = nextPrompt,
                isWorkoutFinished = isFinished
            )

            if (!isFinished) {
                startRestTimer(currentExercise.restSecondsDefault)
            }
        }
    }

    fun startRestTimer(seconds: Int) {
        restTimerJob?.cancel()
        _activeWorkoutState.value = _activeWorkoutState.value.copy(
            restTimerSecondsLeft = seconds,
            totalRestDuration = seconds,
            isRestTimerRunning = true,
            isRestTimerPaused = false
        )

        restTimerJob = viewModelScope.launch {
            while (isActive && _activeWorkoutState.value.restTimerSecondsLeft > 0) {
                delay(1000)
                val currentLeft = _activeWorkoutState.value.restTimerSecondsLeft
                if (!_activeWorkoutState.value.isRestTimerPaused) {
                    if (currentLeft > 1) {
                        _activeWorkoutState.value = _activeWorkoutState.value.copy(
                            restTimerSecondsLeft = currentLeft - 1
                        )
                    } else {
                        _activeWorkoutState.value = _activeWorkoutState.value.copy(
                            restTimerSecondsLeft = 0,
                            isRestTimerRunning = false
                        )
                        break
                    }
                }
            }
        }
    }

    fun pauseRestTimer() {
        _activeWorkoutState.value = _activeWorkoutState.value.copy(isRestTimerPaused = true)
    }

    fun resumeRestTimer() {
        _activeWorkoutState.value = _activeWorkoutState.value.copy(isRestTimerPaused = false)
    }

    fun skipRestTimer() {
        restTimerJob?.cancel()
        _activeWorkoutState.value = _activeWorkoutState.value.copy(
            restTimerSecondsLeft = 0,
            isRestTimerRunning = false,
            isRestTimerPaused = false
        )
    }

    fun adjustRestTimer(secondsDelta: Int) {
        val current = _activeWorkoutState.value.restTimerSecondsLeft
        val newTarget = (current + secondsDelta).coerceAtLeast(5)
        _activeWorkoutState.value = _activeWorkoutState.value.copy(
            restTimerSecondsLeft = newTarget,
            totalRestDuration = maxOf(_activeWorkoutState.value.totalRestDuration, newTarget)
        )
    }

    fun finishActiveWorkout(notes: String = "") {
        val state = _activeWorkoutState.value
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()

        viewModelScope.launch {
            repository.completeWorkoutSession(
                sessionId = state.sessionId,
                durationSeconds = state.workoutDurationSeconds,
                notes = notes
            )

            _activeWorkoutState.value = ActiveWorkoutUiState(
                isActive = false,
                isWorkoutFinished = false
            )
        }
    }

    fun cancelActiveWorkout() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        _activeWorkoutState.value = ActiveWorkoutUiState(isActive = false)
    }

    // --- AI COACH CHAT ---

    fun sendAiMessage(userMessageText: String) {
        val trimmed = userMessageText.trim()
        if (trimmed.isBlank()) return

        val profile = userProfile.value
        val isRoast = profile?.roastModeEnabled ?: true
        val mems = memories.value
        val prs = personalRecords.value

        val activeSummary = if (_activeWorkoutState.value.isActive) {
            val currentEx = _activeWorkoutState.value.routine?.exercises?.getOrNull(_activeWorkoutState.value.currentExerciseIndex)?.name ?: "Workout"
            "Currently tracking: $currentEx (Set ${_activeWorkoutState.value.currentSetIndex})"
        } else null

        viewModelScope.launch {
            repository.addChatMessage(sender = "USER", message = trimmed, isRoast = false)
            _isAiLoading.value = true

            val aiResponse = aiService.getCoachResponse(
                userMessage = trimmed,
                userProfile = profile,
                memories = mems,
                personalRecords = prs,
                roastModeEnabled = isRoast,
                activeWorkoutSummary = activeSummary
            )

            repository.addChatMessage(sender = "AI", message = aiResponse, isRoast = isRoast)
            _isAiLoading.value = false
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChatHistory()
        }
    }

    companion object {
        fun provideFactory(repository: FitnessRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FitnessViewModel(repository) as T
                }
            }
    }
}
