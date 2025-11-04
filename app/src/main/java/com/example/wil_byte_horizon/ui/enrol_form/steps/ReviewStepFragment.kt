package com.example.wil_byte_horizon.ui.enrol_form.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.wil_byte_horizon.databinding.FragmentStepReviewBinding
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ReviewStepFragment : Fragment() {

    private var _binding: FragmentStepReviewBinding? = null
    private val binding get() = _binding!!
    private val vm: EnrolFormViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Observe everything so the review stays in sync if user goes back and edits
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    vm.qualificationTitle,
                    vm.fullName,
                    vm.idNumber,
                    vm.dateOfBirth,
                    vm.email,
                    vm.phone,
                    vm.address1,
                    vm.address2,
                    vm.city,
                    vm.province,
                    vm.postalCode,
                    vm.highestEducation,
                    vm.employmentStatus,
                    vm.motivation
                ) { values ->
                    val title          = values[0]  as String
                    val fullName       = values[1]  as String
                    val idNumber       = values[2]  as String
                    val dob            = values[3]  as String
                    val email          = values[4]  as String
                    val phone          = values[5]  as String
                    val address1       = values[6]  as String
                    val address2       = values[7]  as String
                    val city           = values[8]  as String
                    val province       = values[9]  as String
                    val postalCode     = values[10] as String
                    val highestEdu     = values[11] as String
                    val employment     = values[12] as String
                    val motivation     = values[13] as String

                    binding.tvQualTitle.text = title

                    binding.tvPersonal.text = buildString {
                        appendLine("Full name: $fullName")
                        appendLine("ID number: $idNumber")
                        appendLine("Date of birth: $dob")
                    }

                    binding.tvContact.text = buildString {
                        appendLine("Email: $email")
                        appendLine("Phone: $phone")
                        appendLine("Address: $address1 $address2")
                        appendLine("City/Province: $city / $province")
                        appendLine("Postal code: $postalCode")
                    }

                    binding.tvBackground.text = buildString {
                        appendLine("Highest education: $highestEdu")
                        appendLine("Employment status: $employment")
                        appendLine("Motivation: $motivation")
                    }
                }.collect { /* values applied above */ }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
