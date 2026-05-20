package com.example.githubassignment.ui.screens

import com.example.githubassignment.data.local.room.GithubRepoEntity

sealed class UiState {
    object Loading : UiState()
    data class Success(val data: List<GithubRepoEntity>) : UiState()
    data class Error(val message: String) : UiState()
}
