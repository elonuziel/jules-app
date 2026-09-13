package com.example.data.remote

import android.util.Log
import com.example.data.remote.api.GitHubApiService
import com.example.data.remote.api.JulesApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.random.Random

object NetworkClient {

    private const val TAG = "NetworkClient"
    const val JULES_BASE_URL = "https://jules.googleapis.com/v1alpha/"
    const val GITHUB_BASE_URL = "https://api.github.com/"

    private const val TRUSTED_JULES_HOST = "jules.googleapis.com"
    private const val TRUSTED_GITHUB_HOST = "api.github.com"

    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    /**
     * Interceptor enforcing client-side isolation:
     * Credentials (Jules API Key and GitHub PAT) are strictly prevented
     * from being transmitted anywhere outside authorized Google and GitHub endpoints.
     */
    private val clientSideIsolationInterceptor = Interceptor { chain ->
        val request = chain.request()
        val host = request.url.host

        val julesKey = request.header("x-goog-api-key")
        if (julesKey != null && host != TRUSTED_JULES_HOST) {
            Log.e(TAG, "Security violation: Blocked Jules API Key transmission to untrusted host $host")
            throw SecurityException("Client-side isolation blocked transmission of Jules API key to untrusted host $host")
        }

        val authHeader = request.header("Authorization")
        if (authHeader != null && host != TRUSTED_GITHUB_HOST) {
            Log.e(TAG, "Security violation: Blocked GitHub PAT transmission to untrusted host $host")
            throw SecurityException("Client-side isolation blocked transmission of GitHub PAT to untrusted host $host")
        }

        chain.proceed(request)
    }

    /**
     * Interceptor with exponential backoff and jitter for HTTP 429 and 5xx responses.
     */
    private val rateLimitAndRetryInterceptor = Interceptor { chain ->
        val request = chain.request()
        var response: Response? = null
        var attempt = 0
        val maxRetries = 3

        while (attempt <= maxRetries) {
            try {
                response = chain.proceed(request)
                val code = response.code

                // Check if response requires retry (HTTP 429 Too Many Requests or 5xx Server Error)
                if ((code == 429 || code in 500..599) && attempt < maxRetries) {
                    val retryAfterSec = response.header("Retry-After")?.toLongOrNull()
                    response.close()

                    val backoffMs = if (retryAfterSec != null && retryAfterSec > 0) {
                        retryAfterSec * 1000L + Random.nextLong(100, 300)
                    } else {
                        (800L * (1 shl attempt)) + Random.nextLong(100, 400)
                    }

                    Log.w(TAG, "HTTP $code from ${request.url}. Retrying attempt ${attempt + 1}/$maxRetries after ${backoffMs}ms")
                    try {
                        Thread.sleep(backoffMs)
                    } catch (ie: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw IOException("Retry interrupted", ie)
                    }
                    attempt++
                    continue
                }

                return@Interceptor response
            } catch (ioe: IOException) {
                if (attempt < maxRetries) {
                    val backoffMs = (800L * (1 shl attempt)) + Random.nextLong(100, 400)
                    Log.w(TAG, "Network exception on ${request.url}. Retrying attempt ${attempt + 1}/$maxRetries after ${backoffMs}ms", ioe)
                    try {
                        Thread.sleep(backoffMs)
                    } catch (ie: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw IOException("Retry interrupted", ie)
                    }
                    attempt++
                } else {
                    throw ioe
                }
            }
        }

        response ?: chain.proceed(request)
    }

    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(clientSideIsolationInterceptor)
        .addInterceptor(rateLimitAndRetryInterceptor)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    val julesApi: JulesApiService = Retrofit.Builder()
        .baseUrl(JULES_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(JulesApiService::class.java)

    val gitHubApi: GitHubApiService = Retrofit.Builder()
        .baseUrl(GITHUB_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(GitHubApiService::class.java)
}
