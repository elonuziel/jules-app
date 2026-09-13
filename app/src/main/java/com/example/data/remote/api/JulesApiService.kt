package com.example.data.remote.api

import com.example.data.remote.dto.JulesActivitiesResponseDto
import com.example.data.remote.dto.JulesActivityDto
import com.example.data.remote.dto.JulesCreateSessionRequestDto
import com.example.data.remote.dto.JulesSendMessageRequestDto
import com.example.data.remote.dto.JulesSessionDto
import com.example.data.remote.dto.JulesSessionsResponseDto
import com.example.data.remote.dto.JulesSourcesResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface JulesApiService {

    @GET("sources")
    suspend fun getSources(
        @Header("x-goog-api-key") apiKey: String
    ): JulesSourcesResponseDto

    @GET("sessions")
    suspend fun getSessions(
        @Header("x-goog-api-key") apiKey: String,
        @Query("pageSize") pageSize: Int = 30,
        @Query("pageToken") pageToken: String? = null
    ): JulesSessionsResponseDto

    @GET("sessions/{sessionId}")
    suspend fun getSession(
        @Header("x-goog-api-key") apiKey: String,
        @Path(value = "sessionId", encoded = true) sessionId: String
    ): JulesSessionDto

    @POST("sessions")
    suspend fun createSession(
        @Header("x-goog-api-key") apiKey: String,
        @Body body: JulesCreateSessionRequestDto
    ): JulesSessionDto

    @GET("sessions/{sessionId}/activities")
    suspend fun getSessionActivities(
        @Header("x-goog-api-key") apiKey: String,
        @Path(value = "sessionId", encoded = true) sessionId: String,
        @Query("pageSize") pageSize: Int = 50,
        @Query("pageToken") pageToken: String? = null
    ): JulesActivitiesResponseDto

    @POST("sessions/{sessionId}:approvePlan")
    suspend fun approvePlan(
        @Header("x-goog-api-key") apiKey: String,
        @Path(value = "sessionId", encoded = true) sessionId: String,
        @Body body: Map<String, String> = emptyMap()
    ): JulesSessionDto

    @POST("sessions/{sessionId}:sendMessage")
    suspend fun sendMessage(
        @Header("x-goog-api-key") apiKey: String,
        @Path(value = "sessionId", encoded = true) sessionId: String,
        @Body body: JulesSendMessageRequestDto
    ): JulesActivityDto

    @DELETE("sessions/{sessionId}")
    suspend fun deleteSession(
        @Header("x-goog-api-key") apiKey: String,
        @Path(value = "sessionId", encoded = true) sessionId: String
    ): Response<Unit>
}
