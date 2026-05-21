package com.example.githubassignment.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "github_repos")
@TypeConverters(Converters::class)
data class GithubRepoEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val stars: Int,
    val language: String?,
    val forks_count: Int,
    val stargazers_count: Int,
    val ownerAvatarUrl: String?,
    val contributorUrl : String,
    val contributors: List<String> = emptyList(),
    val cachedAt : Long,
    val isFavourite : Boolean
)
