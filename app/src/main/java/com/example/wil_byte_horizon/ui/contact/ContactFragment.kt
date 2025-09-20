package com.example.wil_byte_horizon.ui.contact

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.auth.LoginActivity
import com.example.wil_byte_horizon.core.FirebaseAuthManager

class ContactFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        // Get reference to the logout button
        val btnLogout: Button = view.findViewById(R.id.btnLogout)

        // Handle logout
        btnLogout.setOnClickListener {
            FirebaseAuthManager.logout()

            // Navigate back to LoginActivity
            val intent = Intent(requireContext(), LoginActivity::class.java)
            // Clear the back stack so user can't go back with back button
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
