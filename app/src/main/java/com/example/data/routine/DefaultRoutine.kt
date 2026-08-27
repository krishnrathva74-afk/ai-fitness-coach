package com.example.data.routine

import java.time.DayOfWeek
import java.time.LocalDate

data class ExercisePlan(
    val id: String,
    val name: String,
    val hindiName: String,
    val targetSets: Int,
    val targetRepsOrSecs: Int,
    val isDuration: Boolean = false,
    val restSecondsDefault: Int = 60,
    val restRangeDesc: String,
    val musclesWorked: String,
    val handPosition: String,
    val bodyPosition: String,
    val movement: String,
    val basicForm: String,
    val commonMistakes: String
)

data class DayWorkoutRoutine(
    val dayOfWeek: DayOfWeek,
    val workoutType: String,
    val title: String,
    val hindiTagline: String,
    val description: String,
    val isRestDay: Boolean,
    val exercises: List<ExercisePlan>
)

object DefaultRoutine {

    val ALL_EXERCISE_GUIDES: Map<String, ExercisePlan> = mapOf(
        "Normal push-ups" to ExercisePlan(
            id = "normal_pushups",
            name = "Normal push-ups",
            hindiName = "स्टैंडर्ड पुश-अप्स",
            targetSets = 3,
            targetRepsOrSecs = 15,
            restSecondsDefault = 60,
            restRangeDesc = "60 sec",
            musclesWorked = "Chest (Pectorals), Front Shoulders (Anterior Deltoid), Triceps, Core",
            handPosition = "Hands slightly wider than shoulder-width, fingers pointing forward or slightly outward.",
            bodyPosition = "Straight rigid line from head to heels. Squeeze glutes and brace core (no sagging hips).",
            movement = "Lower chest until it touches/hovers 1 inch above floor, elbows flared at 45 degrees, then push up powerfully to full extension.",
            basicForm = "Keep neck neutral, elbows tucked at ~45° angle, full range of motion without bouncing.",
            commonMistakes = "Sagging lower back, elbows flaring out 90°, half reps (not going low enough)."
        ),
        "Diamond push-ups" to ExercisePlan(
            id = "diamond_pushups",
            name = "Diamond push-ups",
            hindiName = "डायमंड पुश-अप्स",
            targetSets = 3,
            targetRepsOrSecs = 10,
            restSecondsDefault = 60,
            restRangeDesc = "60 sec",
            musclesWorked = "Triceps (Heavy emphasis), Inner Chest, Anterior Deltoids, Core",
            handPosition = "Hands touching under chest, thumbs and index fingers forming a diamond/triangle shape.",
            bodyPosition = "Tight plank position, feet together or slightly apart for balance.",
            movement = "Lower chest directly towards the center of the diamond shape with elbows tracking close to ribs, then press back up.",
            basicForm = "Control the descent, don't let elbows flare out wide, keep core braced.",
            commonMistakes = "Elbows flaring too wide causing wrist/elbow strain, arching the back."
        ),
        "Feet-elevated push-ups" to ExercisePlan(
            id = "feet_elevated_pushups",
            name = "Feet-elevated push-ups",
            hindiName = "डिक्लाइन (फीट एलिवेटेड) पुश-अप्स",
            targetSets = 3,
            targetRepsOrSecs = 10,
            restSecondsDefault = 75,
            restRangeDesc = "60–90 sec",
            musclesWorked = "Upper Chest (Clavicular head), Front Shoulders, Triceps, Serratus Anterior",
            handPosition = "Shoulder-width or slightly wider on the floor.",
            bodyPosition = "Feet elevated on a bench, chair, or bed (12-18 inches). Body in straight rigid decline line.",
            movement = "Lower upper chest towards floor under strict control, press back up lockout.",
            basicForm = "Keep core squeezed so hips don't drop down. Lead with upper chest.",
            commonMistakes = "Hips sagging down, lifting butt too high in the air."
        ),
        "Pike push-ups" to ExercisePlan(
            id = "pike_pushups",
            name = "Pike push-ups",
            hindiName = "पाइक पुश-अप्स (शोल्डर पावर)",
            targetSets = 3,
            targetRepsOrSecs = 8,
            restSecondsDefault = 75,
            restRangeDesc = "60–90 sec",
            musclesWorked = "Shoulders (Deltoids), Upper Chest, Triceps, Upper Traps, Core",
            handPosition = "Shoulder-width on floor, fingers splayed forward.",
            bodyPosition = "Hips driven high in the air forming an inverted 'V' shape. Look towards toes/knees.",
            movement = "Lower head forward of hands creating a tripod shape, push back up and through to inverted V.",
            basicForm = "Head travels forward on descent and presses back up through shoulders.",
            commonMistakes = "Dropping head directly between hands instead of forward, flattening into regular pushup."
        ),
        "Plank" to ExercisePlan(
            id = "plank",
            name = "Plank",
            hindiName = "प्लैंक होल्ड (कोर स्टेबिलिटी)",
            targetSets = 3,
            targetRepsOrSecs = 60,
            isDuration = true,
            restSecondsDefault = 60,
            restRangeDesc = "45–60 sec",
            musclesWorked = "Rectus Abdominis, Transverse Abdominis, Obliques, Lower Back, Glutes",
            handPosition = "Forearms flat on ground, elbows aligned directly under shoulders.",
            bodyPosition = "Straight solid board from head to toes. Glutes squeezed, pelvis tucked slightly (posterior tilt).",
            movement = "Isometric hold: pull belly button inward, breathe steadily, resist gravity without moving.",
            basicForm = "Keep shoulders engaged (push away from ground), neck relaxed, legs engaged.",
            commonMistakes = "Lower back sagging, holding breath, hiking hips up like a mountain."
        ),
        "Pull-ups" to ExercisePlan(
            id = "pullups",
            name = "Pull-ups",
            hindiName = "पुल-अप्स (ओवरहैंड ग्रिप)",
            targetSets = 4,
            targetRepsOrSecs = 8,
            restSecondsDefault = 90,
            restRangeDesc = "90–120 sec",
            musclesWorked = "Lats (Latissimus Dorsi), Upper Back, Rear Delts, Biceps, Forearms, Core",
            handPosition = "Overhand pronated grip (palms facing away), slightly wider than shoulder-width.",
            bodyPosition = "Dead hang start with straight arms, engage scapula (shoulders down from ears), hollow body or slight arch.",
            movement = "Pull elbows down towards hips until chin clearly clears the bar, pause briefly, lower down under control.",
            basicForm = "Initiate with scapular depression, drive elbows down, full lock out at bottom.",
            commonMistakes = "Kicking legs / kipping, chin reaching forward without pulling bar to chest, half reps."
        ),
        "Chin-ups" to ExercisePlan(
            id = "chinups",
            name = "Chin-ups",
            hindiName = "चिन-अप्स (अंडरहैंड ग्रिप)",
            targetSets = 3,
            targetRepsOrSecs = 8,
            restSecondsDefault = 90,
            restRangeDesc = "90–120 sec",
            musclesWorked = "Biceps Brachii, Lats, Lower Traps, Forearms / Grip, Core",
            handPosition = "Underhand supinated grip (palms facing you), shoulder-width apart.",
            bodyPosition = "Hang from bar, core tight, legs crossed or together in front.",
            movement = "Pull chest up to bar driving elbows into sides until chin crosses over the bar, then lower with a controlled 2-second eccentric.",
            basicForm = "Full extension at bottom, squeeze biceps and lats at peak contraction.",
            commonMistakes = "Swinging body for momentum, not going all the way down, rounding upper back excessively."
        ),
        "Dead hang" to ExercisePlan(
            id = "dead_hang",
            name = "Dead hang",
            hindiName = "डेड हैंग (ग्रिप और स्पाइन डीकंप्रेशन)",
            targetSets = 2,
            targetRepsOrSecs = 30,
            isDuration = true,
            restSecondsDefault = 60,
            restRangeDesc = "60 sec",
            musclesWorked = "Forearms, Grip strength, Lats, Shoulders, Spinal Decompression",
            handPosition = "Overhand grip on bar, shoulder-width.",
            bodyPosition = "Feet off floor, full dead hang, let spine stretch out naturally with deep calm breathing.",
            movement = "Passive or active hang: grip bar tight, maintain relaxed breathing throughout the timer.",
            basicForm = "Relax back and core, let gravity decompress spinal discs while maintaining solid grip.",
            commonMistakes = "Letting grip slip prematurely, holding breath, tensing neck muscles."
        ),
        "Squats" to ExercisePlan(
            id = "squats",
            name = "Squats",
            hindiName = "बॉडीवेट स्क्वैट्स",
            targetSets = 3,
            targetRepsOrSecs = 20,
            restSecondsDefault = 60,
            restRangeDesc = "45–60 sec",
            musclesWorked = "Quadriceps, Glutes, Hamstrings, Calves, Core",
            handPosition = "Hands held out in front for counterbalance or clasped at chest.",
            bodyPosition = "Feet shoulder-width apart, toes turned out 15–30 degrees, chest tall.",
            movement = "Hing hips back and bend knees, descend until thighs are at least parallel to floor, drive through mid-foot to stand.",
            basicForm = "Keep knees tracking over toes, weight balanced on feet, spine neutral.",
            commonMistakes = "Knees caving inward (valgus), heels coming off floor, rounding lower back (butt wink)."
        ),
        "Lunges" to ExercisePlan(
            id = "lunges",
            name = "Lunges",
            hindiName = "वॉकिंग / स्टेशनरी लंजेस",
            targetSets = 3,
            targetRepsOrSecs = 10,
            restSecondsDefault = 60,
            restRangeDesc = "60 sec",
            musclesWorked = "Quadriceps, Glutes, Hamstrings, Hip Stabilizers, Balance",
            handPosition = "Hands on hips or held at chest for balance.",
            bodyPosition = "Torso upright, step forward one stride length.",
            movement = "Lower back knee until it hovers 1 inch above floor, front knee at 90°, press back up through front heel.",
            basicForm = "Keep torso vertical, front knee aligned with front foot.",
            commonMistakes = "Stepping too short, slamming back knee into floor, leaning forward excessively."
        ),
        "Calf raises" to ExercisePlan(
            id = "calf_raises",
            name = "Calf raises",
            hindiName = "काफ रेजेज",
            targetSets = 3,
            targetRepsOrSecs = 20,
            restSecondsDefault = 45,
            restRangeDesc = "45–60 sec",
            musclesWorked = "Gastrocnemius, Soleus (Calf muscles), Ankle stability",
            handPosition = "Lightly touching wall for balance.",
            bodyPosition = "Stand tall on balls of feet (elevate on a step for extra stretch if available).",
            movement = "Raise heels as high as possible, squeeze calves at top for 1 full second, lower down slowly.",
            basicForm = "Full range of motion: deep stretch at bottom, explosive squeeze at top.",
            commonMistakes = "Bouncing reps quickly without pause, partial range of motion."
        ),
        "Leg raises" to ExercisePlan(
            id = "leg_raises",
            name = "Leg raises",
            hindiName = "लाइंग / हैंगिंग लेग रेजेज",
            targetSets = 3,
            targetRepsOrSecs = 12,
            restSecondsDefault = 60,
            restRangeDesc = "45–60 sec",
            musclesWorked = "Lower Abs, Hip Flexors, Rectus Abdominis, Obliques",
            handPosition = "Flat on ground by hips (or holding bar if hanging).",
            bodyPosition = "Lying flat on back, lower back pressed firmly into floor (no gap).",
            movement = "Lift straight legs until perpendicular to floor, then slowly lower them until 2 inches above floor without arching back.",
            basicForm = "Press lower back into floor at all times; bend knees slightly if hamstrings are tight.",
            commonMistakes = "Lower back arching off floor during lowering, swinging legs with momentum."
        )
    )

