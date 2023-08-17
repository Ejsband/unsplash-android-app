package com.project.androidunsplash.ui.favourite.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.androidunsplash.databinding.ViewCollectionBinding
import com.project.androidunsplash.entity.UnsplashCollection

class PagingCollectionAdapter(
    private val onClick: (Int) -> Unit
) : PagingDataAdapter<UnsplashCollection, UnsplashCollectionViewHolder>(
    DiffUtilCallbackCollectionList()
) {

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UnsplashCollectionViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.root.setOnClickListener {
            onClick(position)
        }

        with(holder.binding) {
            collectionTitle.text = item?.title
            collectionSize.text = item?.totalPhotos
            item?.let {
                Glide
                    .with(container.context)
                    .load(item.coverPhoto.url.regular)
                    .into(container)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnsplashCollectionViewHolder {
        return UnsplashCollectionViewHolder(
            ViewCollectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onClick
        )
    }
}

class UnsplashCollectionViewHolder(val binding: ViewCollectionBinding, onClick: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root)

class DiffUtilCallbackCollectionList : DiffUtil.ItemCallback<UnsplashCollection>() {

    override fun areItemsTheSame(oldItem: UnsplashCollection, newItem: UnsplashCollection): Boolean =
        oldItem.id == newItem.id

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: UnsplashCollection, newItem: UnsplashCollection): Boolean =
        oldItem == newItem
}