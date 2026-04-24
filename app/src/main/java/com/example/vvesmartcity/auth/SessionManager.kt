package com.example.vvesmartcity.auth

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS_NAME = "smart_city_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_LOGIN_TIME = "login_time"
    private const val SESSION_TIMEOUT = 7 * 24 * 60 * 60 * 1000L

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveLogin(context: Context, user: User) {
        val prefs = getPrefs(context)
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USERNAME, user.username)
            putString(KEY_PASSWORD, user.password)
            putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
            apply()
        }
    }

    fun getSavedUser(context: Context): User? {
        val prefs = getPrefs(context)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        if (!isLoggedIn) return null

        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0)
        if (System.currentTimeMillis() - loginTime > SESSION_TIMEOUT) {
            clearSession(context)
            return null
        }

        val username = prefs.getString(KEY_USERNAME, null) ?: return null
        val password = prefs.getString(KEY_PASSWORD, null) ?: return null

        return AuthDataSource.login(username, password)
    }

    fun clearSession(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_USERNAME)
            remove(KEY_PASSWORD)
            remove(KEY_LOGIN_TIME)
            apply()
        }
    }
}
