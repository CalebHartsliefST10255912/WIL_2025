package com.example.wil_byte_horizon.ui.qualif

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wil_byte_horizon.databinding.FragmentQualificationsBinding
import com.example.wil_byte_horizon.utils.NetworkUtils

class QualificationFragment : Fragment() {

    private var _binding: FragmentQualificationsBinding? = null
    private val binding get() = _binding!!
    private var isOfflineMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQualificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNetworkAndSetupUI()
    }

    private fun checkNetworkAndSetupUI() {
        isOfflineMode = !NetworkUtils.isOnline(requireContext())

        if (isOfflineMode) {
            Toast.makeText(context, "Offline Mode: Viewing only", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        checkNetworkAndSetupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}