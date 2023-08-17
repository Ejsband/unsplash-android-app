package com.project.androidunsplash.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.androidunsplash.databinding.ViewLikedImageBinding
import com.project.androidunsplash.entity.UnsplashImage

class ProfileAdapter(
    values: List<UnsplashImage>,
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<ProfileViewHolder>() {

    private var values = values.toMutableList()

    fun setData(data: List<UnsplashImage>) {
        this.values = data.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val binding = ViewLikedImageBinding.inflate(LayoutInflater.from(parent.context))
        return ProfileViewHolder(binding, onClick)
    }

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val item = values[position]
        holder.binding.root.setOnClickListener {
            onClick(position)
        }
        Glide.with(holder.binding.image).clear(holder.binding.image)
        Glide.with(holder.binding.image).load(item.url.regular).into(holder.binding.image)
    }
}

class ProfileViewHolder(val binding: ViewLikedImageBinding, onClick: (Int) -> Unit) :
    RecyclerView.ViewHolder(binding.root)