package com.example.wil_byte_horizon.ui.enrol_form.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wil_byte_horizon.databinding.FragmentStepBackgroundBinding
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormViewModel

class BackgroundStepFragment : Fragment() {

    private var _binding: FragmentStepBackgroundBinding? = null
    private val binding get() = _binding!!
    private val vm: EnrolFormViewModel by activityViewModels()
    private var showErrors = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStepBackgroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputHighestEducation.setText(vm.highestEducation.value)
        binding.inputEmploymentStatus.setText(vm.employmentStatus.value)
        binding.inputMotivation.setText(vm.motivation.value)

        // Dropdowns: validate on both text change and item click
        binding.inputHighestEducation.doAfterTextChanged {
            vm.highestEducation.value = it?.toString().orEmpty()
            if (showErrors) validateHighestEducation()
        }
        binding.inputHighestEducation.setOnItemClickListener { _,_,_,_ ->
            vm.highestEducation.value = binding.inputHighestEducation.text?.toString().orEmpty()
            if (showErrors) validateHighestEducation()
        }

        binding.inputEmploymentStatus.doAfterTextChanged {
            vm.employmentStatus.value = it?.toString().orEmpty()
            if (showErrors) validateEmploymentStatus()
        }
        binding.inputEmploymentStatus.setOnItemClickListener { _,_,_,_ ->
            vm.employmentStatus.value = binding.inputEmploymentStatus.text?.toString().orEmpty()
            if (showErrors) validateEmploymentStatus()
        }

        binding.inputMotivation.doAfterTextChanged {
            vm.motivation.value = it?.toString().orEmpty()
            if (showErrors) validateMotivation()
        }
    }

    /** Host calls this on Next. */
    fun onNextPressed(): Boolean {
        showErrors = true
        val ok = validateHighestEducation() && validateEmploymentStatus() && validateMotivation()
        if (!ok) focusFirstError()
        return ok
    }

    private fun validateHighestEducation(): Boolean {
        val ok = binding.inputHighestEducation.text?.toString()?.isNotBlank() == true
        binding.inputHighestEducationLayout.error = if (showErrors && !ok) "Please select your highest education." else null
        return ok
    }

    private fun validateEmploymentStatus(): Boolean {
        val ok = binding.inputEmploymentStatus.text?.toString()?.isNotBlank() == true
        binding.inputEmploymentStatusLayout.error = if (showErrors && !ok) "Please select your employment status." else null
        return ok
    }

    private fun validateMotivation(): Boolean {
        val v = binding.inputMotivation.text?.toString()?.trim().orEmpty()
        val ok = v.length >= 10
        binding.inputMotivationLayout.error = if (showErrors && !ok) "Please provide at least 10 characters." else null
        return ok
    }

    private fun focusFirstError() {
        when {
            binding.inputHighestEducationLayout.error != null -> binding.inputHighestEducation.requestFocus()
            binding.inputEmploymentStatusLayout.error != null -> binding.inputEmploymentStatus.requestFocus()
            binding.inputMotivationLayout.error != null       -> binding.inputMotivation.requestFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
