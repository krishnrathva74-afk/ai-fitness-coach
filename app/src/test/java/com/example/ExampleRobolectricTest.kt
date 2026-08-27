package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.routine.DefaultRoutine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.DayOfWeek

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AI Fitness Coach", appName)
    }

    @Test
    fun `verify routine definitions`() {
        val mondayRoutine = DefaultRoutine.getRoutineForDay(DayOfWeek.MONDAY)
        assertEquals("PUSH", mondayRoutine.workoutType)
        assertTrue(mondayRoutine.exercises.isNotEmpty())

        val thursdayRoutine = DefaultRoutine.getRoutineForDay(DayOfWeek.THURSDAY)
        assertTrue(thursdayRoutine.isRestDay)

        val pullUpGuide = DefaultRoutine.ALL_EXERCISE_GUIDES["Pull-ups"]
        assertNotNull(pullUpGuide)
        assertEquals(4, pullUpGuide?.targetSets)
    }
}
