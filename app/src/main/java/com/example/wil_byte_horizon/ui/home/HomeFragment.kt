package com.example.wil_byte_horizon.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wil_byte_horizon.databinding.FragmentHomeBinding
import com.example.wil_byte_horizon.utils.NetworkUtils
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isOfflineMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup ViewPager2 with banner adapter
        val viewPager = binding.bannerViewPager
        val bannerImages = listOf<Int>(
            com.example.wil_byte_horizon.R.drawable.org_banner,
            com.example.wil_byte_horizon.R.drawable.banner_bh
        )
        val bannerAdapter = BannerAdapter(bannerImages)
        viewPager.adapter = bannerAdapter

        // Setup dots
        val dotsIndicator = binding.dotsIndicator
        dotsIndicator.setViewPager2(viewPager)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNetworkAndSetupUI()
    }

    private fun checkNetworkAndSetupUI() {
        isOfflineMode = !NetworkUtils.isOnline(requireContext())

        if (isOfflineMode) {
            Toast.makeText(context, "Offline Mode: Limited functionality", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        checkNetworkAndSetupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}