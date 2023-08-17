package com.project.androidunsplash.ui.boarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.androidunsplash.R
import com.project.androidunsplash.databinding.FragmentBoardingBinding
import com.project.androidunsplash.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BoardingFragment : Fragment() {

    private var _binding: FragmentBoardingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.hide()

        viewModel.checkIfMetaDataExists()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isTokenCorrect.observe(viewLifecycleOwner) {
                if (it != false) {
                    findNavController().navigate(R.id.action_boarding_to_navigation_main)
                } else {
                }
            }
        }

        binding.loginButton.setOnClickListener {
            openBrowser()
        }
    }

    private fun openBrowser() {
        val intent = Intent(Intent.ACTION_VIEW, composeUrl())
        requireActivity().startActivity(intent)
    }

//    private fun composeUrl(): Uri =
//        Uri.parse("https://unsplash.com/oauth/authorize")
//            .buildUpon()
//            .appendQueryParameter(
//                "client_id",
//                "iC2wjAuzgkMrgNmeG5P15wh8FG1xLflKe0A8urB2ZcI"
//            )
//            .appendQueryParameter(
//                "redirect_uri",
//                "app://oauth"
//            )
//            .appendQueryParameter("response_type", "code")
//            .appendQueryParameter(
//                "scope",
//                "public read_user write_user read_photos write_photos write_likes write_followers read_collections write_collections"
//            )
//            .build()

    private fun composeUrl(): Uri =
        Uri.parse("https://www.reddit.com/api/v1/authorize")
            .buildUpon()
            .appendQueryParameter("client_id", "yDzKaSHnxkkPfRbZtIIMRA")
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("state", "statestring")
            .appendQueryParameter("redirect_uri", "app://oauth")
            .appendQueryParameter("duration", "permanent")
            .appendQueryParameter("scope", "identity")
            .build()
}