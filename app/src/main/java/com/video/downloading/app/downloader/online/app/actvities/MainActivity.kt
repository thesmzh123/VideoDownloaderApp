package com.video.downloading.app.downloader.online.app.actvities

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.find.your.phone.app.utils.PermissionsUtils
import com.video.downloading.app.downloader.online.app.R
import com.video.downloading.app.downloader.online.app.utils.Constants.TAGI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
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