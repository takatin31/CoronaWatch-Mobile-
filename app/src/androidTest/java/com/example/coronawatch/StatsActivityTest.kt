package com.example.coronawatch

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.coronawatch.Activities.StatsActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class StatsActivityTest{

    @get:Rule
    val statsActivity = ActivityScenarioRule(StatsActivity::class.java)

    @get:Rule
    val intentsTestRule = IntentsTestRule(StatsActivity::class.java)

    @Test
    fun is_statsViewDisplayed(){
        onView(withId(R.id.statActivity)).check(matches(isDisplayed()))
    }

    @Test
    fun is_HeaderDisplayed(){
        onView(withId(R.id.countryImageView)).check(matches(isDisplayed()))
        onView(withId(R.id.lastUpdateTitle)).check(matches(withText(R.string.last_update)))
    }


    @Test
    fun is_InfectedDataDisplayed(){
        onView(withId(R.id.targetIconView)).check(matches(isDisplayed()))
        onView(withId(R.id.casesTitleView)).check(matches(withText(R.string.place_nbr_infected)))
    }

    @Test
    fun is_DeathDataDisplayed(){
        onView(withId(R.id.skullIconView)).check(matches(isDisplayed()))
        onView(withId(R.id.deathsTitleView)).check(matches(withText(R.string.place_nbr_deaths)))
    }

    @Test
    fun is_RecoveredDataDisplayed(){
        onView(withId(R.id.medicalIconView)).check(matches(isDisplayed()))
        onView(withId(R.id.caredTitleView)).check(matches(withText(R.string.place_nbr_cared)))
    }

    @Test
    fun isBarChartDisplayed(){
        onView(withId(R.id.barChart)).check(matches(isDisplayed()))
    }

    @Test
    fun isLineChartDisplayed(){
        onView(withId(R.id.chartsScrollView)).perform(scrollTo(), click())
        onView(withId(R.id.lineChart)).check(matches(isDisplayed()))
    }

}