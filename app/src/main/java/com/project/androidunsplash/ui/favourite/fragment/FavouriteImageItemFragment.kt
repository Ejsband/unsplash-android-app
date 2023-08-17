package com.project.androidunsplash.ui.favourite.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.project.androidunsplash.App
import com.project.androidunsplash.MainActivity
import com.project.androidunsplash.R
import com.project.androidunsplash.databinding.FragmentFavouriteImageItemBinding
import com.project.androidunsplash.ui.favourite.FavouriteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"

@AndroidEntryPoint
class FavouriteImageItemFragment : Fragment() {

    private val imageFileName =
        SimpleDateFormat(FILENAME_FORMAT, Locale.ENGLISH).format(System.currentTimeMillis())

    private var _binding: FragmentFavouriteImageItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavouriteViewModel by viewModels()

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                Toast.makeText(requireContext(), "All permissions are granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Permissions are not granted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    lateinit var url: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFavouriteImageItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (checkConnection()) {
            viewModel.reloadUserImageItem(arguments?.getString("imageId")!!)
        } else {
            binding.reloadButton.isVisible = true
        }

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.navbar_share, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {
                    R.id.share -> {
                        try {
                            val shareIntent = Intent(Intent.ACTION_SEND)
                            shareIntent.type = "text/plain"
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Unsplash")
                            val shareMessage = url.trimIndent()
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                            startActivity(Intent.createChooser(shareIntent, "Image"))
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "Unable to share the image!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        return true
                    }

                    android.R.id.home -> {
                        when (arguments?.getString("fragmentName")!!) {
                            "search" -> {
                                val bundle = Bundle()
                                bundle.putString(
                                    "searchQuery",
                                    arguments?.getString("searchQuery")!!
                                )
                                findNavController().navigate(
                                    R.id.action_navigation_favourite_image_item_to_navigation_main_search,
                                    bundle
                                )
                                return true
                            }

                            "collection" -> {
                                val bundle = Bundle()
                                bundle.putString(
                                    "collectionId",
                                    arguments?.getString("collectionId")!!
                                )
                                findNavController().navigate(
                                    R.id.action_navigation_favourite_image_item_to_navigation_favourite_image_list,
                                    bundle
                                )
                                return true
                            }

                            "random" -> {
                                findNavController().navigate(R.id.action_navigation_favourite_image_item_to_navigation_main)
                                return true
                            }

                            "profile" -> {
                                findNavController().navigate(R.id.action_navigation_favourite_image_item_to_navigation_profile)
                                return true
                            }
                        }
                    }
                }
                return false
            }
        }, viewLifecycleOwner)

        checkPermissions()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.imageItemState.collect { imageItem ->

                    with(binding) {
                        Glide.with(image).clear(image)
                        Glide.with(image.context).load(imageItem.url.regular).into(image)
                        likeNumber.text = imageItem.likes
                        author.text = imageItem.creator.name
                        url = imageItem.url.regular

                        if (imageItem.likedByUser) {
                            like.isVisible = true
                            nolike.isVisible = false
                        } else {
                            nolike.isVisible = true
                            like.isVisible = false

                        }
                    }
                }
            }
        }

        with(binding) {
            like.setOnClickListener {
                if (checkConnection()) {
                    nolike.isVisible = true
                    like.isVisible = false
                    val likeAmount: Int = likeNumber.text.toString().toInt()
                    likeNumber.text = (likeAmount - 1).toString()
                    viewModel.dislikeImage(arguments?.getString("imageId")!!)
                }
            }
            nolike.setOnClickListener {
                if (checkConnection()) {
                    nolike.isVisible = false
                    like.isVisible = true
                    val likeAmount: Int = likeNumber.text.toString().toInt()
                    likeNumber.text = (likeAmount + 1).toString()
                    viewModel.likeImage(arguments?.getString("imageId")!!)
                }
            }
        }

        binding.download.setOnClickListener {

            if (checkConnection()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val fileName = "$imageFileName.jpg"
                    saveImageToInternalStorage(
                        getBitmapFutureTargetFromUrl(viewModel.imageItemState.value.url.regular),
                        fileName
                    )
                    createDialog(fileName)
                }
            } else {
//                val work = launchWorker(SaveImageWorker.createWorkRequest(viewModel.imageItemState.value.url.regular)).result
//
//                if (work.isDone) {
//                    createNotification()
//                }
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

    private fun reload() {
        if (checkConnection()) {
            viewModel.reloadUserImageItem(arguments?.getString("imageId")!!)
            binding.reloadButton.isVisible = false
        } else {
            binding.reloadButton.isVisible = true
        }
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

    private fun createDialog(fileName: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Image is downloaded")
            .setMessage("Do you want to open it?")
            .setPositiveButton("Yes") { dialog, id ->
                dialog.cancel()
                openImageInGallery(fileName)
            }.setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
            .show()
    }

    private fun openImageInGallery(name: String) {
        val imagePath = "storage/emulated/0/Download/$name"
        val viewImageIntent = Intent(Intent.ACTION_VIEW)
        viewImageIntent.setDataAndType(Uri.parse("$imagePath"), "image/*")
        startActivity(viewImageIntent)
    }

    private fun saveImageToInternalStorage(
        bitmapFutureTarget: FutureTarget<Bitmap>,
        fileName: String
    ) {

        viewLifecycleOwner.lifecycleScope.launch {

            val bitmap: Bitmap = withContext(Dispatchers.IO) {
                bitmapFutureTarget.get()
            }

            val directory = File("/storage/emulated/0/Download")

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

                Toast.makeText(
                    requireContext(),
                    "File $fileName was saved!",
                    Toast.LENGTH_LONG
                ).show()

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

    private fun checkPermissions() {
        var isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            Toast.makeText(requireContext(), "All permissions are granted!", Toast.LENGTH_SHORT)
                .show()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
        shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            if (Build.VERSION.SDK_INT <= 28) {
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()
    }

    // Worker

    private fun launchWorker(request: OneTimeWorkRequest): Operation {
        return WorkManager.getInstance(requireContext())
            .beginUniqueWork("saveImage", ExistingWorkPolicy.REPLACE, request).enqueue()
    }

    private fun cancelWorkers() {
        WorkManager.getInstance(requireContext())
            .cancelUniqueWork("saveImage")
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification() {

        val intent = Intent(requireContext(), MainActivity::class.java)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
        else
            PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val notification = NotificationCompat.Builder(requireContext(), App.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Alert!")
            .setContentText("Your image was downloaded!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(requireContext()).notify(1000, notification)

    }

}