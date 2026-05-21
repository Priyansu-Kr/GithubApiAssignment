package com.example.githubassignment.data.repository

import com.example.githubassignment.data.local.room.GithubDao
import com.example.githubassignment.data.local.room.GithubRepoEntity
import com.example.githubassignment.data.remote.NetworkApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubRepository @Inject constructor(
    private val api: NetworkApi,
    private val dao: GithubDao
) {

    val cacheDifference = 5*60*60 * 1000

    suspend fun refreshRepos() = withContext(Dispatchers.IO) {
        try {
            val favouriteIds = dao.getFavouriteIds().toSet()
            val response = api.getTrending()

            
            val entities = response.items.take(5).mapIndexed { index, dto ->
                async {
                    delay(index * 200L)
                    val contributors = try {
                        val list = api.getContributors(dto.contributors_url).map { it.avatar_url }
                        list
                    } catch (e: Exception) {
                        val existingRepo = dao.getRepoById(dto.id.toLong())
                        val existingContributors = existingRepo?.contributors
                        if (existingContributors != null && existingContributors.isNotEmpty()) {
                            existingContributors
                        } else {
                            throw e
                        }
                    }
                    GithubRepoEntity(
                        id = dto.id.toLong(),
                        name = dto.name,
                        fullName = dto.full_name,
                        description = dto.description,
                        stars = dto.stargazers_count,
                        language = dto.language ?: "Default",
                        forks_count = dto.forks_count,
                        ownerAvatarUrl = dto.owner.avatar_url,
                        contributorUrl = dto.contributors_url,
                        contributors = contributors,
                        stargazers_count = dto.stargazers_count,
                        cachedAt = System.currentTimeMillis(),
                        isFavourite = favouriteIds.contains(dto.id.toLong()),
                    )
                }
            }.awaitAll()

            dao.clearRepos()
            dao.insertRepos(entities)

        } catch (e: Exception) {
            throw e
        }
    }

    fun getRepoFromRoom() : Flow<List<GithubRepoEntity>>{
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
