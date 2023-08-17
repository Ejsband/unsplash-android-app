package com.project.androidunsplash

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project.androidunsplash.databinding.ActivityMainBinding
import com.project.androidunsplash.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("AppCompatMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.boarding) {
                binding.navView.visibility = View.GONE
            } else {
                binding.navView.visibility = View.VISIBLE
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_main,
                R.id.navigation_favourite_collection_list,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#9eea4a")))
    }

    private fun handleDeepLink(intent: Intent) {
        Log.d("XXXXX", "Your ${intent.data}")
        if (intent.action != Intent.ACTION_VIEW) return
        val deepLinkUrl = intent.data ?: return
        if (deepLinkUrl.queryParameterNames.contains("code")) {

            val authCode = deepLinkUrl.getQueryParameter("code") ?: return

            Log.d("XXXXX", "Your token is $authCode")

            viewModel.createMetaData(
                "iC2wjAuzgkMrgNmeG5P15wh8FG1xLflKe0A8urB2ZcI",
                "DK4b0QlPT-U_djGFwNsEM_gPOCzmk65ooPyzYyy1y_Q",
                "app://oauth",
                authCode,
                "authorization_code"
            )

            viewModel.isTokenCorrect.observe(this) {
                if (it) Navigation.findNavController(this, R.id.nav_host_fragment_activity_main)
                    .navigate(R.id.action_boarding_to_navigation_main)
            }
        } else {
            return
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleDeepLink(it) }
    }
}