package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GitHubPullRequestDto(
    @Json(name = "number") val number: Int,
    @Json(name = "title") val title: String? = null,
    @Json(name = "state") val state: String? = null,
    @Json(name = "merged") val merged: Boolean? = false,
    @Json(name = "mergeable") val mergeable: Boolean? = null,
    @Json(name = "html_url") val htmlUrl: String? = null,
    @Json(name = "head") val head: GitHubBranchRefDto? = null,
    @Json(name = "base") val base: GitHubBranchRefDto? = null,
    @Json(name = "additions") val additions: Int? = 0,
    @Json(name = "deletions") val deletions: Int? = 0,
    @Json(name = "changed_files") val changedFiles: Int? = 0
)

@JsonClass(generateAdapter = true)
data class GitHubBranchRefDto(
    @Json(name = "ref") val ref: String? = null,
    @Json(name = "sha") val sha: String? = null
)

@JsonClass(generateAdapter = true)
data class GitHubFileDto(
    @Json(name = "filename") val filename: String,
    @Json(name = "status") val status: String? = "modified",
    @Json(name = "additions") val additions: Int = 0,
    @Json(name = "deletions") val deletions: Int = 0,
    @Json(name = "changes") val changes: Int = 0,
    @Json(name = "patch") val patch: String? = null,
    @Json(name = "raw_url") val rawUrl: String? = null,
    @Json(name = "blob_url") val blobUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class GitHubReviewRequestDto(
    @Json(name = "event") val event: String = "APPROVE",
    @Json(name = "body") val body: String = "Approved via Jules Mobile"
)

@JsonClass(generateAdapter = true)
data class GitHubMergeRequestDto(
    @Json(name = "merge_method") val mergeMethod: String = "squash"
)

@JsonClass(generateAdapter = true)
data class GitHubMergeResponseDto(
    @Json(name = "sha") val sha: String? = null,
    @Json(name = "merged") val merged: Boolean? = false,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class GitHubUserDto(
    @Json(name = "login") val login: String = "",
    @Json(name = "name") val name: String? = null,
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "html_url") val htmlUrl: String? = null,
    @Json(name = "public_repos") val publicRepos: Int? = 0
)
