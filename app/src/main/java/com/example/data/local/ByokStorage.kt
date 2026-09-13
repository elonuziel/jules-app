package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class ByokStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var julesApiKey: String
        get() = prefs.getString(KEY_JULES_API_KEY, DEFAULT_JULES_KEY) ?: DEFAULT_JULES_KEY
        set(value) = prefs.edit().putString(KEY_JULES_API_KEY, value).apply()

    var githubPat: String
        get() = prefs.getString(KEY_GITHUB_PAT, DEFAULT_GITHUB_PAT) ?: DEFAULT_GITHUB_PAT
        set(value) = prefs.edit().putString(KEY_GITHUB_PAT, value).apply()

    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, true)
        set(value) = prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()

    var hasCompletedWelcome: Boolean
        get() = prefs.getBoolean(KEY_HAS_COMPLETED_WELCOME, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_COMPLETED_WELCOME, value).apply()

    fun clearGitHubPat() {
        prefs.edit().remove(KEY_GITHUB_PAT).apply()
    }

    fun clearAllKeys() {
        prefs.edit()
            .remove(KEY_JULES_API_KEY)
            .remove(KEY_GITHUB_PAT)
            .apply()
    }

    fun resetToCleanSlate() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "jules_byok_client_prefs"
        private const val KEY_JULES_API_KEY = "byok_jules_api_key"
        private const val KEY_GITHUB_PAT = "byok_github_pat"
        private const val KEY_DARK_THEME = "byok_dark_theme"
        private const val KEY_HAS_COMPLETED_WELCOME = "byok_has_completed_welcome"

        const val DEFAULT_JULES_KEY = ""
        const val DEFAULT_GITHUB_PAT = ""
    }
}
