package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "Bhai",
    val nickname: String = "Gym Bro",
    val preferredLanguage: String = "Hinglish",
    val communicationStyle: String = "Casual, Friendly & Direct",
    val fitnessGoal: String = "Calisthenics Strength & Progressive Overload",
    val trainingExperience: String = "Beginner / Intermediate",
    val heightCm: Float? = 175f,
    val weightKg: Float? = 68f,
    val currentAbilities: String = "Push-ups, Pull-ups, Core calisthenics routine",
    val preferences: String = "Strict form, 4-month progression, honest feedback",
    val roastModeEnabled: Boolean = true,
    val startDateEpoch: Long = System.currentTimeMillis(),
    val currentMonth: Int = 1,
    val apiKeyOverride: String = ""
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // PERSONAL, FITNESS, GOAL, PREFERENCE, RECORD
    val content: String,
    val status: String = "CONFIRMED", // CONFIRMED, UNCERTAIN, GUESS
    val timestampEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateIso: String,
    val dayOfWeek: String,
    val workoutType: String, // PUSH, PULL, LEGS_CORE, REST, MANUAL
    val workoutTitle: String,
    val isManualExtra: Boolean = false,
    val startTimeEpoch: Long = System.currentTimeMillis(),
    val endTimeEpoch: Long? = null,
    val durationSeconds: Int = 0,
    val notes: String = "",
    val isCompleted: Boolean = false
)

@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val targetReps: Int = 10,
    val completedReps: Int = 0,
    val completedDurationSecs: Int = 0,
    val restSecondsRecorded: Int = 60,
    val isPersonalRecord: Boolean = false,
    val notes: String = "",
    val timestampEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseName: String,
    val recordValue: Int,
    val unit: String = "reps", // reps or seconds
    val isSingleSet: Boolean = true,
    val dateIso: String,
    val status: String = "CONFIRMED", // CONFIRMED or UNCERTAIN
    val notes: String = "",
    val updatedAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // USER or AI
    val message: String,
    val timestampEpoch: Long = System.currentTimeMillis(),
    val isRoast: Boolean = false
)
