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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStepBackgroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputHighestEducation.setText(vm.highestEducation.value)
        binding.inputEmploymentStatus.setText(vm.employmentStatus.value)
        binding.inputMotivation.setText(vm.motivation.value)

        binding.inputHighestEducation.doAfterTextChanged { vm.highestEducation.value = it?.toString().orEmpty() }
        binding.inputEmploymentStatus.doAfterTextChanged { vm.employmentStatus.value = it?.toString().orEmpty() }
        binding.inputMotivation.doAfterTextChanged { vm.motivation.value = it?.toString().orEmpty() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
