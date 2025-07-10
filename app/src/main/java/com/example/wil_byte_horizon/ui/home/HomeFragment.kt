package com.example.wil_byte_horizon.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.databinding.FragmentHomeBinding
import com.example.wil_byte_horizon.ui.home.BannerAdapter
import com.example.wil_byte_horizon.ui.home.HomeViewModel
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var leftArrow: ImageButton
    private lateinit var rightArrow: ImageButton
    private lateinit var dotsIndicator: WormDotsIndicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Setup ViewPager2 with banner adapter
        viewPager = binding.bannerViewPager
        val bannerImages = listOf(
            R.drawable.npo_banner,
            R.drawable.banner_bh
        )
        bannerAdapter = BannerAdapter(bannerImages)
        viewPager.adapter = bannerAdapter

        // Setup dots
        dotsIndicator = binding.dotsIndicator
        dotsIndicator.setViewPager2(viewPager)

        // Arrows
        leftArrow = binding.leftArrow
        rightArrow = binding.rightArrow

        leftArrow.setOnClickListener {
            val current = viewPager.currentItem
            if (current > 0) viewPager.currentItem = current - 1
        }

        rightArrow.setOnClickListener {
            val current = viewPager.currentItem
            if (current < bannerAdapter.itemCount - 1) viewPager.currentItem = current + 1
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
