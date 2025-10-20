package com.example.wil_byte_horizon.ui.enrol_form.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.core.widget.doAfterTextChanged
import com.example.wil_byte_horizon.databinding.FragmentStepPersonalBinding
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormViewModel

class PersonalStepFragment : Fragment() {

    private var _binding: FragmentStepPersonalBinding? = null
    private val binding get() = _binding!!
    private val vm: EnrolFormViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStepPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initial fill
        binding.inputFullName.setText(vm.fullName.value)
        binding.inputIdNumber.setText(vm.idNumber.value)
        binding.inputDob.setText(vm.dateOfBirth.value)

        // listeners
        binding.inputFullName.doAfterTextChanged { vm.fullName.value = it?.toString().orEmpty() }
        binding.inputIdNumber.doAfterTextChanged { vm.idNumber.value = it?.toString().orEmpty() }
        binding.inputDob.doAfterTextChanged { vm.dateOfBirth.value = it?.toString().orEmpty() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
