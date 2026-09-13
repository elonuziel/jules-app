package com.example.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JulesSourcesResponseDto(
    @Json(name = "sources") val sources: List<JulesSourceDto> = emptyList(),
    @Json(name = "nextPageToken") val nextPageToken: String? = null
)

@JsonClass(generateAdapter = true)
data class JulesSourceDto(
    @Json(name = "name") val name: String,
    @Json(name = "id") val id: String? = null,
    @Json(name = "githubRepo") val githubRepo: JulesGitHubRepoDto? = null
)

@JsonClass(generateAdapter = true)
data class JulesGitHubRepoDto(
    @Json(name = "owner") val owner: String,
    @Json(name = "repo") val repo: String,
    @Json(name = "defaultBranch") val defaultBranch: String = "main",
    @Json(name = "branches") val branches: List<JulesGitHubBranchDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class JulesGitHubBranchDto(
    @Json(name = "displayName") val displayName: String
)

@JsonClass(generateAdapter = true)
data class JulesSessionsResponseDto(
    @Json(name = "sessions") val sessions: List<JulesSessionDto> = emptyList(),
    @Json(name = "nextPageToken") val nextPageToken: String? = null
)

@JsonClass(generateAdapter = true)
data class JulesSessionDto(
    @Json(name = "name") val name: String,
    @Json(name = "id") val id: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "prompt") val prompt: String? = null,
    @Json(name = "state") val state: String? = null,
    @Json(name = "createTime") val createTime: String? = null,
    @Json(name = "updateTime") val updateTime: String? = null,
    @Json(name = "sourceContext") val sourceContext: JulesSourceContextDto? = null,
    @Json(name = "outputs") val outputs: List<JulesOutputDto>? = null
)

@JsonClass(generateAdapter = true)
data class JulesSourceContextDto(
    @Json(name = "source") val source: String? = null,
    @Json(name = "githubRepoContext") val githubRepoContext: JulesRepoContextDto? = null
)

@JsonClass(generateAdapter = true)
data class JulesRepoContextDto(
    @Json(name = "startingBranch") val startingBranch: String? = null
)

@JsonClass(generateAdapter = true)
data class JulesOutputDto(
    @Json(name = "pullRequest") val pullRequest: JulesPullRequestOutputDto? = null
)

@JsonClass(generateAdapter = true)
data class JulesPullRequestOutputDto(
    @Json(name = "url") val url: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null
)

@JsonClass(generateAdapter = true)
data class JulesCreateSessionRequestDto(
    @Json(name = "prompt") val prompt: String,
    @Json(name = "title") val title: String,
    @Json(name = "sourceContext") val sourceContext: JulesSourceContextDto,
    @Json(name = "requirePlanApproval") val requirePlanApproval: Boolean = false,
    @Json(name = "automationMode") val automationMode: String = "AUTOMATION_MODE_UNSPECIFIED"
)

@JsonClass(generateAdapter = true)
data class JulesSendMessageRequestDto(
    @Json(name = "prompt") val prompt: String
)

@JsonClass(generateAdapter = true)
data class JulesActivitiesResponseDto(
    @Json(name = "activities") val activities: List<JulesActivityDto> = emptyList(),
    @Json(name = "nextPageToken") val nextPageToken: String? = null
)

@JsonClass(generateAdapter = true)
data class JulesActivityDto(
    @Json(name = "name") val name: String? = null,
    @Json(name = "id") val id: String? = null,
    @Json(name = "createTime") val createTime: String? = null,
    @Json(name = "originator") val originator: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "planGenerated") val planGenerated: JulesPlanGeneratedDto? = null,
    @Json(name = "agentMessaged") val agentMessaged: JulesAgentMessagedDto? = null,
    @Json(name = "userMessaged") val userMessaged: JulesUserMessagedDto? = null,
    @Json(name = "progressUpdated") val progressUpdated: JulesProgressUpdatedDto? = null,
    @Json(name = "sessionFailed") val sessionFailed: JulesSessionFailedDto? = null
)

@JsonClass(generateAdapter = true)
data class JulesPlanGeneratedDto(
    @Json(name = "plan") val plan: JulesPlanDto? = null
)

@JsonClass(generateAdapter = true)
data class JulesPlanDto(
    @Json(name = "steps") val steps: List<JulesPlanStepDto>? = null
)

@JsonClass(generateAdapter = true)
data class JulesPlanStepDto(
    @Json(name = "title") val title: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "index") val index: Int? = null
)

@JsonClass(generateAdapter = true)
data class JulesAgentMessagedDto(
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class JulesUserMessagedDto(
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class JulesProgressUpdatedDto(
    @Json(name = "progressPercent") val progressPercent: Int? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class JulesSessionFailedDto(
    @Json(name = "reason") val reason: String? = null
)
