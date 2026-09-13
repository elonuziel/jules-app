package com.example.data.remote.api

import com.example.data.remote.dto.GitHubFileDto
import com.example.data.remote.dto.GitHubMergeRequestDto
import com.example.data.remote.dto.GitHubMergeResponseDto
import com.example.data.remote.dto.GitHubPullRequestDto
import com.example.data.remote.dto.GitHubReviewRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface GitHubApiService {

    @GET("repos/{owner}/{repo}/pulls/{number}")
    suspend fun getPullRequest(
        @Header("Authorization") authHeader: String,
        @Header("Accept") accept: String = "application/vnd.github+json",
        @Header("X-GitHub-Api-Version") apiVersion: String = "2022-11-28",
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int
    ): GitHubPullRequestDto

    @GET("repos/{owner}/{repo}/pulls/{number}/files")
    suspend fun getPullRequestFiles(
        @Header("Authorization") authHeader: String,
        @Header("Accept") accept: String = "application/vnd.github+json",
        @Header("X-GitHub-Api-Version") apiVersion: String = "2022-11-28",
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int
    ): List<GitHubFileDto>

    @POST("repos/{owner}/{repo}/pulls/{number}/reviews")
    suspend fun createReview(
        @Header("Authorization") authHeader: String,
        @Header("Accept") accept: String = "application/vnd.github+json",
        @Header("X-GitHub-Api-Version") apiVersion: String = "2022-11-28",
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body body: GitHubReviewRequestDto = GitHubReviewRequestDto()
    ): Response<Unit>

    @PUT("repos/{owner}/{repo}/pulls/{number}/merge")
    suspend fun mergePullRequest(
        @Header("Authorization") authHeader: String,
        @Header("Accept") accept: String = "application/vnd.github+json",
        @Header("X-GitHub-Api-Version") apiVersion: String = "2022-11-28",
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body body: GitHubMergeRequestDto = GitHubMergeRequestDto()
    ): GitHubMergeResponseDto

    @DELETE("repos/{owner}/{repo}/git/refs/heads/{branch}")
    suspend fun deleteBranch(
        @Header("Authorization") authHeader: String,
        @Header("Accept") accept: String = "application/vnd.github+json",
        @Header("X-GitHub-Api-Version") apiVersion: String = "2022-11-28",
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path(value = "branch", encoded = true) branch: String
    ): Response<Unit>
}
