package com.example.wil_byte_horizon.ui.contact

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.auth.LoginActivity
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.databinding.FragmentContactBinding
import com.google.android.material.snackbar.Snackbar

class ContactFragment : Fragment() {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    // Centralize org details (override in strings.xml)
    private val orgEmail by lazy { getString(R.string.org_email) }
    private val orgPhone by lazy { getString(R.string.org_phone) }
    private val orgWebsite by lazy { getString(R.string.org_website) }
    private val orgAddressQuery by lazy { getString(R.string.org_address_query) } // for Maps search
    private val donateUrl by lazy { getString(R.string.org_donate_url) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)

        // Prefill contact tiles
        binding.txtEmailValue.text = orgEmail
        binding.txtPhoneValue.text = orgPhone
        binding.txtWebsiteValue.text = orgWebsite

        // Real-time validation
        binding.inputEmail.editText?.doOnTextChanged { text, _, _, _ ->
            if (!text.isNullOrBlank() && !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                binding.inputEmail.error = getString(R.string.contact_err_email)
            } else binding.inputEmail.error = null
        }
        binding.inputName.editText?.doOnTextChanged { text, _, _, _ ->
            binding.inputName.error = if (text.isNullOrBlank()) getString(R.string.contact_err_name) else null
        }
        binding.inputMessage.editText?.doOnTextChanged { text, _, _, _ ->
            binding.inputMessage.error = if (text.isNullOrBlank()) getString(R.string.contact_err_message) else null
        }

        // Primary send action -> Email intent (safe for no email apps)
        binding.btnSend.setOnClickListener { sendEmail() }

        // Quick actions
        binding.rowEmail.setOnClickListener { sendEmail() }
        binding.rowPhone.setOnClickListener { dialPhone() }
        binding.rowWebsite.setOnClickListener { openUrl(orgWebsite) }
        binding.rowAddress.setOnClickListener { openMaps(orgAddressQuery) }
        binding.btnDonate.setOnClickListener { openUrl(donateUrl) }

        // Logout (kept, but subtle)
        binding.btnLogout.setOnClickListener {
            FirebaseAuthManager.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }

        return binding.root
    }

    private fun sendEmail() {
        val name = binding.inputName.editText?.text?.toString()?.trim().orEmpty()
        val email = binding.inputEmail.editText?.text?.toString()?.trim().orEmpty()
        val message = binding.inputMessage.editText?.text?.toString()?.trim().orEmpty()

        var valid = true
        if (name.isBlank()) {
            binding.inputName.error = getString(R.string.contact_err_name); valid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.error = getString(R.string.contact_err_email); valid = false
        }
        if (message.isBlank()) {
            binding.inputMessage.error = getString(R.string.contact_err_message); valid = false
        }
        if (!valid) return

        val subject = getString(R.string.contact_email_subject, name)
        val body = buildString {
            appendLine(getString(R.string.contact_email_body_header))
            appendLine()
            appendLine(message)
            appendLine()
            appendLine("---")
            appendLine(getString(R.string.contact_email_from, name, email))
        }

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$orgEmail")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.contact_chooser_email)))
        } catch (_: ActivityNotFoundException) {
            view?.let { v -> Snackbar.make(v, R.string.contact_no_email_apps, Snackbar.LENGTH_LONG).show() }
        }
    }

    private fun dialPhone() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$orgPhone"))
        runCatching { startActivity(intent) }
            .onFailure { view?.let { v -> Snackbar.make(v, R.string.contact_cannot_dial, Snackbar.LENGTH_LONG).show() } }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        runCatching { startActivity(intent) }
            .onFailure { view?.let { v -> Snackbar.make(v, R.string.contact_cannot_open_link, Snackbar.LENGTH_LONG).show() } }
    }

    private fun openMaps(query: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        try {
            startActivity(mapIntent)
        } catch (_: ActivityNotFoundException) {
            // fallback to any maps-capable app / browser
            openUrl("https://www.google.com/maps/search/?api=1&query=${Uri.encode(query)}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
