package com.example.coronawatch

import android.util.Log
import android.widget.TextView
import androidx.core.view.get
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.coronawatch.Activities.ArticleActivity
import com.example.coronawatch.Adapters.ArticleAdapter
import com.example.coronawatch.Adapters.CommentAdapter
import com.example.coronawatch.Request.ApiManager
import com.example.coronawatch.Testing.EspressoIdelingResource
import com.example.coronawatch.Testing.FakeData
import kotlinx.android.synthetic.main.activity_article.*
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


@RunWith(AndroidJUnit4ClassRunner::class)
class ArticleActivityTest{

    @get:Rule
    val activityRule = ActivityScenarioRule(ArticleActivity::class.java)

    @get:Rule
    val activity = ActivityTestRule(ArticleActivity::class.java)

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun initTest(){

        val fakeUrl = "${context.resources.getString(R.string.test_host)}/"

        val mockedApiManager = mock(ApiManager::class.java)
        `when`(mockedApiManager.getApiUrl()).thenReturn(fakeUrl)

        activity.activity.apiManager = mockedApiManager

        IdlingRegistry.getInstance().register(EspressoIdelingResource.countingIdlingResorce)
        activity.activity.getArticle(0)
        activity.activity.getComments(0)
    }

    @After
    fun unregisterIdlingResource(){
        IdlingRegistry.getInstance().unregister(EspressoIdelingResource.countingIdlingResorce)
    }

    @Test
    fun isLayoutVisible(){
        onView(withId(R.id.layout_article_activity))
            .check(matches(isDisplayed()))
    }


    @Test
    fun is_contentDisplayedCorrectly(){
        val testArticle = FakeData.article

        //Thread.sleep(10000)
        onView(withId(R.id.articleTitle))
            .check(matches(withText(testArticle.title)))

        onView(withId(R.id.articleDate))
            .check(matches(withText(testArticle.date)))

        onView(withId(R.id.`userImageٍView`))
            .check(matches(isDisplayed()))

    }

    @Test
    fun comment_is_succefull(){
        val fakeUrl = "${context.resources.getString(R.string.test_host)}/new"

        val mockedApiManager = mock(ApiManager::class.java)
        `when`(mockedApiManager.getApiUrl()).thenReturn(fakeUrl)

        activity.activity.apiManager = mockedApiManager

        val testingComment = "testingComment"

        onView(withId(R.id.commentEditText))
            .perform((replaceText(testingComment)))

        onView(withId(R.id.addCommentBtn))
            .perform(click())

        activity.activity.getComments(0)

        onView(withId(R.id.commentsRecycler))
            .check(matches(isDisplayed()))

        val viewH = activity.activity.commentsRecycler.findViewHolderForAdapterPosition(0) as CommentAdapter.CommentViewHolder

        if (viewH.commentContent.text.toString() != testingComment){
            assert(false)
        }else{
            assert(true)
        }

    }

}