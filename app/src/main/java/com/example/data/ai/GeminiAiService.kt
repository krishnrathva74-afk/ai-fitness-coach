package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.MemoryEntity
import com.example.data.model.PersonalRecordEntity
import com.example.data.model.UserProfileEntity
import com.example.data.routine.DefaultRoutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.concurrent.TimeUnit

class GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getCoachResponse(
        userMessage: String,
        userProfile: UserProfileEntity?,
        memories: List<MemoryEntity>,
        personalRecords: List<PersonalRecordEntity>,
        roastModeEnabled: Boolean,
        activeWorkoutSummary: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = when {
            !userProfile?.apiKeyOverride.isNullOrBlank() -> userProfile!!.apiKeyOverride
            try { BuildConfig.GEMINI_API_KEY.isNotBlank() && !BuildConfig.GEMINI_API_KEY.contains("MY_GEMINI") } catch (e: Exception) { false } -> BuildConfig.GEMINI_API_KEY
            else -> ""
        }

        val systemPrompt = buildSystemPrompt(
            userProfile = userProfile,
            memories = memories,
            personalRecords = personalRecords,
            roastModeEnabled = roastModeEnabled,
            activeWorkoutSummary = activeWorkoutSummary
        )

        if (apiKey.isBlank()) {
            return@withContext getOfflineCoachFallback(
                userMessage = userMessage,
                roastMode = roastModeEnabled,
                userProfile = userProfile,
                personalRecords = personalRecords
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val jsonPayload = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", userMessage))
                        })
                    })
                })
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemPrompt))
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", if (roastModeEnabled) 0.8 else 0.4)
                    put("topP", 0.95)
                    put("maxOutputTokens", 600)
                })
            }

            val requestBody = jsonPayload.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    val text = parts?.optJSONObject(0)?.optString("text")
                    if (!text.isNullOrBlank()) {
                        return@withContext text.trim()
                    }
                }
            }

            // If API key had error or quota, use high precision intelligent local coach fallback
            return@withContext getOfflineCoachFallback(
                userMessage = userMessage,
                roastMode = roastModeEnabled,
                userProfile = userProfile,
                personalRecords = personalRecords
            )
        } catch (e: Exception) {
            return@withContext getOfflineCoachFallback(
                userMessage = userMessage,
                roastMode = roastModeEnabled,
                userProfile = userProfile,
                personalRecords = personalRecords
            )
        }
    }

    private fun buildSystemPrompt(
        userProfile: UserProfileEntity?,
        memories: List<MemoryEntity>,
        personalRecords: List<PersonalRecordEntity>,
        roastModeEnabled: Boolean,
        activeWorkoutSummary: String?
    ): String {
        val today = LocalDate.now()
        val todayRoutine = DefaultRoutine.getTodayRoutine()

        val prString = if (personalRecords.isEmpty()) "No confirmed PRs stored yet."
        else personalRecords.joinToString("\n") { "- ${it.exerciseName}: ${it.recordValue} ${it.unit} (${it.status})" }

        val memoryString = if (memories.isEmpty()) "No explicit memories saved yet."
        else memories.joinToString("\n") { "- [${it.category}] [${it.status}]: ${it.content}" }

        return """
You are a close male friend, gym buddy, and serious personal calisthenics fitness coach for the user (${userProfile?.name ?: "Bhai"}).

### 1. MOST IMPORTANT RULE (CRITICAL)
- You MUST answer the user's EXACT question first.
- If the user asks “Aaj kya workout hai?” -> State today's scheduled workout directly.
- If the user asks “Kitne reps karu?” -> Give the specific target reps based on their current exercise or routine.
- If the user asks “Pull-up aur chin-up mein kya difference hai?” -> Explain the grip and muscle difference clearly.
- If the user asks “Mera record kya hai?” -> Check stored confirmed records.
- If the user asks “Ab next exercise kya hai?” -> State the exact next exercise in the routine.
- DO NOT change the subject, do not invent questions the user did not ask, do not automatically start a different workout, and do not provide unrelated filler.
- If the request is unclear, ask only the minimum necessary clarification.
- If information is missing or not stored, say so directly. NEVER hallucinate records or history.

### 2. TONE & LANGUAGE
- Natural Hindi/Hinglish, casual, energetic, brotherhood vibe.
- Use words like “bhai”, “yaar”, “mitrr” naturally without overusing them.
- Match length: short question -> short concise answer; detailed question ("detail mein bata") -> clear breakdown.

### 3. ROAST MODE: ${if (roastModeEnabled) "ON (FULL GAALI ENABLED 🔥)" else "OFF (SUPPORTIVE COACHING)"}
${if (roastModeEnabled) """
- You can playfully roast, tease, and use authentic Hindi gym slang/gaaliyan (e.g. bhai, saala, chutiya, madarchod, behenchod, bhosdike, harami, land, gaand, kutta).
- IMPORTANT: Roasting must be in context and playful. FIRST answer the question accurately, then add the roast/gaali.
- NEVER insult body shape, weight, attractiveness, disability, medical conditions, mental health, or real sensitive personal info in a hurtful way.
- If the user reports genuine pain, injury, or illness, immediately drop all roasts and switch to serious supportive safety advice.
""" else """
- Keep it encouraging, friendly, and supportive with zero heavy gaaliyan.
"""}

### 4. SAFETY & RECOVERY
- If the user reports sharp pain, dizziness, fainting, or chest pain: instruct them to stop immediately and seek medical advice. Do not diagnose.
- Supplements: Conservative guidance. Emphasize whole food, hydration, sleep over pills/powders.
- Progressive Overload: Gradual progression, strict form, consistency over ego maxing.

### 5. STORED USER CONTEXT
- Name: ${userProfile?.name ?: "Bhai"} (${userProfile?.nickname ?: "Gym Bro"})
- Goal: ${userProfile?.fitnessGoal ?: "Calisthenics Strength"}
- Today is: ${today.dayOfWeek.name} (${today})
- Scheduled Today: ${todayRoutine.title} (${if (todayRoutine.isRestDay) "REST DAY" else todayRoutine.workoutType})
${if (!todayRoutine.isRestDay) "Exercises today: " + todayRoutine.exercises.joinToString(", ") { "${it.name} (${it.targetSets}x${it.targetRepsOrSecs})" } else ""}
${if (activeWorkoutSummary != null) "Current In-Progress Workout: $activeWorkoutSummary" else ""}

### STORED PERSONAL RECORDS:
$prString

### STORED USER MEMORIES:
$memoryString
""".trimIndent()
    }

    private fun getOfflineCoachFallback(
        userMessage: String,
        roastMode: Boolean,
        userProfile: UserProfileEntity?,
        personalRecords: List<PersonalRecordEntity>
    ): String {
        val query = userMessage.lowercase().trim()
        val today = LocalDate.now()
        val routine = DefaultRoutine.getTodayRoutine()
        val name = userProfile?.name ?: "Bhai"

        // 1. "Aaj kya workout hai?" / Today's workout
        if (query.contains("aaj kya") || query.contains("today") || query.contains("aaj ka workout") || query.contains("routine")) {
            return if (routine.isRestDay) {
                if (roastMode) {
                    "Aaj ${today.dayOfWeek.name} hai $name, REST DAY hai saale! 💤 Kuch aaram kar le, body rest mein grow karti hai land ke, gym mein overtrain karke aag mat laga."
                } else {
                    "Aaj ${today.dayOfWeek.name} hai $name — Rest day hai! Aaram se recover karo, hydration aur balanced nutrition pe dhyan do bhai."
                }
            } else {
                val exList = routine.exercises.joinToString("\n") { "• ${it.name}: ${it.targetSets} sets × ${it.targetRepsOrSecs} (Rest: ${it.restRangeDesc})" }
                if (roastMode) {
                    "Aaj ${today.dayOfWeek.name} ka workout hai: ${routine.title} 🔥\n$exList\n\nAb bahane mat bana chutiye, chal start kar workout!"
                } else {
                    "Aaj ${today.dayOfWeek.name} ka scheduled workout hai: ${routine.title} 💪\n$exList\n\nProper warm-up karo aur strict form se execute karo bhai!"
                }
            }
        }

        // 2. "Pull-up aur chin-up mein kya difference hai?" / Chin up vs Pull up
        if ((query.contains("pull-up") || query.contains("pull up")) && (query.contains("chin-up") || query.contains("chin up") || query.contains("difference") || query.contains("diff"))) {
            return if (roastMode) {
                "Sun dhyan se $name:\n1. Pull-up: Overhand grip (palms facing away), wide grip. Ye majorly LATS (back width) aur upper back train karta hai.\n2. Chin-up: Underhand grip (palms facing you), shoulder-width. Isme BICEPS aur lats dono heavily engage hote hain.\n\nSamjha ya abhi bhi confuse hai madarchod? Dono strict form se lagane hain!"
            } else {
                "Dono mein primary difference grip aur muscle focus ka hai bhai:\n• Pull-up: Overhand (palms facing away) grip — focus Lats (Back) aur upper back pe hota hai.\n• Chin-up: Underhand (palms facing you) grip — Biceps aur lats dono par strong load aata hai.\nForm clean rakho aur bina swing kare karo!"
            }
        }

        // 3. "Mera record kya hai?" / PR inquiry
        if (query.contains("record") || query.contains("pr") || query.contains("best")) {
            val prInfo = if (personalRecords.isEmpty()) {
                "Currently koi confirmed PR stored nahi hai bhai."
            } else {
                personalRecords.joinToString("\n") { "• ${it.exerciseName}: ${it.recordValue} ${it.unit} (${it.status})" }
            }
            return if (roastMode) {
                "Ye rahe tere confirmed personal records $name:\n$prInfo\n\nAur sun, isko dekh ke khush mat ho ja bhosdike, ab isse aage nikalna hai!"
            } else {
                "Ye rahe aapke confirmed personal records:\n$prInfo\n\nConsistency bani rahegi toh har month progressive overload se ye easily beat honge bhai!"
            }
        }

        // 4. "Kitne reps karu?" / Reps
        if (query.contains("kitne reps") || query.contains("reps")) {
            return if (roastMode) {
                "Push-ups ke 15 reps, Diamond ke 10, Pull-ups ke 6-10 reps target rakh. Form chudne lage toh wahin set rok de, ego reps mat maar land ke!"
            } else {
                "Exercise ke hisab se targets hain bhai: Standard Push-ups 15 reps, Diamond 10 reps, Pull-ups 6–10 reps, aur Squats 15–20 reps. Form clean honi chahiye!"
            }
        }

        // 5. "Creatine" or supplement question
        if (query.contains("creatine") || query.contains("protein") || query.contains("supplement")) {
            return "Supplements zaroori nahi hain bhai. Pehle clean ghar ka khana (dal, paneer, eggs/chicken, sprouts, milk), 3-4 litre paani, aur 8 ghante ki neend fix karo. Creatine lena ho toh basic monohydrate 3g daily kaafi hai, but family/doctor se consult karke lena best hai."
        }

        // 6. Pain or injury
        if (query.contains("dard") || query.contains("pain") || query.contains("chot") || query.contains("injury") || query.contains("dizziness")) {
            return "⚠️ Bhai workout turant rok do! Sharp pain ya injury mein exercise continue nahi karni. Ice lagao, rest do aur agar pain persist kare toh doctor ko check karao. Health first hai!"
        }

        // Default response
        return if (roastMode) {
            "Sahi se bol $name, exact kya janna hai? Workout, exercise form, rest timer, ya apna PR check karna hai? Time waste mat kar chutiye, bata kya help chahiye!"
        } else {
            "Haan bhai $name, batao kya doubt hai? Workout details, exercise form, rest duration ya PRs ke bare mein kuch poochna hai toh batao!"
        }
    }
}
