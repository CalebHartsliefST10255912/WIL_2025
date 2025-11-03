package com.example.wil_byte_horizon.ui.enrol_form.steps

import android.os.Bundle
import android.util.Patterns
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
    private var showErrors = false

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

        binding.inputEmail.doAfterTextChanged {
            vm.email.value = it?.toString().orEmpty()
            if (showErrors) validateEmail()
        }
        binding.inputPhone.doAfterTextChanged {
            vm.phone.value = it?.toString().orEmpty()
            if (showErrors) validatePhone()
        }
        binding.inputAddress1.doAfterTextChanged {
            vm.address1.value = it?.toString().orEmpty()
            if (showErrors) validateAddress1()
        }
        binding.inputAddress2.doAfterTextChanged {
            vm.address2.value = it?.toString().orEmpty()
            if (showErrors) binding.inputAddress2Layout.error = null // optional field
        }
        binding.inputCity.doAfterTextChanged {
            vm.city.value = it?.toString().orEmpty()
            if (showErrors) validateCity()
        }
        binding.inputProvince.doAfterTextChanged {
            vm.province.value = it?.toString().orEmpty()
            if (showErrors) validateProvince()
        }
        // Make sure dropdown selection triggers validation everywhere
        binding.inputProvince.setOnItemClickListener { _,_,_,_ ->
            vm.province.value = binding.inputProvince.text?.toString().orEmpty()
            if (showErrors) validateProvince()
        }
        binding.inputPostalCode.doAfterTextChanged {
            vm.postalCode.value = it?.toString().orEmpty()
            if (showErrors) validatePostal()
        }
    }

    /** Host calls this on Next. */
    fun onNextPressed(): Boolean {
        showErrors = true
        val ok = validateEmail() && validatePhone() && validateAddress1() &&
                validateCity() && validateProvince() && validatePostal()
        if (!ok) focusFirstError()
        return ok
    }

    private fun validateEmail(): Boolean {
        val v = binding.inputEmail.text?.toString()?.trim().orEmpty()
        val ok = v.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(v).matches()
        binding.inputEmailLayout.error = if (showErrors && !ok) "Enter a valid email address." else null
        return ok
    }

    private fun validatePhone(): Boolean {
        val digits = binding.inputPhone.text?.toString()?.filter { it.isDigit() }.orEmpty()
        val ok = digits.length in 10..15
        binding.inputPhoneLayout.error = if (showErrors && !ok) "Enter a valid phone number (10–15 digits)." else null
        return ok
    }

    private fun validateAddress1(): Boolean {
        val ok = binding.inputAddress1.text?.toString()?.trim()?.isNotEmpty() == true
        binding.inputAddress1Layout.error = if (showErrors && !ok) "Address line 1 is required." else null
        return ok
    }

    private fun validateCity(): Boolean {
        val ok = binding.inputCity.text?.toString()?.trim()?.isNotEmpty() == true
        binding.inputCityLayout.error = if (showErrors && !ok) "City is required." else null
        return ok
    }

    private fun validateProvince(): Boolean {
        val ok = binding.inputProvince.text?.toString()?.trim()?.isNotEmpty() == true
        binding.inputProvinceLayout.error = if (showErrors && !ok) "Please select a province." else null
        return ok
    }

    private fun validatePostal(): Boolean {
        val v = binding.inputPostalCode.text?.toString()?.trim().orEmpty()
        val ok = v.length == 4 && v.all { it.isDigit() }
        binding.inputPostalCodeLayout.error = if (showErrors && !ok) "Postal code must be 4 digits." else null
        return ok
    }

    private fun focusFirstError() {
        when {
            binding.inputEmailLayout.error != null       -> binding.inputEmail.requestFocus()
            binding.inputPhoneLayout.error != null       -> binding.inputPhone.requestFocus()
            binding.inputAddress1Layout.error != null    -> binding.inputAddress1.requestFocus()
            binding.inputCityLayout.error != null        -> binding.inputCity.requestFocus()
            binding.inputProvinceLayout.error != null    -> binding.inputProvince.requestFocus()
            binding.inputPostalCodeLayout.error != null  -> binding.inputPostalCode.requestFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
