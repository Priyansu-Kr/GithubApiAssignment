package com.example.githubassignment.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubassignment.databinding.ContributorsItemLayoutBinding

class ContributorAdapter : ListAdapter<String, ContributorAdapter.ContributorViewHolder>(DiffCallback) {

    class ContributorViewHolder(private val binding: ContributorsItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            Glide.with(binding.ivAvatar.context)
                .load(url)
                .circleCrop()
                .into(binding.ivAvatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorViewHolder {
        val binding = ContributorsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ContributorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
