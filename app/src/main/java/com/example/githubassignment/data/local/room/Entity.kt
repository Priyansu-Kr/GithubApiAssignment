package com.example.githubassignment.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "github_repos")
data class GithubRepoEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val fullName: String?,
    val description: String?,
    val stars: Int,
    val language: String?,
    val forks_count: Int,
    val stargazers_count: Int,
    val ownerAvatarUrl: String?,
    val cachedAt : Long,
    val isFavourite : Boolean
)
