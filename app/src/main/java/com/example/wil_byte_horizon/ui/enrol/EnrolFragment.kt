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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.auth.LoginActivity
import kotlinx.coroutines.flow.collectLatest

class EnrolFragment : Fragment() {

    private val vm: EnrolViewModel by viewModels()
    private lateinit var recycler: RecyclerView
    private lateinit var search: EditText
    private lateinit var adapter: EnrolAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val root = inflater.inflate(R.layout.fragment_enrol, container, false)

        search  = root.findViewById(R.id.searchBar)
        recycler = root.findViewById(R.id.rvQualifications)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = EnrolAdapter { qual ->
            if (vm.canEnrol()) {
                // 🚀 Proceed to enrol flow (navigate to your enrol details / form)
                Toast.makeText(requireContext(), "Proceed to enrol: ${qual.title}", Toast.LENGTH_SHORT).show()
                // e.g., findNavController().navigate(R.id.action_enrol_to_enrolDetails, bundleOf("qualId" to qual.id))
            } else {
                // 🔒 Not signed in: prompt auth JUST IN TIME
                val i = Intent(requireContext(), LoginActivity::class.java)
                startActivity(i)
            }
        }
        recycler.adapter = adapter

        // Search
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                vm.updateSearch(s?.toString().orEmpty())
            }
        })

        // Observe data
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vm.filtered.collectLatest { list -> adapter.submitList(list) }
        }

        return root
    }
}
