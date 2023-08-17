package com.project.androidunsplash.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.androidunsplash.databinding.ViewCommonImageBinding
import com.project.androidunsplash.entity.CachedImage

class CachedImageAdapter(
values: List<CachedImage>
) : RecyclerView.Adapter<CachedImageViewHolder>() {

    private var values = values.toMutableList()

    fun setData(data: List<CachedImage>) {
        this.values = data.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CachedImageViewHolder {
        val binding = ViewCommonImageBinding.inflate(LayoutInflater.from(parent.context))
        return CachedImageViewHolder(binding)
    }

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: CachedImageViewHolder, position: Int) {
        val item = values[position]

        with(holder.binding) {
            creatorId.text = item.userName
            likeNumber.text = item.likes.toString()
            item.let {
                Glide
                    .with(image.context)
                    .load(it.source)
                    .into(image)
            }
            if (item.likedByUser) {
                like.isVisible = true
                nolike.isVisible = false
            } else {
                like.isVisible = false
                nolike.isVisible = true
            }
        }
    }
}

class CachedImageViewHolder(val binding: ViewCommonImageBinding) :
    RecyclerView.ViewHolder(binding.root)