package com.example.wil_byte_horizon.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.databinding.FragmentHomeBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val vm: HomeViewModel by viewModels()

    private lateinit var programsAdapter: ProgramAdapter
    private lateinit var newsAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.scrollHome.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(top = insets.systemWindowInsetTop)
            insets
        }

        setupLists()
        setupClicks()
        bindObservers()
        //animateStats()

        return binding.root
    }

    private fun setupLists() {
        programsAdapter = ProgramAdapter { program ->
            // TODO: Navigate to program details if you have it
            // findNavController().navigate(HomeFragmentDirections.actionHomeToProgram(program.id))
        }
        binding.rvPrograms.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = programsAdapter
        }

        newsAdapter = ArticleAdapter { article ->
            // TODO: Navigate to article details
            // findNavController().navigate(HomeFragmentDirections.actionHomeToArticle(article.id))
        }
        binding.rvNews.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = newsAdapter
        }
    }

    private fun setupClicks() {
//        binding.btnDonate.setOnClickListener {
//            // findNavController().navigate(R.id.action_home_to_donate)
//        }
//        binding.btnVolunteer.setOnClickListener {
//            // findNavController().navigate(R.id.action_home_to_volunteer)
//        }
        binding.btnAllNews.setOnClickListener {
            // findNavController().navigate(R.id.action_home_to_news)
        }
    }

    private fun bindObservers() {
        vm.programs.observe(viewLifecycleOwner) { programs ->
            programsAdapter.submitList(programs)
        }
        vm.articles.observe(viewLifecycleOwner) { articles ->
            newsAdapter.submitList(articles)
        }
        vm.stats.observe(viewLifecycleOwner) { s ->
//            binding.cardBeneficiaries.setLabel(getString(R.string.stat_beneficiaries))
//            binding.cardVolunteers.setLabel(getString(R.string.stat_volunteers))
//            binding.cardProjects.setLabel(getString(R.string.stat_projects))
//
//            // set targets + animate
//            binding.cardBeneficiaries.animateTo(s.beneficiaries)
//            binding.cardVolunteers.animateTo(s.volunteers)
//            binding.cardProjects.animateTo(s.projects)
        }
    }

//    private fun animateStats() {
//        lifecycleScope.launch {
//            delay(150)
//            listOf(binding.cardBeneficiaries, binding.cardVolunteers, binding.cardProjects).forEach { statView ->
//                val valueView = statView.findViewById<android.widget.TextView>(R.id.txtValue)
//                val target = (statView.tag as? Int) ?: 0
//                val duration = 700L
//                val steps = 40
//                for (i in 0..steps) {
//                    val t = i / steps.toFloat()
//                    val eased = DecelerateInterpolator().getInterpolation(t)
//                    val current = (target * eased).toInt()
//                    valueView.text = current.toString()
//                    delay(duration / steps)
//                }
//                valueView.text = target.toString()
//            }
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
