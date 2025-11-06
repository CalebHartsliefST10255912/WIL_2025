package com.example.wil_byte_horizon.ui.enrol_form

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.databinding.ActivityEnrolFormBinding
import com.example.wil_byte_horizon.ui.enrol_form.steps.BackgroundStepFragment
import com.example.wil_byte_horizon.ui.enrol_form.steps.ContactStepFragment
import com.example.wil_byte_horizon.ui.enrol_form.steps.PersonalStepFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

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

        // Show title immediately; if you ever change it later, collect the flow
        binding.tvQualTitle.text = vm.qualificationTitle.value

        // Pager
        val pager = binding.pager
        pager.adapter = EnrolFormPagerAdapter(this)
        pager.isUserInputEnabled = false
        updateButtons(pager.currentItem)

        // Back
        binding.btnBack.setOnClickListener {
            if (pager.currentItem == 0) finish()
            else {
                pager.currentItem -= 1
                updateButtons(pager.currentItem)
            }
        }

        // Next
        binding.btnNext.setOnClickListener {
            val step = pager.currentItem

            // Show field-level errors ON DEMAND (only when Next is pressed)
            val currentFrag = findStepFragment(step)
            val canProceedByUi = when (currentFrag) {
                is PersonalStepFragment   -> currentFrag.onNextPressed()
                is ContactStepFragment    -> currentFrag.onNextPressed()
                is BackgroundStepFragment -> currentFrag.onNextPressed()
                // Review step has no per-field editors; allow continue to submit.
                else -> true
            }

            if (!canProceedByUi) {
                Snackbar.make(binding.root, R.string.form_fix_errors, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Secondary guard
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

        // Optional: reflect submitting state in UI (prevents edge cases on rotation)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vm.submitting.collect { isSubmitting ->
                        binding.progressCircular.visibility = if (isSubmitting) View.VISIBLE else View.GONE
                        binding.btnNext.isEnabled = !isSubmitting
                        binding.btnBack.isEnabled = !isSubmitting
                    }
                }
            }
        }
    }

    /** Find the fragment currently shown by ViewPager2. */
    private fun findStepFragment(position: Int): Fragment? {
        // Default tag used by FragmentStateAdapter for ViewPager2
        val tag = "f$position"
        return supportFragmentManager.findFragmentByTag(tag)
    }

    private fun updateButtons(step: Int) {
        binding.btnBack.text =
            if (step == 0) getString(R.string.cancel) else getString(R.string.back)
        binding.btnNext.text =
            if (step == EnrolFormPagerAdapter.TOTAL_STEPS - 1) getString(R.string.submit)
            else getString(R.string.next)

        val pct = ((step + 1).toFloat() / EnrolFormPagerAdapter.TOTAL_STEPS * 100).toInt()

        binding.progressHorizontal.isIndeterminate = false
        binding.progressHorizontal.max = 100
        binding.progressHorizontal.progress = pct
    }

    private fun submit() {
        // The ViewModel guards double submits; UI will also be disabled via the 'submitting' collector
        vm.submit { ok, msg ->
            if (ok) {
                // ✅ Firestore write completed successfully.
                // The Cloud Function already enqueued the success email.
                Toast.makeText(
                    this,
                    "Registration successful! You should receive an email shortly.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            } else {
                // The Cloud Function attempts to enqueue a failure email (best effort).
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
