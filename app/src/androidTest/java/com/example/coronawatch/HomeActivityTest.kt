package com.example.coronawatch

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class HomeActivityTest{

    @get:Rule
    val activityRule = ActivityScenarioRule(HomeActivity::class.java)

    @get:Rule
    var permissionAccessLocation = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @get:Rule
    var permissionCamera = GrantPermissionRule.grant(android.Manifest.permission.CAMERA)

    @get:Rule
    var permissionInternet = GrantPermissionRule.grant(android.Manifest.permission.INTERNET)

    @get:Rule
    var permissionWriteExternalStorage = GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


    @Test
    fun is_HomeViewDisplayed(){
        onView(withId(R.id.homeActivity)).check(matches(isDisplayed()))
        onView(withId(R.id.mainFragmentView)).check(matches(isDisplayed()))
    }

    @Test
    fun is_ProfileImageDisplayed(){
        onView(withId(R.id.profile_image)).check(matches(isDisplayed()))
    }

    @Test
    fun is_NotificationIconDisplayed(){
        onView(withId(R.id.notificationIconView)).check(matches(isDisplayed()))
    }

    @Test
    fun is_BottomButtonsDisplayed(){
        onView(withId(R.id.homeBtnView)).check(matches(isDisplayed()))
        onView(withId(R.id.homeBtnView)).
            check(matches(withText(R.string.home)))

        onView(withId(R.id.mapBtnView)).check(matches(isDisplayed()))
        onView(withId(R.id.mapBtnView)).
            check(matches(withText(R.string.map)))

        onView(withId(R.id.signalBtnView)).check(matches(isDisplayed()))
        onView(withId(R.id.signalBtnView)).
            check(matches(withText(R.string.signal)))

        onView(withId(R.id.contentBtnView)).check(matches(isDisplayed()))
        onView(withId(R.id.contentBtnView)).
            check(matches(withText(R.string.content)))

        onView(withId(R.id.btnAddContentView)).check(matches(isDisplayed()))
    }

    @Test
    fun is_HomeFragmentDisplayedOnClick(){
        onView(withId(R.id.homeBtnView)).perform(click())
        onView(withId(R.id.homeFragmentView)).check(matches(isDisplayed()))
    }

    @Test
    fun is_MapFragmentDisplayedOnClick(){
        onView(withId(R.id.mapBtnView)).perform(click())
        onView(withId(R.id.mapFragmentView)).check(matches(isDisplayed()))
    }

    @Test
    fun is_SignalFragmentDisplayedOnClick(){
        onView(withId(R.id.signalBtnView)).perform(click())
        onView(withId(R.id.signalFragmentView)).check(matches(isDisplayed()))
    }
}