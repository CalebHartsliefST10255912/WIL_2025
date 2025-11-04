package com.example.wil_byte_horizon.ui.enrol_form.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.databinding.FragmentStepBackgroundBinding
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormViewModel

class BackgroundStepFragment : Fragment() {

    private var _binding: FragmentStepBackgroundBinding? = null
    private val binding get() = _binding!!
    private val vm: EnrolFormViewModel by activityViewModels()

    private var showErrors = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepBackgroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Prefill from VM
        binding.inputHighestEducation.setText(vm.highestEducation.value, false)
        binding.inputEmploymentStatus.setText(vm.employmentStatus.value, false)
        binding.inputMotivation.setText(vm.motivation.value)

        // Adapters for exposed dropdowns
        val edu = resources.getStringArray(R.array.education_level_options)
        val emp = resources.getStringArray(R.array.employment_status_options)

        (binding.inputHighestEducation as AutoCompleteTextView).setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, edu)
        )
        (binding.inputEmploymentStatus as AutoCompleteTextView).setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, emp)
        )

        // Write to VM on selection/typing
        binding.inputHighestEducation.setOnItemClickListener { parent, _, pos, _ ->
            vm.highestEducation.value = parent.getItemAtPosition(pos).toString()
        }
        binding.inputEmploymentStatus.setOnItemClickListener { parent, _, pos, _ ->
            vm.employmentStatus.value = parent.getItemAtPosition(pos).toString()
        }

        binding.inputHighestEducation.doAfterTextChanged {
            vm.highestEducation.value = it?.toString().orEmpty()
        }
        binding.inputEmploymentStatus.doAfterTextChanged {
            vm.employmentStatus.value = it?.toString().orEmpty()
        }
        binding.inputMotivation.doAfterTextChanged {
            vm.motivation.value = it?.toString().orEmpty()
        }
    }

    /** Called by EnrolFormActivity when Next is pressed on this step. */
    fun onNextPressed(): Boolean {
        showErrors = true
        // Background step is optional according to ViewModel.validateStep(2); always pass.
        // If later you make fields required, add validators here and return their AND.
        return true
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
