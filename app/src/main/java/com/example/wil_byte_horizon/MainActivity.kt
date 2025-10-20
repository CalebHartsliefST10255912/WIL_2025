package com.example.wil_byte_horizon

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ No auth redirect here – app opens freely
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Optional: show a subtle message if signed in
        FirebaseAuthManager.currentUser()?.email?.let { email ->
            Snackbar.make(binding.root, "Welcome back, $email", Snackbar.LENGTH_SHORT).show()
        }

        // ---- Bottom Navigation + NavController setup ----
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_train,
                R.id.navigation_soup,
                R.id.navigation_enrol,
                R.id.navigation_contact
            )
        )

        navView.setupWithNavController(navController)

        // ---- Custom toolbar setup ----
        val toolbar: Toolbar = findViewById(R.id.customToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // ---- Toolbar buttons ----
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
