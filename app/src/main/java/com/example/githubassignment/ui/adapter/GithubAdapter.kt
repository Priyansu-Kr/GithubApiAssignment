package com.example.githubassignment.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubassignment.R
import com.example.githubassignment.data.local.room.GithubRepoEntity
import com.example.githubassignment.databinding.ItemLayoutBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubassignment.utils.Constants

class GithubAdapter(
    private val onFavoriteClick: (GithubRepoEntity) -> Unit
) : ListAdapter<GithubRepoEntity, GithubAdapter.GithubViewHolder>(DiffCallback) {

    private val viewPool = RecyclerView.RecycledViewPool()

    class GithubViewHolder(private val binding: ItemLayoutBinding,private val viewPool: RecyclerView.RecycledViewPool) :
        RecyclerView.ViewHolder(binding.root) {
        
        private val contributorAdapter = ContributorAdapter()

        init {
            binding.rvContributors.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = contributorAdapter
                setHasFixedSize(true)
                isNestedScrollingEnabled = false
                setRecycledViewPool(viewPool)
            }


        }

        fun bind(item: GithubRepoEntity, onFavoriteClick: (GithubRepoEntity) -> Unit) {
            binding.apply {
                val formattedText = Constants.getFormattedText(item.fullName)
                tvRepoName.text = formattedText
                tvDescription.text = item.description
                tvLanguage.text = item.language
                langColor.background.setTint(Constants.getColor(item.language))
                tvStars.text = item.stars.toString()
                tvForks.text = item.forks_count.toString()
                tvTrendingStars.text = "${item.stargazers_count} stars today"

                val favoriteRes = if (item.isFavourite) R.drawable.ic_heart_filled else R.drawable.ic_heart
                ivFavorite.setImageResource(favoriteRes)

                ivFavorite.setOnClickListener {
                    onFavoriteClick(item)
                }

                item.contributors?.let {
                    contributorAdapter.submitList(it)

                }
            }
        }

        // Partial update function
        fun updateFavorite(isFavorite: Boolean) {

            val favoriteRes =
                if (isFavorite) {
                    R.drawable.ic_heart_filled
                } else {
                    R.drawable.ic_heart
                }

            binding.ivFavorite.setImageResource(favoriteRes)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GithubViewHolder {
        val binding = ItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GithubViewHolder(binding,viewPool)
    }

    override fun onBindViewHolder(holder: GithubViewHolder, position: Int) {
        holder.bind(getItem(position), onFavoriteClick)
    }

    override fun onBindViewHolder(
        holder: GithubViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        if (payloads.isNotEmpty()) {

            payloads.forEach { payload ->

                when (payload) {

                    FAVORITE_PAYLOAD -> {

                        holder.updateFavorite(
                            getItem(position).isFavourite
                        )
                    }
                }
            }

        } else {

            super.onBindViewHolder(
                holder,
                position,
                payloads
            )
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<GithubRepoEntity>() {

        private const val FAVORITE_PAYLOAD =
            "FAVORITE_PAYLOAD"

        override fun areItemsTheSame(oldItem: GithubRepoEntity, newItem: GithubRepoEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GithubRepoEntity, newItem: GithubRepoEntity): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(
            oldItem: GithubRepoEntity,
            newItem: GithubRepoEntity
        ): Any? {

            return if (
                oldItem.isFavourite != newItem.isFavourite
            ) {

                FAVORITE_PAYLOAD

            } else {

                null
            }
        }
    }
}
