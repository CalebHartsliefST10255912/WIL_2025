package com.example.wil_byte_horizon.ui.event

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.data.admin.AdminRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EditEventDialog(
    private val event: UiEvent,
    private val onUpdated: () -> Unit
) : DialogFragment() {

    private val adminRepo = AdminRepository()
    private var pickedPoster: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> pickedPoster = uri }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_event, null, false)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDesc  = view.findViewById<EditText>(R.id.etDescription)
        val etStart = view.findViewById<EditText>(R.id.etStart)
        val etEnd   = view.findViewById<EditText>(R.id.etEnd)
        val etLoc   = view.findViewById<EditText>(R.id.etLocationName)
        val etLat   = view.findViewById<EditText>(R.id.etLat)
        val etLng   = view.findViewById<EditText>(R.id.etLng)
        val etImageUrl = view.findViewById<EditText>(R.id.etImageUrl)

        // Prefill
        etTitle.setText(event.title)
        etDesc.setText(event.description)
        etLoc.setText(event.locationName)
        // NOTE: UiEvent doesn't carry millis; keep blank or prefill via extra fields if you add them.
        etLat.setText(event.lat?.toString().orEmpty())
        etLng.setText(event.lng?.toString().orEmpty())
        etImageUrl.setText(event.imageUrl.takeIf { it.startsWith("http") || it.startsWith("gs://") }.orEmpty())

        view.findViewById<android.widget.Button>(R.id.btnPickPoster)
            .setOnClickListener { pickImage.launch("image/*") }

        val dlg = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .create()

        view.findViewById<android.widget.Button>(R.id.btnCancel).setOnClickListener { dlg.dismiss() }
        view.findViewById<android.widget.Button>(R.id.btnSave).setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc  = etDesc.text.toString().trim()
            val loc   = etLoc.text.toString().trim()
            val lat   = etLat.text.toString().toDoubleOrNull()
            val lng   = etLng.text.toString().toDoubleOrNull()
            val pastedUrl = etImageUrl.text.toString().trim().ifBlank { null }

            // Parse optional times — expect "yyyy-MM-dd HH:mm"
            val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            val startMillis = etStart.text.toString().trim().takeIf { it.isNotEmpty() }?.let {
                runCatching { fmt.parse(it)?.time }.getOrNull()
            }
            val endMillis = etEnd.text.toString().trim().takeIf { it.isNotEmpty() }?.let {
                runCatching { fmt.parse(it)?.time }.getOrNull()
            }

            lifecycleScope.launch {
                runCatching {
                    adminRepo.updateEventFull(
                        eventId = event.id,
                        title = title,
                        description = desc,
                        startMillis = startMillis,
                        endMillis = endMillis,
                        locationName = loc,
                        lat = lat,
                        lng = lng,
                        newPosterUri = pickedPoster,
                        newImageUrl = pastedUrl
                    )
                }.onSuccess {
                    Toast.makeText(requireContext(), "Event updated", Toast.LENGTH_SHORT).show()
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
