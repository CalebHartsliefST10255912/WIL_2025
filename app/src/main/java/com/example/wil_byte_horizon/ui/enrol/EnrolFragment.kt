package com.example.wil_byte_horizon.ui.enrol

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.auth.LoginActivity
import com.example.wil_byte_horizon.core.AdminClaimsManager
import com.example.wil_byte_horizon.data.admin.AdminRepository
import com.example.wil_byte_horizon.data.qualifications.Qualification
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class EnrolFragment : Fragment() {

    private val vm: EnrolViewModel by viewModels()

    private lateinit var recycler: RecyclerView
    private lateinit var search: EditText
    private var adapter: EnrolAdapter? = null

    private val adminRepo = AdminRepository()
    private var pendingQual: Qualification? = null

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val qual = pendingQual
        if (user != null && qual != null) {
            launchForm(qual)
        } else {
            Toast.makeText(requireContext(), "Login cancelled", Toast.LENGTH_SHORT).show()
        }
        pendingQual = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_enrol, container, false)

        search = root.findViewById(R.id.searchBar)
        recycler = root.findViewById(R.id.rvQualifications)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Search → update filter
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                vm.updateSearch(s?.toString().orEmpty())
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // 1) Build adapter when admin flag arrives (only when it changes)
                launch {
                    AdminClaimsManager.isAdminFlow()
                        .distinctUntilChanged()
                        .collectLatest { isAdmin ->
                            adapter = EnrolAdapter(
                                isAdmin = isAdmin,
                                onEnrolClick = { qual ->
                                    val user = FirebaseAuth.getInstance().currentUser
                                    if (user == null) {
                                        pendingQual = qual
                                        loginLauncher.launch(
                                            Intent(requireContext(), LoginActivity::class.java)
                                        )
                                    } else {
                                        launchForm(qual)
                                    }
                                },
                                onEdit = { qual -> showEditQualificationDialog(qual) },
                                onDelete = { qual -> confirmDeleteQualification(qual) }
                            ).also { recycler.adapter = it }

                            // Immediately paint current snapshot so list isn't blank when we return
                            adapter?.submitList(vm.filtered.value)
                        }
                }

                // 2) Collect filtered list continuously
                launch {
                    vm.filtered.collectLatest { list ->
                        adapter?.submitList(list)
                    }
                }
            }
        }
    }

    private fun showEditQualificationDialog(q: Qualification) {
        EditQualificationDialog(q) { /* snapshot listener updates list */ }
            .show(parentFragmentManager, "edit_qualification")
    }

    private fun confirmDeleteQualification(q: Qualification) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete this qualification?")
            .setMessage(q.title)
            .setPositiveButton("Delete") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    runCatching { adminRepo.deleteQualification(q.id) }
                        .onFailure {
                            Toast.makeText(
                                requireContext(),
                                it.message ?: "Delete failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun launchForm(qual: Qualification) {
        val i = Intent(requireContext(), EnrolFormActivity::class.java)
        i.putExtra("QUAL_ID", qual.id)
        i.putExtra("QUAL_TITLE", qual.title)
        startActivity(i)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }
}
