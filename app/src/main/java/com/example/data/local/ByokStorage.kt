package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class ByokStorage(context: Context) {

    private val prefs: SharedPreferences = createEncryptedPrefs(context)

    var julesApiKey: String
        get() = prefs.getString(KEY_JULES_API_KEY, DEFAULT_JULES_KEY) ?: DEFAULT_JULES_KEY
        set(value) = prefs.edit().putString(KEY_JULES_API_KEY, value.trim()).apply()

    var githubPat: String
        get() = prefs.getString(KEY_GITHUB_PAT, DEFAULT_GITHUB_PAT) ?: DEFAULT_GITHUB_PAT
        set(value) = prefs.edit().putString(KEY_GITHUB_PAT, value.trim()).apply()

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
        private const val TAG = "ByokStorage"
        private const val SECURE_PREFS_NAME = "jules_secure_byok_prefs"
        private const val LEGACY_PREFS_NAME = "jules_byok_client_prefs"
        private const val KEY_JULES_API_KEY = "byok_jules_api_key"
        private const val KEY_GITHUB_PAT = "byok_github_pat"
        private const val KEY_DARK_THEME = "byok_dark_theme"
        private const val KEY_HAS_COMPLETED_WELCOME = "byok_has_completed_welcome"

        const val DEFAULT_JULES_KEY = ""
        const val DEFAULT_GITHUB_PAT = ""

        private fun createEncryptedPrefs(context: Context): SharedPreferences {
            return try {
                val masterKey = MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val securePrefs = EncryptedSharedPreferences.create(
                    context,
                    SECURE_PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                // Migrate legacy unencrypted prefs if they exist
                val legacyPrefs = context.getSharedPreferences(LEGACY_PREFS_NAME, Context.MODE_PRIVATE)
                if (legacyPrefs.all.isNotEmpty()) {
                    val editor = securePrefs.edit()
                    legacyPrefs.all.forEach { (key, value) ->
                        when (value) {
                            is String -> editor.putString(key, value)
                            is Boolean -> editor.putBoolean(key, value)
                            is Int -> editor.putInt(key, value)
                            is Long -> editor.putLong(key, value)
                            is Float -> editor.putFloat(key, value)
                        }
                    }
                    editor.apply()
                    legacyPrefs.edit().clear().apply()
                    Log.i(TAG, "Successfully migrated legacy keys to Keystore-backed EncryptedSharedPreferences")
                }

                securePrefs
            } catch (e: Exception) {
                Log.w(TAG, "Keystore unavailable, falling back to private SharedPreferences", e)
                context.getSharedPreferences(SECURE_PREFS_NAME, Context.MODE_PRIVATE)
            }
        }
    }
}

