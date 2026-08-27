package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MemoryEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.data.model.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class FitnessRepository(private val database: AppDatabase) {

    private val userProfileDao = database.userProfileDao()
    private val memoryDao = database.memoryDao()
    private val workoutSessionDao = database.workoutSessionDao()
    private val workoutSetDao = database.workoutSetDao()
    private val personalRecordDao = database.personalRecordDao()
    private val chatMessageDao = database.chatMessageDao()

    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getProfile()
    val allMemories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()
    val allWorkoutSessions: Flow<List<WorkoutSessionEntity>> = workoutSessionDao.getAllSessions()
    val completedSessionsCount: Flow<Int> = workoutSessionDao.getCompletedSessionsCount()
    val allPersonalRecords: Flow<List<PersonalRecordEntity>> = personalRecordDao.getAllRecords()
    val chatMessages: Flow<List<ChatMessageEntity>> = chatMessageDao.getAllMessages()

    suspend fun initializeDefaultsIfNeeded() {
        val currentProfile = userProfileDao.getProfileDirect()
        if (currentProfile == null) {
            userProfileDao.insertOrUpdate(
                UserProfileEntity(
                    id = 1,
                    name = "Bhai",
                    nickname = "Gym Bro",
                    preferredLanguage = "Hinglish",
                    communicationStyle = "Direct, Friendly & Full Energy",
                    fitnessGoal = "4-Month Calisthenics Progression & Explosive Reps",
                    trainingExperience = "Intermediate",
                    heightCm = 174f,
                    weightKg = 68f,
                    currentAbilities = "Clean push-ups, pull-up progression, solid core stability",
                    preferences = "Strict form over ego lifting, 60-90s rest, roast mode active",
                    roastModeEnabled = true,
                    startDateEpoch = System.currentTimeMillis(),
                    currentMonth = 1
                )
            )

            // Seed initial baseline confirmed personal records
            personalRecordDao.insertOrUpdateRecord(
                PersonalRecordEntity(
                    exerciseName = "Pull-ups",
                    recordValue = 8,
                    unit = "reps",
                    isSingleSet = true,
                    dateIso = LocalDate.now().toString(),
                    status = "CONFIRMED",
                    notes = "Clean dead-hang to chin-over-bar form"
                )
            )
            personalRecordDao.insertOrUpdateRecord(
                PersonalRecordEntity(
                    exerciseName = "Chin-ups",
                    recordValue = 8,
                    unit = "reps",
                    isSingleSet = true,
                    dateIso = LocalDate.now().toString(),
                    status = "CONFIRMED",
                    notes = "Underhand supinated grip"
                )
            )
            personalRecordDao.insertOrUpdateRecord(
                PersonalRecordEntity(
                    exerciseName = "Normal push-ups",
                    recordValue = 20,
                    unit = "reps",
                    isSingleSet = true,
                    dateIso = LocalDate.now().toString(),
                    status = "CONFIRMED",
                    notes = "Full chest to floor depth"
                )
            )
            personalRecordDao.insertOrUpdateRecord(
                PersonalRecordEntity(
                    exerciseName = "Diamond push-ups",
                    recordValue = 12,
                    unit = "reps",
                    isSingleSet = true,
                    dateIso = LocalDate.now().toString(),
                    status = "CONFIRMED",
                    notes = "Strict tricep lockout"
                )
            )
            personalRecordDao.insertOrUpdateRecord(
                PersonalRecordEntity(
                    exerciseName = "Plank",
                    recordValue = 60,
                    unit = "seconds",
                    isSingleSet = true,
                    dateIso = LocalDate.now().toString(),
                    status = "CONFIRMED",
                    notes = "Solid hollow body hold"
                )
            )

            // Seed initial memory
            memoryDao.insertMemory(
                MemoryEntity(
                    category = "PERSONAL",
                    content = "User started 4-Month Calisthenics Routine. Loves direct motivation and playful roasting.",
                    status = "CONFIRMED"
                )
            )
            memoryDao.insertMemory(
                MemoryEntity(
                    category = "FITNESS",
                    content = "Focuses on strict calisthenics form: zero kipping on pull-ups and chest-to-floor on push-ups.",
                    status = "CONFIRMED"
                )
            )
        }
    }

    suspend fun getProfileDirect(): UserProfileEntity? = userProfileDao.getProfileDirect()

    suspend fun saveProfile(profile: UserProfileEntity) {
        userProfileDao.insertOrUpdate(profile)
    }

    suspend fun toggleRoastMode(enabled: Boolean) {
        userProfileDao.updateRoastMode(enabled)
    }

    suspend fun updateApiKey(apiKey: String) {
        userProfileDao.updateApiKey(apiKey)
    }

    suspend fun addMemory(category: String, content: String, status: String = "CONFIRMED") {
        memoryDao.insertMemory(
            MemoryEntity(
                category = category,
                content = content,
                status = status
            )
        )
    }

    suspend fun deleteMemory(id: Long) {
        memoryDao.deleteMemoryById(id)
    }

    suspend fun clearAllMemories() {
        memoryDao.clearAllMemories()
    }

    suspend fun startWorkoutSession(
        dateIso: String,
        dayOfWeek: String,
        workoutType: String,
        workoutTitle: String,
        isManualExtra: Boolean = false
    ): Long {
        return workoutSessionDao.insertSession(
            WorkoutSessionEntity(
                dateIso = dateIso,
                dayOfWeek = dayOfWeek,
                workoutType = workoutType,
                workoutTitle = workoutTitle,
                isManualExtra = isManualExtra,
                startTimeEpoch = System.currentTimeMillis(),
                isCompleted = false
            )
        )
    }

    suspend fun recordSet(
        sessionId: Long,
        exerciseName: String,
        setNumber: Int,
        targetReps: Int,
        completedReps: Int,
        completedDurationSecs: Int = 0,
        restSecondsRecorded: Int = 60,
        notes: String = ""
    ): Boolean {
        // Check if this is a new PR
        val existingPr = personalRecordDao.getRecordForExerciseDirect(exerciseName)
        val valueToCompare = if (completedDurationSecs > 0) completedDurationSecs else completedReps
        var isNewPr = false

        if (valueToCompare > 0) {
            if (existingPr == null || valueToCompare > existingPr.recordValue) {
                isNewPr = true
                personalRecordDao.insertOrUpdateRecord(
                    PersonalRecordEntity(
                        id = existingPr?.id ?: 0,
                        exerciseName = exerciseName,
                        recordValue = valueToCompare,
                        unit = if (completedDurationSecs > 0) "seconds" else "reps",
                        isSingleSet = true,
                        dateIso = LocalDate.now().toString(),
                        status = "CONFIRMED",
                        notes = "New PR logged in session #$sessionId"
                    )
                )
            }
        }

        workoutSetDao.insertSet(
            WorkoutSetEntity(
                sessionId = sessionId,
                exerciseName = exerciseName,
                setNumber = setNumber,
                targetReps = targetReps,
                completedReps = completedReps,
                completedDurationSecs = completedDurationSecs,
                restSecondsRecorded = restSecondsRecorded,
                isPersonalRecord = isNewPr,
                notes = notes
            )
        )

        return isNewPr
    }

    suspend fun completeWorkoutSession(
        sessionId: Long,
        durationSeconds: Int,
        notes: String = ""
    ) {
        val current = workoutSessionDao.getSessionById(sessionId)
        // update session
        workoutSessionDao.updateSession(
            WorkoutSessionEntity(
                id = sessionId,
                dateIso = LocalDate.now().toString(),
                dayOfWeek = LocalDate.now().dayOfWeek.name,
                workoutType = "PUSH",
                workoutTitle = "Completed Session",
                startTimeEpoch = System.currentTimeMillis() - (durationSeconds * 1000L),
                endTimeEpoch = System.currentTimeMillis(),
                durationSeconds = durationSeconds,
                notes = notes,
                isCompleted = true
            )
        )
    }

    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSetEntity>> {
        return workoutSetDao.getSetsForSession(sessionId)
    }

    suspend fun insertOrUpdatePersonalRecord(
        exerciseName: String,
        value: Int,
        unit: String,
        status: String = "CONFIRMED",
        notes: String = ""
    ) {
        val existing = personalRecordDao.getRecordForExerciseDirect(exerciseName)
        personalRecordDao.insertOrUpdateRecord(
            PersonalRecordEntity(
                id = existing?.id ?: 0,
                exerciseName = exerciseName,
                recordValue = value,
                unit = unit,
                isSingleSet = true,
                dateIso = LocalDate.now().toString(),
                status = status,
                notes = notes
            )
        )
    }

    suspend fun deletePersonalRecord(id: Long) {
        personalRecordDao.deleteRecordById(id)
    }

    suspend fun clearAllRecords() {
        personalRecordDao.clearAllRecords()
    }

    suspend fun addChatMessage(sender: String, message: String, isRoast: Boolean = false) {
        chatMessageDao.insertMessage(
            ChatMessageEntity(
                sender = sender,
                message = message,
                timestampEpoch = System.currentTimeMillis(),
                isRoast = isRoast
            )
        )
    }

    suspend fun clearChatHistory() {
        chatMessageDao.clearChatHistory()
    }

    suspend fun getRecentChat(limit: Int = 10): List<ChatMessageEntity> {
        return chatMessageDao.getRecentMessages(limit)
    }
}
