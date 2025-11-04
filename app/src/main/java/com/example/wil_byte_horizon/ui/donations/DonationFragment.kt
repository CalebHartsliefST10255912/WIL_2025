package com.example.wil_byte_horizon.ui.donation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wil_byte_horizon.databinding.DonationFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class DonationFragment : Fragment() {

    private var _binding: DonationFragmentBinding? = null
    private val binding get() = _binding!!

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DonationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnDonatePayfast.setOnClickListener {
            val amount = binding.editAmount.text.toString().trim()
            val message = binding.editMessage.text.toString().trim()

            if (amount.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a donation amount.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
                Toast.makeText(requireContext(), "Enter a valid donation amount.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proceed to PayFast redirect
            launchPayFast(amount, message)
        }
    }

    private fun launchPayFast(amount: String, message: String) {
        val user = auth.currentUser
        val email = user?.email ?: "guest@ndikhondinani.org"
        val name = user?.displayName ?: "Anonymous Donor"

        // 🔹 Replace with your actual PayFast credentials
        val merchantId = "10000100" // Example test ID
        val merchantKey = "46f0cd694581a"
        val returnUrl = "https://yourapp.com/success"
        val cancelUrl = "https://yourapp.com/cancel"
        val notifyUrl = "https://europe-west1-ndikhondinani-npo-db.cloudfunctions.net/payfastNotify"

        // 🔹 Encode parameters safely
        val uriBuilder = Uri.parse("https://www.payfast.co.za/eng/process").buildUpon()
            .appendQueryParameter("merchant_id", merchantId)
            .appendQueryParameter("merchant_key", merchantKey)
            .appendQueryParameter("return_url", returnUrl)
            .appendQueryParameter("cancel_url", cancelUrl)
            .appendQueryParameter("notify_url", notifyUrl)
            .appendQueryParameter("name_first", URLEncoder.encode(name, StandardCharsets.UTF_8.toString()))
            .appendQueryParameter("email_address", email)
            .appendQueryParameter("amount", amount)
            .appendQueryParameter("item_name", "Ndikhondinani Donation")
            .appendQueryParameter("item_description", URLEncoder.encode(message.ifBlank { "Mobile Donation" }, StandardCharsets.UTF_8.toString()))

        val payfastUri = uriBuilder.build()

        try {
            val intent = Intent(Intent.ACTION_VIEW, payfastUri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Unable to open PayFast checkout.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
