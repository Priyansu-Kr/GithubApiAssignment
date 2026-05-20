package com.example.githubassignment.data.remote

import com.example.githubassignment.data.dto.GithubResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkApi {

    @GET("repositories")
    suspend fun getTrending(
        @Query("q") q: String = "stars:>1",
        @Query("per_page") per_page :Int = 50,
        @Query("page") page :Int = 1,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc"
    ): GithubResponseDto

}
