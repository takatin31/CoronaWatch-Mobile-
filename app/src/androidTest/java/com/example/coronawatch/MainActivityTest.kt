package com.example.coronawatch

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.coronawatch.Activities.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest{

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun test_isMainActivityInView(){
        onView(withId(R.id.mainActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun test_isLogoDisplayed(){
        onView(withId(R.id.logoView)).check(matches(isDisplayed()))
    }

    @Test
    fun test_isMainTitleDisplayed(){
        onView(withId(R.id.mainTitleView)).
            check(matches(withText(R.string.app_title)))
    }

    @Test
    fun test_isSubTiteDisplayed(){
        onView(withId(R.id.subTitleView)).
            check(matches(withText(R.string.slogan)))
    }

    @Test
    fun test_isStartButtonDisplayed(){
        onView(withId(R.id.start_button)).check(matches(isDisplayed()))
        onView(withId(R.id.start_button)).
            check(matches(withText(R.string.start_btn)))
    }

    @Test
    fun test_isLoginDisplayedOnClick(){
        onView(withId(R.id.start_button)).perform(click())
        onView(withId(R.id.loginActivity)).check(matches(isDisplayed()))
    }


}