package com.video.downloading.app.downloader.online.app.actvities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.find.lost.app.phone.utils.SharedPrefUtils
import com.find.your.phone.app.utils.PermissionsUtils
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
        setContentView(R.layout.activity_main)
        if (!SharedPrefUtils.getBooleanData(this@MainActivity, "isTerms")) {
            startActivity(Intent(applicationContext, TermsAndConditionsActivity::class.java))
            finish()

        } else {
            val navView: BottomNavigationView = findViewById(R.id.nav_view)

            navController = findNavController(R.id.nav_host_fragment)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home,
                    R.id.navigation_paste_link,
                    R.id.navigation_dashboard
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
            if (Build.VERSION.SDK_INT >= 23) {
                val permissionsUtils = PermissionsUtils().getInstance(this)
                if (permissionsUtils?.isAllPermissionAvailable()!!) {
                    Log.d(TAGI, "Permission")
                } else {
                    permissionsUtils.setActivity(this)
                    permissionsUtils.requestPermissionsIfDenied()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}