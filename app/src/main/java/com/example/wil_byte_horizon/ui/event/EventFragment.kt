package com.example.wil_byte_horizon.ui.event

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wil_byte_horizon.databinding.FragmentEventBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private val vm: EventViewModel by viewModels()
    private lateinit var adapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        setupRecycler()
        observeState()
        return binding.root
    }

    private fun setupRecycler() {
        adapter = EventsAdapter(
            onOpenMap = { ui ->
                val label = android.net.Uri.encode(ui.title.ifBlank { ui.locationName })
                val uri = when {
                    ui.lat != null && ui.lng != null ->
                        android.net.Uri.parse("geo:${ui.lat},${ui.lng}?q=${ui.lat},${ui.lng}($label)")
                    ui.locationName.isNotBlank() ->
                        android.net.Uri.parse("geo:0,0?q=${android.net.Uri.encode(ui.locationName)}")
                    else -> android.net.Uri.parse("geo:0,0")
                }
                startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, uri))
            }
        )
        binding.recyclerEvents.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        binding.recyclerEvents.adapter = adapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.state.collect { state: EventsUiState ->
                    binding.progress.isVisible = state.isLoading
                    binding.textOffline.isVisible = state.isOfflineSource
                    binding.textError.isVisible = state.error != null
                    binding.textError.text = state.error ?: ""
                    binding.empty.isVisible = !state.isLoading && state.events.isEmpty() && state.error == null
                    adapter.submitList(state.events)
                }
            }
        }
    }

    private fun buildMapIntent(ui: UiEvent): Intent {
        val label = Uri.encode(ui.title.ifBlank { ui.locationName })
        val uri = when {
            ui.lat != null && ui.lng != null -> Uri.parse("geo:${ui.lat},${ui.lng}?q=${ui.lat},${ui.lng}($label)")
            ui.locationName.isNotBlank() -> Uri.parse("geo:0,0?q=${Uri.encode(ui.locationName)}")
            else -> Uri.parse("geo:0,0") // fallback
        }
        return Intent(Intent.ACTION_VIEW, uri)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
