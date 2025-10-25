package com.example.wil_byte_horizon.ui.admin

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wil_byte_horizon.databinding.FragmentQualificationEditorBinding
import kotlinx.coroutines.flow.collectLatest

class QualificationEditorFragment : Fragment() {
    private var _binding: FragmentQualificationEditorBinding? = null
    private val binding get() = _binding!!
    private val vm: QualificationEditorViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQualificationEditorBinding.inflate(inflater, container, false)

        binding.btnSave.setOnClickListener {
            vm.upsert(
                id = binding.etId.text?.toString()?.trim().orEmpty(),
                title = binding.etTitle.text?.toString()?.trim().orEmpty(),
                description = binding.etDesc.text?.toString()?.trim().orEmpty(),
                category = binding.etCategory.text?.toString()?.trim().orEmpty(),
                isOpen = binding.cbOpen.isChecked
            )
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.state.collectLatest { s ->
                binding.progress.visibility = if (s.loading) View.VISIBLE else View.GONE
                if (s.error != null) Toast.makeText(requireContext(), s.error, Toast.LENGTH_LONG).show()
                if (s.saved) {
                    Toast.makeText(requireContext(), "Qualification saved.", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
