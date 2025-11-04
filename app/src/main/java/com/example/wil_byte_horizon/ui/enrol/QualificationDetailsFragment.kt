package com.example.wil_byte_horizon.ui.enrol

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.data.qualifications.Qualification
import com.example.wil_byte_horizon.data.qualifications.QualificationsRepository
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class QualificationDetailsFragment : Fragment() {

    private val repo = QualificationsRepository()

    private lateinit var tvTitle: TextView
    private lateinit var tvMeta: TextView
    private lateinit var tvDesc: TextView
    private lateinit var btnEnrol: Button

    private var qual: Qualification? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_qualification_details, container, false)
        tvTitle = v.findViewById(R.id.tvTitle)
        tvMeta  = v.findViewById(R.id.tvMeta)
        tvDesc  = v.findViewById(R.id.tvDesc)
        btnEnrol = v.findViewById(R.id.btnEnrol)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val qualificationId = arguments?.getString("qualificationId")
        if (qualificationId.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Missing qualificationId", Toast.LENGTH_SHORT).show()
            btnEnrol.isEnabled = false
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val q = repo.getById(qualificationId)
            qual = q
            if (q == null) {
                tvTitle.text = "Qualification not found"
                tvDesc.text = ""
                btnEnrol.isEnabled = false
                return@launch
            }

            // Bind UI
            tvTitle.text = q.title
            tvDesc.text  = q.description

            val status = if (q.isOpen) "Open" else "Closed"
            val updated = q.updatedAt?.toDate()
            val updatedStr = updated?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
            } ?: "—"

            // e.g. "Category: ICT • Status: Open • Updated: 12 Sep 2025"
            tvMeta.text = "Category: ${q.category} • Status: $status • Updated: $updatedStr"

            btnEnrol.isEnabled = q.isOpen
            btnEnrol.setOnClickListener { launchForm(q) }
        }
    }

    private fun launchForm(q: Qualification) {
        val i = Intent(requireContext(), EnrolFormActivity::class.java).apply {
            putExtra("QUAL_ID", q.id)
            putExtra("QUAL_TITLE", q.title)
        }
        startActivity(i)
    }
}
