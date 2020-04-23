package com.example.coronawatch


import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MapFragmentTest{

    @get:Rule
    val homeActivity = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun isSearchBarDisplayed(){
        onView(withId(R.id.mapBtnView)).perform(ViewActions.click())
        onView(withId(R.id.filterIconView)).
            check(matches(isDisplayed()))

        onView(withId(R.id.searchCountryView)).
            check(matches(withHint(R.string.searchBarHint)))
    }

    @Test
    fun isFloatingActionDisplayed(){
        onView(withId(R.id.mapBtnView)).perform(ViewActions.click())
        onView(withId(R.id.changeDataDisplayView)).check(matches(isDisplayed()))
    }

    @Test
    fun isMapBoxDisplayed(){
        onView(withId(R.id.mapBtnView)).perform(ViewActions.click())
        onView(withId(R.id.mapBoxView)).check(matches(isDisplayed()))
    }

}