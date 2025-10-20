package com.example.wil_byte_horizon.ui.enrol_form

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.databinding.ActivityEnrolFormBinding
import com.google.android.material.snackbar.Snackbar

class EnrolFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEnrolFormBinding
    private val vm: EnrolFormViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuthManager.currentUser() == null) {
            finish(); return
        }

        binding = ActivityEnrolFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.enrol_form_title)

        // Read qual extras
        vm.setQualification(
            intent.getStringExtra("QUAL_ID") ?: "",
            intent.getStringExtra("QUAL_TITLE") ?: ""
        )
        binding.tvQualTitle.text = vm.qualificationTitle.value

        // Pager
        val pager = binding.pager
        pager.adapter = EnrolFormPagerAdapter(this)
        pager.isUserInputEnabled = false
        updateButtons(pager.currentItem)

        // Nav buttons
        binding.btnBack.setOnClickListener {
            if (pager.currentItem == 0) finish()
            else {
                pager.currentItem -= 1
                updateButtons(pager.currentItem)
            }
        }
        binding.btnNext.setOnClickListener {
            val step = pager.currentItem
            if (!vm.validateStep(step)) {
                Snackbar.make(binding.root, R.string.form_fix_errors, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (step < EnrolFormPagerAdapter.TOTAL_STEPS - 1) {
                pager.currentItem += 1
                updateButtons(pager.currentItem)
            } else {
                submit()
            }
        }
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) = updateButtons(position)
        })
    }

    private fun updateButtons(step: Int) {
        binding.btnBack.text =
            if (step == 0) getString(R.string.cancel) else getString(R.string.back)
        binding.btnNext.text =
            if (step == EnrolFormPagerAdapter.TOTAL_STEPS - 1) getString(R.string.submit)
            else getString(R.string.next)

        val pct = ((step + 1).toFloat() / EnrolFormPagerAdapter.TOTAL_STEPS * 100).toInt()

        // Your XML uses a standard ProgressBar with id progressHorizontal
        binding.progressHorizontal.isIndeterminate = false
        binding.progressHorizontal.max = 100
        binding.progressHorizontal.progress = pct
    }

    private fun submit() {
        binding.progressCircular.visibility = View.VISIBLE
        binding.btnNext.isEnabled = false
        binding.btnBack.isEnabled = false

        vm.submit { ok, msg ->
            binding.progressCircular.visibility = View.GONE
            binding.btnNext.isEnabled = true
            binding.btnBack.isEnabled = true

            if (ok) {
                Snackbar.make(binding.root, R.string.form_submitted, Snackbar.LENGTH_LONG).show()
                finish()
            } else {
                Snackbar.make(
                    binding.root,
                    msg ?: getString(R.string.form_submit_failed),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
