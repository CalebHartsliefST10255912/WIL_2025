package com.example.wil_byte_horizon.ui.soup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.databinding.FragmentSoupBinding
import com.example.wil_byte_horizon.ui.home.BannerAdapter
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class SoupFragment : Fragment() {

    private var _binding: FragmentSoupBinding? = null
    private val binding get() = _binding!!

    private lateinit var bannerAdapter: BannerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSoupBinding.inflate(inflater, container, false)

        // Set up the banner ViewPager
        val bannerImages = listOf(
            R.drawable.npo_banner,
            R.drawable.banner_bh
        )
        bannerAdapter = BannerAdapter(bannerImages)
        binding.bannerViewPager.adapter = bannerAdapter

        // Set up the dots indicator
        binding.dotsIndicator.setViewPager2(binding.bannerViewPager)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
