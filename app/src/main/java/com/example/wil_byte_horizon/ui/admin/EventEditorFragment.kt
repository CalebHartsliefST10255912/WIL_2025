package com.example.wil_byte_horizon.ui.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wil_byte_horizon.databinding.FragmentEventEditorBinding
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

class EventEditorFragment : Fragment() {
    private var _binding: FragmentEventEditorBinding? = null
    private val binding get() = _binding!!

    private val vm: EventEditorViewModel by viewModels()

    private var posterUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            posterUri = uri
            binding.tvPoster.setText(uri.lastPathSegment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventEditorBinding.inflate(inflater, container, false)

        binding.btnPickPoster.setOnClickListener { pickImage.launch("image/*") }

        binding.etStart.setOnClickListener { showDateTimePicker { millis -> binding.etStart.setText(millis.toString()); vm.setStart(millis) } }
        binding.etEnd.setOnClickListener { showDateTimePicker { millis -> binding.etEnd.setText(millis.toString()); vm.setEnd(millis) } }

        binding.btnSave.setOnClickListener {
            vm.saveEvent(
                title = binding.etTitle.text?.toString().orEmpty(),
                description = binding.etDesc.text?.toString().orEmpty(),
                locationName = binding.etLocation.text?.toString().orEmpty(),
                lat = binding.etLat.text?.toString()?.toDoubleOrNull(),
                lng = binding.etLng.text?.toString()?.toDoubleOrNull(),
                poster = posterUri
            )
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.state.collectLatest { s ->
                binding.progress.visibility = if (s.loading) View.VISIBLE else View.GONE
                if (s.error != null) Toast.makeText(requireContext(), s.error, Toast.LENGTH_LONG).show()
                if (s.saved) {
                    Toast.makeText(requireContext(), "Event saved.", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        return binding.root
    }

    private fun showDateTimePicker(onMillis: (Long) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, y, m, d ->
            TimePickerDialog(requireContext(), { _, h, min ->
                val c = Calendar.getInstance().apply { set(y, m, d, h, min, 0); set(Calendar.MILLISECOND, 0) }
                onMillis(c.timeInMillis)
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
