package com.example.coronawatch

import android.util.Log
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4Builder
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.coronawatch.Activities.HomeActivity
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.Fragments.ArticlesFragment
import com.example.coronawatch.Request.RequestHandler
import kotlinx.android.synthetic.main.fragment_articles.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RunWith(AndroidJUnit4ClassRunner::class)
class ArticlesFragmentTest{

    @get:Rule
    val activityRule = ActivityScenarioRule(HomeActivity::class.java)

    @get:Rule
    val activity = ActivityTestRule(HomeActivity::class.java)

    @get:Rule
    var permissionInternet = GrantPermissionRule.grant(android.Manifest.permission.INTERNET)

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    lateinit var myFragment : ArticlesFragment

    @Before
    fun  initTest(){
        myFragment = ArticlesFragment()
        val transaction = activity.activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragmentView, myFragment)
        myFragment.urlData = "${context.resources.getString(R.string.test_host)}/articles"
        transaction.commit()
    }

    @Test
    fun is_FragmentViewDisplayed(){
        onView(ViewMatchers.withId(R.id.fragment_articles_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun is_contentCorrectlyDisplayed(){
        while (myFragment.isLoading || myFragment.currentPage == 0){

        }
        Log.i("liiiiiiiiiiiii", myFragment.thumbnailArticleList.toString())
    }





}