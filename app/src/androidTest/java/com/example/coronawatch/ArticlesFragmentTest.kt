package com.example.coronawatch

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import androidx.core.view.size
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
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
import com.example.coronawatch.Adapters.ArticleAdapter
import com.example.coronawatch.DataClasses.ArticleThumbnail
import com.example.coronawatch.Fragments.ArticlesFragment
import com.example.coronawatch.Request.RequestHandler
import com.example.coronawatch.Testing.EspressoIdelingResource
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



    @Before
    fun  initTest(){

        myFragment = ArticlesFragment()
        val transaction = activity.activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFragmentView, myFragment)
        myFragment.urlData = "${context.resources.getString(R.string.test_host)}/articles"
        transaction.commit()
        IdlingRegistry.getInstance().register(EspressoIdelingResource.countingIdlingResorce)
    }

    @After
    fun unregisterIdlingResource(){
        IdlingRegistry.getInstance().unregister(EspressoIdelingResource.countingIdlingResorce)
    }

    @Test
    fun is_FragmentViewDisplayed(){
        onView(ViewMatchers.withId(R.id.fragment_articles_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun is_contentCorrectlyDisplayed(){
        onView(ViewMatchers.withId(R.id.articleRecycler))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
/*        var v = myFragment.articleRecycler.findViewHolderForAdapterPosition(0) as ArticleAdapter.ArticleViewHolder

        if (v != null){
            Log.i("piiiiiii", v.titleArticle.text.toString())
        }*/
/*        for (i in 0 until myFragment.layoutManager.childCount){
            val v = myFragment.layoutManager.getChildAt(i)
            if (v != null){
                var title = v.findViewById<TextView>(R.id.article_title)
                Log.i("tiiiiiiiiiiiiiii", title.text.toString()+"khjk")
            }

        }
*/
        Log.i("siiiiiiiii", myFragment.articleRecycler.size.toString())
        for (v in myFragment.articleRecycler.children){
            var title = v.findViewById<TextView>(R.id.article_title)
            Log.i("tiiiiiiiiiiiiiii", title.text.toString())
        }
    }





}