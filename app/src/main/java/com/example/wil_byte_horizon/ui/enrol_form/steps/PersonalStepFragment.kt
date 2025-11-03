package com.example.wil_byte_horizon.ui.enrol_form.steps

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wil_byte_horizon.databinding.FragmentStepPersonalBinding
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormViewModel
import java.util.Calendar
import java.util.Locale

class PersonalStepFragment : Fragment() {

    private var _binding: FragmentStepPersonalBinding? = null
    private val binding get() = _binding!!
    private val vm: EnrolFormViewModel by activityViewModels()
    private var showErrors = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStepPersonalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Prefill
        binding.inputFullName.setText(vm.fullName.value)
        binding.inputIdNumber.setText(vm.idNumber.value)
        binding.inputDob.setText(vm.dateOfBirth.value)

        // DOB picker
        binding.inputDob.apply {
            isFocusable = false; isClickable = true; keyListener = null
            setOnClickListener { showDobPicker() }
        }
        binding.inputDobLayout.setEndIconOnClickListener { showDobPicker() }

        // Live sync (only validate after Next was pressed once)
        binding.inputFullName.doAfterTextChanged {
            vm.fullName.value = it?.toString().orEmpty()
            if (showErrors) validateFullName()
        }
        binding.inputIdNumber.doAfterTextChanged {
            vm.idNumber.value = it?.toString().orEmpty()
            if (showErrors) validateIdNumber()
        }
        binding.inputDob.doAfterTextChanged {
            vm.dateOfBirth.value = it?.toString().orEmpty()
            if (showErrors) validateDob()
        }
    }

    private fun showDobPicker() {
        val cal = Calendar.getInstance()
        binding.inputDob.text?.toString()?.split("-")?.let { p ->
            if (p.size == 3) {
                val y = p[0].toIntOrNull(); val m = p[1].toIntOrNull()?.minus(1); val d = p[2].toIntOrNull()
                if (y != null && m != null && d != null) cal.set(y, m, d)
            }
        }

        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                val mm = String.format(Locale.US, "%02d", m + 1)
                val dd = String.format(Locale.US, "%02d", d)
                binding.inputDob.setText("$y-$mm-$dd")
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    /** Host calls this on Next. Returns true if valid. */
    fun onNextPressed(): Boolean {
        showErrors = true
        val ok = validateFullName() && validateIdNumber() && validateDob()
        if (!ok) focusFirstError()
        return ok
    }

    private fun validateFullName(): Boolean {
        val ok = binding.inputFullName.text?.toString()?.trim()?.length ?: 0 >= 2
        binding.inputFullNameLayout.error = if (showErrors && !ok) "Please enter your full name." else null
        return ok
    }

    private fun validateIdNumber(): Boolean {
        val v = binding.inputIdNumber.text?.toString()?.trim().orEmpty()
        val ok = v.length == 13 && v.all { it.isDigit() }
        binding.inputIdNumberLayout.error = if (showErrors && !ok) "ID number must be 13 digits." else null
        return ok
    }

    private fun validateDob(): Boolean {
        val v = binding.inputDob.text?.toString()?.trim().orEmpty()
        val m = Regex("""^(\d{4})-(\d{2})-(\d{2})$""").matchEntire(v)
        if (v.isBlank() || m == null) {
            binding.inputDobLayout.error = if (showErrors) "Please select your date of birth." else null
            return false
        }
        val (yStr, moStr, dStr) = m.destructured
        return try {
            val dob = Calendar.getInstance().apply {
                set(Calendar.YEAR, yStr.toInt())
                set(Calendar.MONTH, moStr.toInt() - 1)
                set(Calendar.DAY_OF_MONTH, dStr.toInt())
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            val ok = !dob.after(today)
            binding.inputDobLayout.error = if (showErrors && !ok) "Date of birth cannot be in the future." else null
            ok
        } catch (_: Exception) {
            binding.inputDobLayout.error = if (showErrors) "Please select a valid date of birth." else null
            false
        }
    }

    private fun focusFirstError() {
        when {
            binding.inputFullNameLayout.error != null -> binding.inputFullName.requestFocus()
            binding.inputIdNumberLayout.error != null -> binding.inputIdNumber.requestFocus()
            binding.inputDobLayout.error != null      -> binding.inputDob.requestFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
