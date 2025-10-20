package com.example.wil_byte_horizon.ui.enrol_form

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.wil_byte_horizon.ui.enrol_form.steps.BackgroundStepFragment
import com.example.wil_byte_horizon.ui.enrol_form.steps.ContactStepFragment
import com.example.wil_byte_horizon.ui.enrol_form.steps.PersonalStepFragment
import com.example.wil_byte_horizon.ui.enrol_form.steps.ReviewStepFragment

class EnrolFormPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    companion object { const val TOTAL_STEPS = 4 }

    override fun getItemCount(): Int = TOTAL_STEPS

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> PersonalStepFragment()
        1 -> ContactStepFragment()
        2 -> BackgroundStepFragment()
        else -> ReviewStepFragment()
    }
}
