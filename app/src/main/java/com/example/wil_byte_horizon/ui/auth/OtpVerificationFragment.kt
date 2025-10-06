package com.example.wil_byte_horizon.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.ui.home.HomeFragment

class OtpVerificationFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_otp_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.verifyOtpButton).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, HomeFragment())
                .commit()
        }
    }
}