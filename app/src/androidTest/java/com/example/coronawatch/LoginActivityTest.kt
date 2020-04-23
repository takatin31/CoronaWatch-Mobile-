package com.example.coronawatch

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)

class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)


    @Test
    fun test_isLoginViewDisplayed(){
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_isLoginTitleDisplayed(){
        onView(withId(R.id.enterTitle)).
            check(matches(withText(R.string.enter)))
    }

    @Test
    fun test_isLoginSubTitleDisplayed(){
        onView(withId(R.id.subLoginTitle)).
            check(matches(withText(R.string.login_slogan)))
    }

    @Test
    fun test_isFacebookLoginSubDisplayed(){
        onView(withId(R.id.facebookTextView)).
            check(matches(withText(R.string.facebook)))
    }

    @Test
    fun test_isGoogleLoginDisplayed(){
        onView(withId(R.id.googleTextView)).
            check(matches(withText(R.string.gmail)))
    }
}