package ru.lyrian.location_tracker.view.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import ru.lyrian.location_tracker.R
import ru.lyrian.location_tracker.databinding.ActivityMainBinding
import ru.lyrian.location_tracker.viewmodel.ViewModelsFactory
import ru.lyrian.location_tracker.viewmodel.activity.MainActivityViewModel
import javax.inject.Inject

/**
 * Application contains only one Activity to follow Single-Activity Pattern.
 */

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var userSignedIn = false
    private val mainActivityViewModel: MainActivityViewModel by createActivityViewModel()

    @Inject
    override lateinit var viewModelsFactory: ViewModelsFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        val view = this.binding.root
        setContentView(view)

        this.mainActivityViewModel.signedInStatusLD.observe(this) {
            this.userSignedIn = it
            setNavigationParameters()
        }

        this.mainActivityViewModel.checkSignedInStatus()
        suspendActivityDrawing()
    }

    private fun suspendActivityDrawing() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener { this.mainActivityViewModel.viewModelReady }
    }

    private fun setNavigationParameters() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fcvNavigationHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navInflater = navController.navInflater
        val navGraph = navInflater.inflate(R.navigation.navigation_graph)

        if (this.userSignedIn) {
            navGraph.startDestination = R.id.mapsFragment
        } else {
            navGraph.startDestination = R.id.signInFragment
        }

        navController.graph = navGraph
        navController.addOnDestinationChangedListener { _, destination, _ ->
            requestedOrientation = when (destination.id) {
                R.id.signInFragment -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                R.id.signUpFragment -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                R.id.mapsFragment -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                else -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
            }
        }
    }
}