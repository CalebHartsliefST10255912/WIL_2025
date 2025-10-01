package com.example.wil_byte_horizon.ui.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.databinding.FragmentContactBinding
import com.example.wil_byte_horizon.utils.NetworkUtils

class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!
    private var isOfflineMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNetworkAndSetupUI()
    }

    private fun checkNetworkAndSetupUI() {
        isOfflineMode = !NetworkUtils.isOnline(requireContext())

        if (isOfflineMode) {
            Toast.makeText(context, "Offline Mode: Contact form disabled", Toast.LENGTH_LONG).show()
            // Since we don't have the button IDs, just show the toast
        }
        // No UI changes since we don't have the button references
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