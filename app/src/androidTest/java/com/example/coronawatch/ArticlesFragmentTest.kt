package com.example.coronawatch

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
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
import com.example.coronawatch.Activities.ArticleActivity
import com.example.coronawatch.Activities.HomeActivity
import com.example.coronawatch.Activities.LoginActivity
import com.example.coronawatch.Adapters.ArticleAdapter
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.Fragments.ArticlesFragment
import com.example.coronawatch.Request.RequestHandler
import com.example.coronawatch.Testing.EspressoIdelingResource
import com.example.coronawatch.Testing.FakeData
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_articles.*
import org.junit.*
import org.junit.Assert.*
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

    val LIST_ITEM_SIZE = 2
    val listThumbnailArticles = FakeData.thumbnailArticles


    //initialiser les ressources necessaires pour le test
    @Before
    fun  initTest(){
        myFragment = ArticlesFragment()
        val transaction = activity.activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragmentView, myFragment)
        myFragment.urlData = "${context.resources.getString(R.string.test_host)}/articles"
        transaction.commit()
        IdlingRegistry.getInstance().register(EspressoIdelingResource.countingIdlingResorce)
    }

    //utiliser pour mettre le tests en pause jusqu'a la fin de la requette
    @After
    fun unregisterIdlingResource(){
        IdlingRegistry.getInstance().unregister(EspressoIdelingResource.countingIdlingResorce)
    }

    //verifier si le fragmenet est affiché
    @Test
    fun is_FragmentViewDisplayed(){
        onView(withId(R.id.fragment_articles_layout))
            .check(matches(isDisplayed()))
    }

    //Verifier si la liste des articles est affichée
    @Test
    fun is_contentCorrectlyDisplayed(){
        onView(withId(R.id.articleRecycler))
            .check(matches(isDisplayed()))
    }

    //Verifier si l'activite articleActivity est affichée aprés avoir cliqué sur un element
    @Test
    fun articleThumbnail_isClickPossible(){
        onView(withId(R.id.articleRecycler)).
            perform(RecyclerViewActions.actionOnItemAtPosition<ArticleAdapter.ArticleViewHolder>(0, click()))
    }






}