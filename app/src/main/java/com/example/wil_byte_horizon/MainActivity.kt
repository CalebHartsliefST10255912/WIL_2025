package com.example.wil_byte_horizon

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.wil_byte_horizon.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        //  Setup AppBarConfiguration for Navigation
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_train,
                R.id.navigation_soup,
                R.id.navigation_qualification,
                R.id.navigation_contact
            )
        )

        //  Hook up bottom nav with navController
        navView.setupWithNavController(navController)

        //  Setup custom toolbar as ActionBar
        val toolbar: Toolbar = findViewById(R.id.customToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // hide default title

        //  Setup action bar buttons
        val btnNotifications: ImageButton = findViewById(R.id.btnNotifications)
        val btnSearch: ImageButton = findViewById(R.id.btnSearch)

        btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
            // navController.navigate(R.id.navigation_notifications)
        }

        btnSearch.setOnClickListener {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
            // navController.navigate(R.id.navigation_search)
        }
    }
}
