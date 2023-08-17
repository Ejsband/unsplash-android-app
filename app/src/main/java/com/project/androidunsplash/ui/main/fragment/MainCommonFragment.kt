package com.project.androidunsplash.ui.main.fragment

import android.content.Context
import android.graphics.Bitmap
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.project.androidunsplash.R
import com.project.androidunsplash.databinding.FragmentMainCommonBinding
import com.project.androidunsplash.entity.CachedImage
import com.project.androidunsplash.ui.main.adapter.CachedImageAdapter
import com.project.androidunsplash.ui.main.MainViewModel
import com.project.androidunsplash.ui.main.adapter.PagingImageAdapter
import com.project.androidunsplash.ui.main.adapter.PagingSearchImageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class MainCommonFragment : Fragment() {

    private var _binding: FragmentMainCommonBinding? = null
    private val binding get() = _binding!!

    lateinit var pagingAdapter: PagingImageAdapter

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.navbar_search, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.search -> {
                        binding.searchGroup.isVisible = binding.searchGroup.isVisible != true
                        return true
                    }
                }
                return false
            }
        }, viewLifecycleOwner)

        _binding = FragmentMainCommonBinding.inflate(inflater, container, false)
        return binding.root
    }

    //    @SuppressLint("CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagingAdapter = PagingImageAdapter { position ->

            val data = pagingAdapter.snapshot()
            val bundle = Bundle()

            bundle.putString("fragmentName", "random")
            bundle.putString("imageId", data[position]?.id.toString())
            findNavController().navigate(
                R.id.action_navigation_main_to_navigation_favourite_image_item, bundle)
        }

        (activity as AppCompatActivity).supportActionBar?.show()

        if (checkConnection()) {
            reload()
        } else {
            binding.reloadButton.isVisible = true

            viewLifecycleOwner.lifecycleScope.launch {

                val list = withContext(Dispatchers.IO) {
                    viewModel.cachedImage.getCachedImageData()
                }

                if (list.isNotEmpty()) {
                    binding.recycler.layoutManager = GridLayoutManager(requireContext(), 1)
                    binding.recycler.adapter = CachedImageAdapter(list)
                }
            }

            Toast.makeText(
                requireContext(),
                "Ошибка загрузки, фото из кэша",
                Toast.LENGTH_LONG
            ).show()
        }

        pagingAdapter.addLoadStateListener {
            val refreshState = it.refresh
            if (refreshState is LoadState.Error) {
                binding.recycler.isVisible = false
                binding.reloadButton.isVisible = true
            } else {
                binding.recycler.isVisible = true
                binding.reloadButton.isVisible = false
            }
        }

        binding.reloadButton.setOnClickListener {
            if (checkConnection())  {
                reload()
            } else {
                binding.reloadButton.isVisible = true

                viewLifecycleOwner.lifecycleScope.launch {

                    val list = withContext(Dispatchers.IO) {
                        viewModel.cachedImage.getCachedImageData()
                    }

                    if (list.isNotEmpty()) {
                        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 1)
                        binding.recycler.adapter = CachedImageAdapter(list)
                    }
                }

                Toast.makeText(
                    requireContext(),
                    "Ошибка загрузки, фото из кэша",
                    Toast.LENGTH_LONG
                ).show()
            }

        }


        binding.searchButton.setOnClickListener {
            val input = binding.textInputEditText.text
            if (input.toString() == "") {
                Toast.makeText(requireContext(), "No text in input", Toast.LENGTH_LONG).show()
            } else {

                val bundle = Bundle()
                bundle.putString("searchQuery", input.toString())
                findNavController()
                    .navigate(R.id.action_navigation_main_to_navigation_main_search, bundle)
            }
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

        viewLifecycleOwner.lifecycleScope.launch {
            delay(3000)
            val data = pagingAdapter.snapshot()
            if (data.size > 0) {
                withContext(Dispatchers.IO) {


                    if (viewModel.cachedImage.getCachedImageData().isNotEmpty()) {
                        Log.d("ALERT!!!!", "DELETING ALL DATA")
                        viewModel.cachedImage.deleteCachedImageData()
                        Log.d("ALERT!!!!", "SAVING ALL DATA")
                        for (i in data) {

                            val fileName = "image_${i?.id.toString()}.jpg"
                            val path = "/storage/emulated/0/Pictures/$fileName"
                            saveImageToInternalStorage(
                                getBitmapFutureTargetFromUrl(i?.url?.regular.toString()),
                                fileName
                            )
                            viewModel.saveCachedImage(
                                CachedImage(
                                    i!!.id,
                                    path,
                                    i.likes.toInt(),
                                    i.likedByUser,
                                    i.creator.name
                                )
                            )
                        }
                    } else {
                        Log.d("ALERT!!!!", "SAVING ALL DATA")
                        for (i in data) {

                            val fileName = "image_${i?.id.toString()}.jpg"
                            val path = "/storage/emulated/0/Pictures/$fileName"
                            saveImageToInternalStorage(
                                getBitmapFutureTargetFromUrl(i?.url?.regular.toString()),
                                fileName
                            )

                            viewModel.saveCachedImage(
                                CachedImage(
                                    i!!.id,
                                    path,
                                    i.likes.toInt(),
                                    i.likedByUser,
                                    i.creator.name
                                )
                            )
                        }
                    }
                }
            }
        }

        viewModel.pagingRandomImageList.onEach {
            pagingAdapter.submitData(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun saveImageToInternalStorage(
        bitmapFutureTarget: FutureTarget<Bitmap>,
        fileName: String
    ) {

        viewLifecycleOwner.lifecycleScope.launch {

            val bitmap: Bitmap = withContext(Dispatchers.IO) {
                bitmapFutureTarget.get()
            }

            val directory = File("/storage/emulated/0/Pictures/")

            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)

            try {
                withContext(Dispatchers.IO) {
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                }

            } catch (e: IOException) {
                Toast.makeText(
                    requireContext(),
                    "Unable to save the file!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun getBitmapFutureTargetFromUrl(url: String): FutureTarget<Bitmap> {
        return Glide.with(requireContext()).asBitmap().load(url).submit()
    }
}