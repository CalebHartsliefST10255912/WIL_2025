package com.example.wil_byte_horizon.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible // For easier visibility changes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wil_byte_horizon.data.local.AppDatabase
import com.example.wil_byte_horizon.data.repository.EventRepository
import com.example.wil_byte_horizon.databinding.FragmentAdminDashboardBinding
import com.example.wil_byte_horizon.utils.NetworkUtils // Import NetworkUtils
import kotlinx.coroutines.launch

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private val adminViewModel: AdminViewModel by viewModels {
        AdminViewModelFactory(
            EventRepository(AppDatabase.getDatabase(requireContext()).eventDao())
        )
    }

    private lateinit var eventAdapter: EventAdapter
    private var isOfflineMode = false // Track current mode

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        checkNetworkAndSetupUI() // Check network first
        observeViewModel()
    }

    private fun checkNetworkAndSetupUI() {
        isOfflineMode = !NetworkUtils.isOnline(requireContext())
        adminViewModel.updateReadOnlyState(isOfflineMode) // Inform ViewModel

        if (isOfflineMode) {
            Toast.makeText(context, "Offline Mode: Viewing only.", Toast.LENGTH_LONG).show()
            // In offline mode, hide/disable "Add Event" related UI
            binding.editTextEventTitle.isEnabled = false
            binding.editTextEventDescription.isEnabled = false
            binding.editTextEventDate.isEnabled = false
            binding.editTextEventLocation.isEnabled = false
            binding.buttonAddEvent.isEnabled = false
            binding.buttonAddEvent.alpha = 0.5f // Visually indicate it's disabled

            binding.buttonSyncEvents.isEnabled = false // Cannot sync offline
            binding.buttonSyncEvents.alpha = 0.5f

            // Optionally hide the input form container
            // binding.scrollViewInput.visibility = View.GONE
            binding.textViewOfflineNotice.visibility = View.VISIBLE // Show an offline notice
        } else {
            // Online mode, enable UI
            binding.editTextEventTitle.isEnabled = true
            binding.editTextEventDescription.isEnabled = true
            binding.editTextEventDate.isEnabled = true
            binding.editTextEventLocation.isEnabled = true
            binding.buttonAddEvent.isEnabled = true
            binding.buttonAddEvent.alpha = 1.0f

            binding.buttonSyncEvents.isEnabled = true
            binding.buttonSyncEvents.alpha = 1.0f
            // binding.scrollViewInput.visibility = View.VISIBLE
            binding.textViewOfflineNotice.visibility = View.GONE
        }
        setupEventSubmission() // Call this after UI state is set
    }


    private fun setupRecyclerView() {
        eventAdapter = EventAdapter()
        binding.recyclerViewEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }
    }

    private fun setupEventSubmission() {
        binding.buttonAddEvent.setOnClickListener {
            if (isOfflineMode) { // Double check, though button should be disabled
                Toast.makeText(context, "Cannot add events in offline mode.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val title = binding.editTextEventTitle.text.toString().trim()
            val description = binding.editTextEventDescription.text.toString().trim()
            val date = binding.editTextEventDate.text.toString().trim()
            val location = binding.editTextEventLocation.text.toString().trim()

            adminViewModel.addNewEvent(title, description, date, location)

            binding.editTextEventTitle.text?.clear()
            binding.editTextEventDescription.text?.clear()
            binding.editTextEventDate.text?.clear()
            binding.editTextEventLocation.text?.clear()
            binding.editTextEventTitle.requestFocus()
        }

        binding.buttonSyncEvents.setOnClickListener {
            if (isOfflineMode) { // Double check
                Toast.makeText(context, "Cannot sync in offline mode. Please check your connection.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            adminViewModel.syncLocalEvents()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adminViewModel.events.collect { eventsList ->
                    eventAdapter.submitList(eventsList)
                }
            }
        }

        adminViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        adminViewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                adminViewModel.clearToastMessage()
            }
        }

        // Observe the read-only state if you want to make other dynamic UI changes
        // adminViewModel.isReadOnly.observe(viewLifecycleOwner) { isReadOnly ->
        //    if (isReadOnly) { /* further UI changes if needed */ }
        // }
    }

    override fun onResume() {
        super.onResume()
        // Re-check network state when the fragment resumes, as it might have changed
        // This is a simple way; for more robust live updates, you'd use a BroadcastReceiver or LiveData from ConnectivityManager
        checkNetworkAndSetupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
