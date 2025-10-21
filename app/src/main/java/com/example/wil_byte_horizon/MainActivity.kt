package com.example.wil_byte_horizon

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var btnLogout: ImageButton

    // Keep UI in sync with auth changes (e.g., after returning from LoginActivity)
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val authListener = FirebaseAuth.AuthStateListener {
        updateLogoutVisibility()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // No auth gate here — app opens freely
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ---- Top banner / toolbar ----
        val toolbar: Toolbar = findViewById(R.id.customToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        btnLogout = findViewById(R.id.btnLogout)
        updateLogoutVisibility()

        btnLogout.setOnClickListener {
            // 1) Sign out of Firebase
            FirebaseAuthManager.logout()

            // 2) Clear Credential Manager (Google/passkeys) state (non-blocking)
            val cm = CredentialManager.create(this)
            lifecycleScope.launch {
                try { cm.clearCredentialState(ClearCredentialStateRequest()) } catch (_: Exception) {}
            }

            // 3) Feedback + update UI
            updateLogoutVisibility()
            Snackbar.make(binding.root, getString(R.string.logged_out), Snackbar.LENGTH_SHORT).show()

            // Optional: if you want to send them to the login screen after logout:
            // startActivity(Intent(this, com.example.wil_byte_horizon.auth.LoginActivity::class.java)
            //     .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }

        // Optional greeting if already signed in
        FirebaseAuthManager.currentUser()?.email?.let { email ->
            Snackbar.make(binding.root, "Welcome back, $email", Snackbar.LENGTH_SHORT).show()
        }

        // ---- Bottom Navigation + NavController ----
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_train,
                R.id.navigation_soup,
                R.id.navigation_enrol,
                R.id.navigation_about
            )
        )

        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authListener)
    }

    private fun updateLogoutVisibility() {
        val isSignedIn = FirebaseAuthManager.currentUser() != null
        btnLogout.visibility = if (isSignedIn) View.VISIBLE else View.GONE
    }
}
