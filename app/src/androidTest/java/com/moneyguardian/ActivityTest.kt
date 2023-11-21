package com.moneyguardian

import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActivityTest {
    @Test
    fun testEvent() {
        launchActivity<MainActivity>().use { scenario ->

            onView(withId(R.id.profileButton)).perform(click())

            // Activity under test is now finished.

            val resultCode = scenario.result.resultCode
            Assert.assertEquals(resultCode, 0);
            val resultData = scenario.result.resultData
            println(resultData);
        }
    }
}
