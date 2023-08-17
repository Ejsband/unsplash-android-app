package com.project.androidunsplash.ui.favourite.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.androidunsplash.databinding.ViewCommonImageBinding
import com.project.androidunsplash.entity.UnsplashImage

class PagingImageListAdapter(
    private val onClick: (Int) -> Unit
) : PagingDataAdapter<UnsplashImage, UnsplashImageListViewHolder>(DiffUtilCallbackImageList()) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UnsplashImageListViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.root.setOnClickListener {
            onClick(position)
        }

        with(holder.binding) {
            creatorId.text = item?.creator?.name
            likeNumber.text = item?.likes
            item?.let {
                Glide
                    .with(image.context)
                    .load(it.url.regular)
                    .into(image)
            }
            if (item!!.likedByUser) {
                like.isVisible = true
                nolike.isVisible = false
            } else {
                like.isVisible = false
                nolike.isVisible = true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnsplashImageListViewHolder {
        return UnsplashImageListViewHolder(
            ViewCommonImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick
        )
    }
}

class UnsplashImageListViewHolder(val binding: ViewCommonImageBinding, onClick: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root)

class DiffUtilCallbackImageList : DiffUtil.ItemCallback<UnsplashImage>() {

    override fun areItemsTheSame(oldItem: UnsplashImage, newItem: UnsplashImage): Boolean =
        oldItem.id == newItem.id

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: UnsplashImage, newItem: UnsplashImage): Boolean =
        oldItem == newItem
}