package com.example.wil_byte_horizon.ui.enrol

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.data.admin.AdminRepository
import com.example.wil_byte_horizon.data.qualifications.Qualification
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class EditQualificationDialog(
    private val q: Qualification,
    private val onUpdated: () -> Unit
) : DialogFragment() {

    private val adminRepo = AdminRepository()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_qualification, null, false)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDesc  = view.findViewById<EditText>(R.id.etDescription)
        val etCat   = view.findViewById<EditText>(R.id.etCategory)
        val cbOpen  = view.findViewById<CheckBox>(R.id.cbOpen)

        etTitle.setText(q.title)
        etDesc.setText(q.description)
        etCat.setText(q.category)
        cbOpen.isChecked = q.isOpen

        val dlg = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .create()

        view.findViewById<android.widget.Button>(R.id.btnCancel).setOnClickListener { dlg.dismiss() }
        view.findViewById<android.widget.Button>(R.id.btnSave).setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc  = etDesc.text.toString().trim()
            val cat   = etCat.text.toString().trim()
            val open  = cbOpen.isChecked

            lifecycleScope.launch {
                runCatching {
                    adminRepo.updateQualificationFull(
                        id = q.id,
                        title = title,
                        description = desc,
                        category = cat,
                        isOpen = open
                    )
                }.onSuccess {
                    Toast.makeText(requireContext(), "Qualification updated", Toast.LENGTH_SHORT).show()
                    onUpdated()
                    dlg.dismiss()
                }.onFailure {
                    Toast.makeText(requireContext(), it.message ?: "Update failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        return dlg
    }
}
