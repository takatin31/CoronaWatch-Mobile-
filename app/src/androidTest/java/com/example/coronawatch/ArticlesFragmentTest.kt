package com.example.coronawatch

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4Builder
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import com.example.coronawatch.Activities.HomeActivity
import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class ArticlesFragmentTest{

    @get:Rule
    val activityRule = ActivityScenarioRule(HomeActivity::class.java)

    @get:Rule
    var permissionInternet = GrantPermissionRule.grant(android.Manifest.permission.INTERNET)


}