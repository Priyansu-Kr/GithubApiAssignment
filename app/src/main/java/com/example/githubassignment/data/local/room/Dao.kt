package com.example.githubassignment.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GithubDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<GithubRepoEntity>)

    @Query("SELECT * FROM github_repos ORDER BY stargazers_count DESC")
    fun getAllRepos(): Flow<List<GithubRepoEntity>>


    @Query("DELETE FROM github_repos")
    suspend fun clearRepos()

    @Query("SELECT cachedAt FROM github_repos LIMIT 1")
    suspend fun getCachedAt(): Long?


    @Query("UPDATE github_repos SET isFavourite = NOT isFavourite WHERE id = :itemId")
    suspend fun toggleFavourite(itemId: Long)

    @Query("SELECT id FROM github_repos WHERE isFavourite = 1")
    suspend fun getFavouriteIds(): List<Long>

    @Query("SELECT * FROM github_repos WHERE id = :repoId")
    suspend fun getRepoById(repoId: Long): GithubRepoEntity?
}
