package com.example.coronawatch

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.example.coronawatch.Activities.HomeActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4ClassRunner::class)
class HomeFragmentTest{

    @get:Rule
    val homeActivity = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun are_titlesDisplayed(){
        onView(withId(R.id.currentStatsTextView)).
            check(matches(withText(R.string.current_stats)))

        onView(withId(R.id.nbrCasesTextView)).
            check(matches(withText(R.string.nbr_infected)))

        onView(withId(R.id.nbrDeathsTextView)).
            check(matches(withText(R.string.nbr_deaths)))

        onView(withId(R.id.nbrRecoveredTextView)).
            check(matches(withText(R.string.nbr_cared)))

        onView(withId(R.id.positionStateTextView)).
            check(matches(withText(R.string.postion_state)))
    }

    @Test
    fun is_figureDisplayed(){
        onView(withId(R.id.closestDistanceLabelView)).
            check(matches(withText(R.string.closestDistanceLabel)))

        onView(withId(R.id.dangerTextView)).
            check(matches(withText(R.string.dangerLabel)))

        onView(withId(R.id.positionLabelView)).
            check(matches(withText(R.string.yourPositionLabel)))

        onView(withId(R.id.closestZoneLabelView)).
            check(matches(withText(R.string.closestDangerZoneLabel)))

        onView(withId(R.id.pointBottomView)).check(matches(isDisplayed()))
        onView(withId(R.id.pointTopView)).check(matches(isDisplayed()))
        onView(withId(R.id.deviderView)).check(matches(isDisplayed()))
    }

    @Test
    fun is_ProgressDisplayed(){

        onView(withId(R.id.progressBarNbrCared)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBarNbrDeaths)).check(matches(isDisplayed()))
        onView(withId(R.id.progressBarNbrInfected)).check(matches(isDisplayed()))
    }
}