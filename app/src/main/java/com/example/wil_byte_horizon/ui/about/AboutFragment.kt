package com.example.wil_byte_horizon.ui.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wil_byte_horizon.BuildConfig
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.databinding.FragmentAboutBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    // Customize to your project
    private val websiteUrl = "https://example.com"
    private val privacyUrl = "https://example.com/privacy"
    private val shareText =
        "Check out this app: https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)

        // Version under app name
        binding.txtVersion.text = getString(R.string.about_version, BuildConfig.VERSION_NAME)

        // Click actions
        binding.rowPrivacy.setOnClickListener { openUrl(privacyUrl) }
        binding.rowContact.setOnClickListener {
            // Ensure your nav_graph has a destination with id contactFragment
            findNavController().navigate(R.id.contactFragment)
        }
        binding.rowShare.setOnClickListener { shareApp() }
        binding.btnRate.setOnClickListener { openPlayStore() }

        // Footer year
//        val year = Calendar.getInstance().get(Calendar.YEAR)
//        binding.txtCopyright.text = getString(R.string.about_copyright_dynamic, year)

        return binding.root
    }

    private fun openUrl(url: String) {
        runCatching {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }.onFailure {
            view?.let { v ->
                Snackbar.make(v, R.string.about_error_open_link, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun shareApp() {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(sendIntent, getString(R.string.about_share_app)))
    }

    private fun openPlayStore() {
        val pkg = requireContext().packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")))
        } catch (_: ActivityNotFoundException) {
            startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$pkg"))
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
