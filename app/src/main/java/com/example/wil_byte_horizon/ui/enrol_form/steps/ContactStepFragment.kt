package com.example.wil_byte_horizon.ui.enrol_form.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wil_byte_horizon.databinding.FragmentStepContactBinding
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormViewModel

class ContactStepFragment : Fragment() {

    private var _binding: FragmentStepContactBinding? = null
    private val binding get() = _binding!!
    private val vm: EnrolFormViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStepContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.inputEmail.setText(vm.email.value)
        binding.inputPhone.setText(vm.phone.value)
        binding.inputAddress1.setText(vm.address1.value)
        binding.inputAddress2.setText(vm.address2.value)
        binding.inputCity.setText(vm.city.value)
        binding.inputProvince.setText(vm.province.value)
        binding.inputPostalCode.setText(vm.postalCode.value)

        binding.inputEmail.doAfterTextChanged { vm.email.value = it?.toString().orEmpty() }
        binding.inputPhone.doAfterTextChanged { vm.phone.value = it?.toString().orEmpty() }
        binding.inputAddress1.doAfterTextChanged { vm.address1.value = it?.toString().orEmpty() }
        binding.inputAddress2.doAfterTextChanged { vm.address2.value = it?.toString().orEmpty() }
        binding.inputCity.doAfterTextChanged { vm.city.value = it?.toString().orEmpty() }
        binding.inputProvince.doAfterTextChanged { vm.province.value = it?.toString().orEmpty() }
        binding.inputPostalCode.doAfterTextChanged { vm.postalCode.value = it?.toString().orEmpty() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
