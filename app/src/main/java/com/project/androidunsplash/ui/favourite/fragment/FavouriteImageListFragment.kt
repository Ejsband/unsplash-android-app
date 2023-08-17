package com.project.androidunsplash.ui.favourite.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.project.androidunsplash.R
import com.project.androidunsplash.databinding.FragmentFavouriteImageListBinding
import com.project.androidunsplash.ui.favourite.FavouriteViewModel
import com.project.androidunsplash.ui.favourite.adapter.PagingImageListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FavouriteImageListFragment : Fragment() {

    private var _binding: FragmentFavouriteImageListBinding? = null

    private val binding get() = _binding!!

    lateinit var pagingAdapter: PagingImageListAdapter

    private val viewModel: FavouriteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.loadPagingImageList(arguments?.getString("collectionId")!!)
        _binding = FragmentFavouriteImageListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        pagingAdapter = PagingImageListAdapter() { position ->

            val data = pagingAdapter.snapshot()
            val bundle = Bundle()

            bundle.putString("fragmentName", "collection")
            bundle.putString("imageId", data[position]?.id.toString())
            bundle.putString("collectionId", arguments?.getString("collectionId")!!)

            findNavController().navigate(
                R.id.action_navigation_favourite_image_list_to_navigation_favourite_image_item,
                bundle
            )
        }

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigate(R.id.action_navigation_favourite_image_list_to_navigation_favourite_collection_list)
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner)

        if (checkConnection()) {
            reload()
        } else {
            binding.reloadButton.isVisible = true
        }

        pagingAdapter.addLoadStateListener {
            val refreshState = it.refresh
            if (refreshState is LoadState.Error) {
                binding.recycler.isVisible = false
                binding.reloadButton.isVisible = true
                Toast.makeText(
                    requireContext(),
                    refreshState.error.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.recycler.isVisible = true
                binding.reloadButton.isVisible = false
            }
        }

        binding.reloadButton.setOnClickListener {
            reload()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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

    private fun reload() {
        binding.recycler.adapter = pagingAdapter
        viewModel.pagingImageList.onEach {
            pagingAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}