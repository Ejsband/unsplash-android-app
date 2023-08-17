package com.project.androidunsplash.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.project.androidunsplash.R
import com.project.androidunsplash.databinding.FragmentProfileBinding
import com.project.androidunsplash.entity.UnsplashImage
import dagger.hilt.android.AndroidEntryPoint
import hilt_aggregated_deps._com_project_androidunsplash_ui_profile_ProfileViewModel_HiltModules_BindsModule
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkConnection()) {
            viewModel.reloadUserState()
            viewModel.reloadUserLikedImageListState()
        } else {
            binding.reloadButton.isVisible = true
        }

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.navbar_logout_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {
                    R.id.logout -> {
                        createDialog()
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userState.collect { user ->
                    with(binding) {
                        Glide
                            .with(profilePhoto.context)
                            .load(user.profile_image.small)
                            .into(profilePhoto)
                        profileName.text = user.name
                        profileAlias.text = "@${user.username}"
                        profileLocation.text = user.location
                    }

                }
            }
        }

        val data: List<UnsplashImage> = viewModel.userLikedImageListState.value
        val myAdapter = ProfileAdapter(data) { position ->

            val bundle = Bundle()
            bundle.putString("fragmentName", "profile")
            bundle.putString("imageId", viewModel.userLikedImageListState.value[position].id)
            findNavController().navigate(
                R.id.action_navigation_profile_to_navigation_favourite_image_item,
                bundle
            )
        }

        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        binding.recycler.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLikedImageListState.collect { photo ->
                    myAdapter.setData(photo)
                }
            }
        }

        binding.profileLocation.setOnClickListener {
            openGoogleMaps(binding.profileLocation.text.toString())
        }

        binding.reloadButton.setOnClickListener {
            reload()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun reload() {
        if (checkConnection()) {
            viewModel.reloadUserState()
            viewModel.reloadUserLikedImageListState()
            binding.reloadButton.isVisible = false
        } else {
            binding.reloadButton.isVisible = true
        }
    }

    private fun openGoogleMaps(location: String) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:0.0?q=$location")
        )
        startActivity(intent)
    }

    fun createDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Attention")
            .setMessage("Are you sure you want to logout?")
            .setIcon(R.drawable.ic_profile)
            .setPositiveButton("Yes") { dialog, id ->
                dialog.cancel()

                viewModel.deleteMetaData()
                findNavController().navigate(
                    R.id.action_navigation_profile_to_boarding
                )


            }.setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
            .show()
    }

    private fun checkConnection(): Boolean {
        val connectionManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectionManager.activeNetworkInfo
        val isConnected = activeNetwork?.isConnectedOrConnecting == true
        return if (isConnected) {
            true
        } else {
            Toast.makeText(
                requireContext(),
                "No network",
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }
}