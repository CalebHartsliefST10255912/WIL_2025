package com.example.wil_byte_horizon

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.wil_byte_horizon.auth.LoginActivity
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Moved earlier — initialize binding first
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Redirect check happens after binding is ready
        if (FirebaseAuthManager.currentUser() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // ⬇️ Everything below is your existing navigation and UI wiring
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_soup,
                R.id.navigation_train,
                R.id.navigation_contact,
                R.id.navigation_qualification,
            )
        )
        navView.setupWithNavController(navController)

        val btnNotifications: ImageButton = findViewById(R.id.btnNotifications)
        val btnSearch: ImageButton = findViewById(R.id.btnSearch)

        btnNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications clicked", Toast.LENGTH_SHORT).show()
        }

        btnSearch.setOnClickListener {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
