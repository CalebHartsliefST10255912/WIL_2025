package com.example.wil_byte_horizon.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {
    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)

        binding.btnAddEvent.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_admin_to_eventEditorFragment)
        }
        binding.btnAddQualification.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_admin_to_qualificationEditorFragment)
        }
        // Optional: add "Manage existing" screens later

        return binding.root
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
