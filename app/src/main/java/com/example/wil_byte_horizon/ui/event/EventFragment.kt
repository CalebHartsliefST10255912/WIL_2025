package com.example.wil_byte_horizon.ui.event

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wil_byte_horizon.core.AdminClaimsManager
import com.example.wil_byte_horizon.data.admin.AdminRepository
import com.example.wil_byte_horizon.databinding.FragmentEventBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private val vm: EventViewModel by viewModels()
    private val adminRepo = AdminRepository()

    private var adapter: EventsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        binding.recyclerEvents.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1) Build adapter when admin flag arrives (only when it changes)
                launch {
                    AdminClaimsManager.isAdminFlow()
                        .distinctUntilChanged()
                        .collectLatest { isAdmin ->
                            adapter = EventsAdapter(
                                isAdmin = isAdmin,
                                onOpenMap = { ui -> startActivity(buildMapIntent(ui)) },
                                onEdit = { ui -> showEditEventDialog(ui) },
                                onDelete = { ui -> confirmDeleteEvent(ui) }
                            ).also { binding.recyclerEvents.adapter = it }

                            // Immediately paint current snapshot to avoid waiting for a re-emit
                            adapter?.submitList(vm.state.value.events)
                        }
                }

                // 2) Collect UI state to drive chrome + list updates
                launch {
                    vm.state.collectLatest { state ->
                        binding.progress.isVisible = state.isLoading
                        binding.textOffline.isVisible = state.isOfflineSource
                        binding.textError.isVisible = state.error != null
                        binding.textError.text = state.error ?: ""
                        binding.empty.isVisible =
                            !state.isLoading && state.events.isEmpty() && state.error == null

                        adapter?.submitList(state.events)
                    }
                }
            }
        }
    }

    private fun showEditEventDialog(e: UiEvent) {
        EditEventDialog(e) { /* list updates via snapshot listener */ }
            .show(parentFragmentManager, "edit_event")
    }

    private fun confirmDeleteEvent(e: UiEvent) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete this event?")
            .setMessage(e.title)
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    runCatching { adminRepo.deleteEvent(e.id) }
                        .onFailure {
                            Toast.makeText(
                                requireContext(),
                                it.message ?: "Delete failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun buildMapIntent(ui: UiEvent): Intent {
        val label = Uri.encode(ui.title.ifBlank { ui.locationName })
        val uri = when {
            ui.lat != null && ui.lng != null ->
                Uri.parse("geo:${ui.lat},${ui.lng}?q=${ui.lat},${ui.lng}($label)")
            ui.locationName.isNotBlank() ->
                Uri.parse("geo:0,0?q=${Uri.encode(ui.locationName)}")
            else -> Uri.parse("geo:0,0")
        }
        return Intent(Intent.ACTION_VIEW, uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        _binding = null
    }
}