    fun getRoutineForDay(dayOfWeek: DayOfWeek): DayWorkoutRoutine {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> DayWorkoutRoutine(
                dayOfWeek = DayOfWeek.MONDAY,
                workoutType = "PUSH",
                title = "Monday Push Blast",
                hindiTagline = "Chest, Shoulders & Triceps ka din bhai! 🔥",
                description = "Build upper-body pushing power with targeted chest and tricep calisthenics.",
                isRestDay = false,
                exercises = listOf(
                    ALL_EXERCISE_GUIDES["Normal push-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 15),
                    ALL_EXERCISE_GUIDES["Diamond push-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 10),
                    ALL_EXERCISE_GUIDES["Feet-elevated push-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 10),
                    ALL_EXERCISE_GUIDES["Pike push-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 8),
                    ALL_EXERCISE_GUIDES["Plank"]!!.copy(targetSets = 3, targetRepsOrSecs = 55)
                )
            )
            DayOfWeek.TUESDAY -> DayWorkoutRoutine(
                dayOfWeek = DayOfWeek.TUESDAY,
                workoutType = "PULL",
                title = "Tuesday Pull Strength",
                hindiTagline = "Wings & Biceps phadne ka time! 🦅",
                description = "Back width, bicep density, and grip stamina with pull-ups and dead hangs.",
                isRestDay = false,
                exercises = listOf(
                    ALL_EXERCISE_GUIDES["Pull-ups"]!!.copy(targetSets = 4, targetRepsOrSecs = 8),
                    ALL_EXERCISE_GUIDES["Chin-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 8),
                    ALL_EXERCISE_GUIDES["Dead hang"]!!.copy(targetSets = 2, targetRepsOrSecs = 30)
                )
            )
            DayOfWeek.WEDNESDAY -> DayWorkoutRoutine(
                dayOfWeek = DayOfWeek.WEDNESDAY,
                workoutType = "LEGS_CORE",
                title = "Wednesday Legs + Core",
                hindiTagline = "Chicken legs nahi chalenge bhai! 🍗💥",
                description = "High-rep calisthenics leg foundation paired with core stabilization.",
                isRestDay = false,
                exercises = listOf(
                    ALL_EXERCISE_GUIDES["Squats"]!!.copy(targetSets = 3, targetRepsOrSecs = 18),
                    ALL_EXERCISE_GUIDES["Lunges"]!!.copy(targetSets = 3, targetRepsOrSecs = 10),
                    ALL_EXERCISE_GUIDES["Calf raises"]!!.copy(targetSets = 3, targetRepsOrSecs = 20),
                    ALL_EXERCISE_GUIDES["Leg raises"]!!.copy(targetSets = 3, targetRepsOrSecs = 12),
                    ALL_EXERCISE_GUIDES["Plank"]!!.copy(targetSets = 3, targetRepsOrSecs = 50)
                )
            )
            DayOfWeek.THURSDAY -> DayWorkoutRoutine(
                dayOfWeek = DayOfWeek.THURSDAY,
                workoutType = "REST",
                title = "Thursday Active Recovery",
                hindiTagline = "Aaram se recover karo bhai, muscle rest mein banti hai! 💤",
                description = "No heavy workout. Hydrate well, stretch, eat balanced nutrition and rest.",
                isRestDay = true,
                exercises = emptyList()
            )
            DayOfWeek.FRIDAY -> DayWorkoutRoutine(
                dayOfWeek = DayOfWeek.FRIDAY,
                workoutType = "PUSH",
                title = "Friday Push Heavy",
                hindiTagline = "Weekend se pehle pump pura hona chahiye! 💪",
                description = "Repeat Monday Push progression with strict form and progressive overload.",
                isRestDay = false,
                exercises = listOf(
                    ALL_EXERCISE_GUIDES["Normal push-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 15),
                    ALL_EXERCISE_GUIDES["Diamond push-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 10),
                    ALL_EXERCISE_GUIDES["Feet-elevated push-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 10),
                    ALL_EXERCISE_GUIDES["Pike push-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 8),
                    ALL_EXERCISE_GUIDES["Plank"]!!.copy(targetSets = 3, targetRepsOrSecs = 55)
                )
            )
            DayOfWeek.SATURDAY -> DayWorkoutRoutine(
                dayOfWeek = DayOfWeek.SATURDAY,
                workoutType = "PULL",
                title = "Saturday Pull + Light Legs/Core",
                hindiTagline = "Full body athletic finisher! ⚡",
                description = "High efficiency hybrid workout targeting back, arms, legs and core.",
                isRestDay = false,
                exercises = listOf(
                    ALL_EXERCISE_GUIDES["Pull-ups"]!!.copy(targetSets = 3, targetRepsOrSecs = 8),
                    ALL_EXERCISE_GUIDES["Chin-ups"]!!.copy(targetSets = 2, targetRepsOrSecs = 8),
                    ALL_EXERCISE_GUIDES["Squats"]!!.copy(targetSets = 2, targetRepsOrSecs = 15),
                    ALL_EXERCISE_GUIDES["Calf raises"]!!.copy(targetSets = 2, targetRepsOrSecs = 18),
                    ALL_EXERCISE_GUIDES["Leg raises"]!!.copy(targetSets = 2, targetRepsOrSecs = 12)
                )
            )
            DayOfWeek.SUNDAY -> DayWorkoutRoutine(
                dayOfWeek = DayOfWeek.SUNDAY,
                workoutType = "REST",
                title = "Sunday Full Rest & Recharge",
                hindiTagline = "Sunday chill karo, kal se naya week shuru! 🧘",
                description = "Full recovery day. Review your weekly PRs, sleep 8+ hours, and prepare for Monday.",
                isRestDay = true,
                exercises = emptyList()
            )
        }
    }

    fun getTodayRoutine(): DayWorkoutRoutine {
        return getRoutineForDay(LocalDate.now().dayOfWeek)
    }

    fun getAllWorkouts(): List<DayWorkoutRoutine> {
        return DayOfWeek.values().map { getRoutineForDay(it) }
    }
}
