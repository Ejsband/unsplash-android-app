package com.project.androidunsplash.ui.main.fragment

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
import com.project.androidunsplash.databinding.FragmentMainSearchBinding
import com.project.androidunsplash.ui.favourite.adapter.PagingImageListAdapter
import com.project.androidunsplash.ui.main.MainViewModel
import com.project.androidunsplash.ui.main.adapter.PagingSearchImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainSearchFragment : Fragment() {

    private var _binding: FragmentMainSearchBinding? = null
    private val binding get() = _binding!!

    lateinit var pagingAdapter: PagingSearchImageAdapter

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigate(R.id.action_navigation_main_search_to_navigation_main)
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner)

        viewModel.loadPagingSearchImageList(arguments?.getString("searchQuery")!!)

        _binding = FragmentMainSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagingAdapter = PagingSearchImageAdapter() { position ->

            val data = pagingAdapter.snapshot()
            val bundle = Bundle()

            bundle.putString("fragmentName", "search")
            bundle.putString("imageId", data[position]?.id.toString())
            bundle.putString("searchQuery", arguments?.getString("searchQuery")!!)
            findNavController().navigate(
                R.id.action_navigation_main_search_to_navigation_favourite_image_item,
                bundle
            )
        }

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
                Log.d("ALERT", refreshState.error.localizedMessage)
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
        viewModel.pagingSearchImageList.onEach {
            pagingAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
}