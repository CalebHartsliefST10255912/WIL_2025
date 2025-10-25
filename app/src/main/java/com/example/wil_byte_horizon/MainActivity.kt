package com.example.wil_byte_horizon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.wil_byte_horizon.core.AdminClaimsManager
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val authListener = FirebaseAuth.AuthStateListener {
        if (firebaseAuth.currentUser == null) goToLoginClearTask()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ---- Toolbar (defensive: fail early if missing) ----
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
            ?: error("MaterialToolbar with id 'topAppBar' not found in activity_main.xml. " +
                    "Make sure you included toolbar_top.xml and the id matches.")
        setSupportActionBar(binding.topAppBar)

        // ---- Nav setup using NavHostFragment ----
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as androidx.navigation.fragment.NavHostFragment
        val navController = navHost.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_event,
                R.id.navigation_enrol,
                R.id.navigation_about,
                R.id.contactFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView: BottomNavigationView = binding.navView
        navView.setupWithNavController(navController)

        // ---- Toolbar menu (Admin visibility + Logout) ----
        addMenuProvider(object : MenuProvider {
            private var adminItem: MenuItem? = null

            override fun onCreateMenu(menu: Menu, menuInflater: android.view.MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
                adminItem = menu.findItem(R.id.action_admin)

                lifecycleScope.launch {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        AdminClaimsManager.isAdminFlow().collect { isAdmin ->
                            adminItem?.isVisible = isAdmin
                        }
                    }
                }
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_admin -> {
                        navController.navigate(R.id.navigation_admin)
                        true
                    }
                    R.id.action_logout -> {
                        lifecycleScope.launch {
                            FirebaseAuthManager.logoutAll(this@MainActivity)
                            goToLoginClearTask()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, this, Lifecycle.State.STARTED)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authListener)
        if (FirebaseAuthManager.currentUser() == null) {
            goToLoginClearTask()
        }
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as? NavHostFragment
        return navHost?.navController?.navigateUp() ?: false || super.onSupportNavigateUp()
    }

    private fun goToLoginClearTask() {
        val i = Intent(this, com.example.wil_byte_horizon.auth.LoginActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(i)
        finish()
    }
}
