package com.example.githubassignment.data.dto

data class GithubResponseDto(
    val incomplete_results: Boolean,
    val items: List<ItemDto>,
    val total_count: Int
)