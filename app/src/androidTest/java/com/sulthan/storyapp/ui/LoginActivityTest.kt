package com.sulthan.storyapp.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sulthan.storyapp.JsonConverter
import com.sulthan.storyapp.R
import com.sulthan.storyapp.data.remote.retrofit.ApiConfig
import com.sulthan.storyapp.ui.view.LoginActivity
import com.sulthan.storyapp.util.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setup() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginAndLogout() {
        ActivityScenario.launch(LoginActivity::class.java)

        Espresso.onView(withId(R.id.emailTextInput)).perform(typeText("zz@z.com"), closeSoftKeyboard())
        Espresso.onView(withId(R.id.passwordTextInput)).perform(typeText("12345678"), closeSoftKeyboard())
        Espresso.onView(withId(R.id.loginButton)).perform(click())

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        Thread.sleep(2000L)
        Espresso.onView(withText("Continue")).perform(click())

        Intents.init()
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(withText("Log out")).perform(click())

        Thread.sleep(1000L)
    }
}