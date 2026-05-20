package com.example.githubassignment.data.repository

import android.util.Log
import com.example.githubassignment.data.local.room.GithubDao
import com.example.githubassignment.data.local.room.GithubRepoEntity
import com.example.githubassignment.data.remote.NetworkApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepository @Inject constructor(
    private val api: NetworkApi,
    private val dao: GithubDao
) {

    val cacheDifference = 10 * 1000

    suspend fun refreshRepos() {
        try {
            Log.d("repoFromNetwork", "###########################${isCacheExpired()}")
            val favouriteIds = dao.getFavouriteIds().toSet()
            val response = api.getTrending()
            Log.d("NetworkResponse", "$response")
            val entities = response.items.map { dto ->
                GithubRepoEntity(
                    id = dto.id.toLong(),
                    name = dto.name,
                    fullName = dto.full_name,
                    description = dto.description,
                    stars = dto.stargazers_count,
                    language = dto.language ?: "Default",
                    forks_count = dto.forks_count,
                    ownerAvatarUrl = dto.owner.avatar_url,
                    stargazers_count = dto.stargazers_count,
                    cachedAt = System.currentTimeMillis(),
                    isFavourite = favouriteIds.contains(dto.id.toLong()),
                )
            }
            dao.clearRepos()
            dao.insertRepos(entities)

        } catch (e: Exception) {
            Log.e("GithubRepository", "Error refreshing repos", e)
            throw e
        }
    }

    fun getRepoFromRoom() : Flow<List<GithubRepoEntity>>{
        Log.d("repoFromRoom", "$$$$$$$$$$$$$$$$$$$$$$$$$$$")
        return dao.getAllRepos()
    }

    suspend fun updateFavourite(itemId: Long) {
        dao.toggleFavourite(itemId)
    }

    suspend fun isCacheExpired() : Boolean{
        val lastUpdated = dao.getCachedAt()
        val currentTime = System.currentTimeMillis()
        return if (lastUpdated != null) (currentTime - lastUpdated) > cacheDifference else true
    }
}
