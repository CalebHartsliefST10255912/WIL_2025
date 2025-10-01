package com.example.wil_byte_horizon.ui.train

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wil_byte_horizon.databinding.FragmentTrainBinding
import com.example.wil_byte_horizon.utils.NetworkUtils

class TrainFragment : Fragment() {

    private var _binding: FragmentTrainBinding? = null
    private val binding get() = _binding!!
    private var isOfflineMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNetworkAndSetupUI()
    }

    private fun checkNetworkAndSetupUI() {
        isOfflineMode = !NetworkUtils.isOnline(requireContext())

        if (isOfflineMode) {
            Toast.makeText(context, "Offline Mode: Training features limited", Toast.LENGTH_LONG).show()
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