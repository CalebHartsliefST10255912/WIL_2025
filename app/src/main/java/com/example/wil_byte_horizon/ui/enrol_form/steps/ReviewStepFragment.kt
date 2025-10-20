package com.example.wil_byte_horizon.ui.enrol_form.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wil_byte_horizon.databinding.FragmentStepReviewBinding
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormViewModel

class ReviewStepFragment : Fragment() {
    private var _binding: FragmentStepReviewBinding? = null
    private val binding get() = _binding!!
    private val vm: EnrolFormViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStepReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvQualTitle.text = vm.qualificationTitle.value

        binding.tvPersonal.text = buildString {
            appendLine("Full name: ${vm.fullName.value.orEmpty()}")
            appendLine("ID number: ${vm.idNumber.value.orEmpty()}")
            appendLine("Date of birth: ${vm.dateOfBirth.value.orEmpty()}")
        }

        binding.tvContact.text = buildString {
            appendLine("Email: ${vm.email.value.orEmpty()}")
            appendLine("Phone: ${vm.phone.value.orEmpty()}")
            appendLine("Address: ${vm.address1.value.orEmpty()} ${vm.address2.value.orEmpty()}")
            appendLine("City/Province: ${vm.city.value.orEmpty()} / ${vm.province.value.orEmpty()}")
            appendLine("Postal code: ${vm.postalCode.value.orEmpty()}")
        }

        binding.tvBackground.text = buildString {
            appendLine("Highest education: ${vm.highestEducation.value.orEmpty()}")
            appendLine("Employment status: ${vm.employmentStatus.value.orEmpty()}")
            appendLine("Motivation: ${vm.motivation.value.orEmpty()}")
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
