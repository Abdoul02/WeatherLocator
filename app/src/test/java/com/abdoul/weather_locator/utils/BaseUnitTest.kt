package com.abdoul.weather_locator.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule

open class BaseUnitTest {

    @get:Rule
    var coroutinesTestRule = MainCoroutineScopeRule()

    @get:Rule
    val instantTaskExecutorRule =
        InstantTaskExecutorRule() //Allows execution of LiveData to happen instantly
}