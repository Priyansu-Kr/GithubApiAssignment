package com.example.githubassignment.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubassignment.R
import com.example.githubassignment.data.local.room.GithubRepoEntity
import com.example.githubassignment.databinding.ItemLayoutBinding

class GithubAdapter(
    private val onFavoriteClick: (GithubRepoEntity) -> Unit
) : ListAdapter<GithubRepoEntity, GithubAdapter.GithubViewHolder>(DiffCallback) {

    class GithubViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GithubRepoEntity, onFavoriteClick: (GithubRepoEntity) -> Unit) {
            binding.apply {
                tvRepoName.text = item.fullName
                tvDescription.text = item.description
                tvLanguage.text = item.language
                tvStars.text = item.stars.toString()
                tvForks.text = item.forks_count.toString()
                tvTrendingStars.text = "${item.stargazers_count} stars today"

                val favoriteRes = if (item.isFavourite) R.drawable.ic_heart_filled else R.drawable.ic_heart
                ivFavorite.setImageResource(favoriteRes)

                ivFavorite.setOnClickListener {
                    onFavoriteClick(item)
                }

                Glide.with(ivOwner.context)
                    .load(item.ownerAvatarUrl)
                    .circleCrop()
                    .into(ivOwner)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GithubViewHolder {
        val binding = ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GithubViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GithubViewHolder, position: Int) {
        holder.bind(getItem(position), onFavoriteClick)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GithubRepoEntity>() {
        override fun areItemsTheSame(oldItem: GithubRepoEntity, newItem: GithubRepoEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GithubRepoEntity, newItem: GithubRepoEntity): Boolean {
            return oldItem == newItem
        }
    }
}
