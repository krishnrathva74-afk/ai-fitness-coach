package com.example.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MemoryEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.UserProfileEntity
import com.example.data.model.WorkoutSessionEntity
import com.example.data.model.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileDirect(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET roastModeEnabled = :enabled WHERE id = 1")
    suspend fun updateRoastMode(enabled: Boolean)

    @Query("UPDATE user_profile SET apiKeyOverride = :apiKey WHERE id = 1")
    suspend fun updateApiKey(apiKey: String)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY timestampEpoch DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Query("DELETE FROM memories WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)

    @Query("DELETE FROM memories")
    suspend fun clearAllMemories()
}

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY startTimeEpoch DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE id = :id")
    fun getSessionById(id: Long): Flow<WorkoutSessionEntity?>

    @Query("SELECT * FROM workout_sessions WHERE isCompleted = 1 ORDER BY endTimeEpoch DESC LIMIT 1")
    suspend fun getLatestCompletedSession(): WorkoutSessionEntity?

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE isCompleted = 1")
    fun getCompletedSessionsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Query("DELETE FROM workout_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)
}

@Dao
interface WorkoutSetDao {
    @Query("SELECT * FROM workout_sets WHERE sessionId = :sessionId ORDER BY id ASC")
    fun getSetsForSession(sessionId: Long): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE exerciseName = :exerciseName ORDER BY timestampEpoch DESC")
    fun getAllSetsForExercise(exerciseName: String): Flow<List<WorkoutSetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: WorkoutSetEntity): Long

    @Query("DELETE FROM workout_sets WHERE sessionId = :sessionId")
    suspend fun deleteSetsForSession(sessionId: Long)
}

@Dao
interface PersonalRecordDao {
    @Query("SELECT * FROM personal_records ORDER BY exerciseName ASC")
    fun getAllRecords(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE exerciseName = :exerciseName LIMIT 1")
    fun getRecordForExercise(exerciseName: String): Flow<PersonalRecordEntity?>

    @Query("SELECT * FROM personal_records WHERE exerciseName = :exerciseName LIMIT 1")
    suspend fun getRecordForExerciseDirect(exerciseName: String): PersonalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(pr: PersonalRecordEntity)

    @Query("DELETE FROM personal_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM personal_records")
    suspend fun clearAllRecords()
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestampEpoch ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestampEpoch DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatHistory()
}

@Database(
    entities = [
        UserProfileEntity::class,
        MemoryEntity::class,
        WorkoutSessionEntity::class,
        WorkoutSetEntity::class,
        PersonalRecordEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun memoryDao(): MemoryDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutSetDao(): WorkoutSetDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitness_coach_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
