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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.auth.LoginActivity
import com.example.wil_byte_horizon.data.qualifications.Qualification
import com.example.wil_byte_horizon.ui.enrol_form.EnrolFormActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest

class EnrolFragment : Fragment() {

    private val vm: EnrolViewModel by viewModels()

    private lateinit var recycler: RecyclerView
    private lateinit var search: EditText
    private lateinit var adapter: EnrolAdapter

    // we store which qual the user intended to enrol for before login
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
        recycler = root.findViewById(R.id.rvQualifications) // make sure your XML has this RecyclerView
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = EnrolAdapter { qual ->
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                // not signed in → prompt auth, then continue
                pendingQual = qual
                loginLauncher.launch(Intent(requireContext(), LoginActivity::class.java))
            } else {
                // already signed in → go straight to the form
                launchForm(qual)
            }
        }
        recycler.adapter = adapter

        // search box -> update filter
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                vm.updateSearch(s?.toString().orEmpty())
            }
        })

        // observe list
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.filtered.collectLatest { list ->
                adapter.submitList(list)
            }
        }

        return root
    }

    private fun launchForm(qual: Qualification) {
        val i = Intent(requireContext(), EnrolFormActivity::class.java)
        i.putExtra("QUAL_ID", qual.id)
        i.putExtra("QUAL_TITLE", qual.title)
        startActivity(i)
    }
}
