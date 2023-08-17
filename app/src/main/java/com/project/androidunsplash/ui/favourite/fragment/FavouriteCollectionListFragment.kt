package com.project.androidunsplash.ui.favourite.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.project.androidunsplash.R
import com.project.androidunsplash.databinding.FragmentFavouriteCollectionListBinding
import com.project.androidunsplash.ui.favourite.FavouriteViewModel
import com.project.androidunsplash.ui.favourite.adapter.PagingCollectionAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FavouriteCollectionListFragment : Fragment() {

    private var _binding: FragmentFavouriteCollectionListBinding? = null

    private val binding get() = _binding!!

    private val viewModel: FavouriteViewModel by viewModels()

    lateinit var pagingAdapter: PagingCollectionAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteCollectionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagingAdapter = PagingCollectionAdapter() { position ->

            val data = pagingAdapter.snapshot()
            val bundle = Bundle()

            bundle.putString("collectionId", data[position]?.id.toString())
            findNavController().navigate(
                R.id.action_navigation_favourite_collection_list_to_navigation_favourite_image_list,
                bundle
            )
        }

        if (checkConnection()) {
            reload()
        } else {
            binding.reloadButton.isVisible = true
        }



        pagingAdapter.addLoadStateListener {
            if (it.refresh is LoadState.Error) {
                binding.recycler.isVisible = false
                binding.reloadButton.isVisible = true
                Toast.makeText(
                    requireContext(),
                    "error",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.recycler.isVisible = true
                binding.reloadButton.isVisible = false
                Toast.makeText(
                    requireContext(),
                    "Success loading",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.reloadButton.setOnClickListener {
            reload()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
        viewModel.pagingCollectionList.onEach {
            pagingAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}