package com.example.githubassignment.data.remote

import com.example.githubassignment.data.dto.ContributorsDtoItem
import com.example.githubassignment.data.dto.GithubResponseDto
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface NetworkApi {

    @GET("search/repositories")
    suspend fun getTrending(
        @Query("q") q: String = "stars:>1",
        @Query("per_page") per_page :Int = 8,
        @Query("page") page :Int = 1,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc"
    ): GithubResponseDto

    @GET
    suspend fun getContributors(
        @Url contributorsUrl: String
    ): List<ContributorsDtoItem>

}
