package com.ryanrvldo.spreadspectrumsteganography.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.ViewModelStore
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.ryanrvldo.spreadspectrumsteganography.R

inline fun <reified F : Fragment> initNavController(): NavController {
    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    val fragmentScenario = launchFragmentInContainer<F>(themeResId = R.style.AppTheme)

    fragmentScenario.onFragment {
        navController.setViewModelStore(ViewModelStore())
        navController.setGraph(R.navigation.main_navigation)
        Navigation.setViewNavController(it.requireView(), navController)
    }
    return navController
}