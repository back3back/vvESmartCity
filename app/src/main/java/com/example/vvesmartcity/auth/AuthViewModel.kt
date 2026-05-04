package com.example.vvesmartcity.auth

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String = ""
)

class AuthViewModel : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    val isLoggedIn: Boolean get() = _state.value.isLoggedIn
    val currentUser: User? get() = _state.value.currentUser

    fun checkSavedSession(context: Context) {
        viewModelScope.launch {
            val savedUser = SessionManager.getSavedUser(context)
            if (savedUser != null) {
                _state.update { it.copy(
                    isLoggedIn = true,
                    currentUser = savedUser,
                    errorMessage = ""
                ) }
            }
        }
    }

    fun login(context: Context, username: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (username.isBlank()) {
                _state.update { it.copy(errorMessage = "请输入用户名") }
                return@launch
            }
            if (password.isBlank()) {
                _state.update { it.copy(errorMessage = "请输入密码") }
                return@launch
            }
            
            val user = AuthDataSource.login(username, password)
            if (user != null) {
                SessionManager.saveLogin(context, user)
                _state.update { it.copy(
                    isLoggedIn = true,
                    currentUser = user,
                    errorMessage = ""
                ) }
                onSuccess()
            } else {
                _state.update { it.copy(errorMessage = "用户名或密码错误") }
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            SessionManager.clearSession(context)
            _state.update { AuthState() }
        }
    }

    fun updateAvatar(context: Context, uri: String?) {
        viewModelScope.launch {
            val oldAvatarUri = _state.value.currentUser?.avatarUri
            if (oldAvatarUri != null && oldAvatarUri.startsWith("file://")) {
                try {
                    val oldFile = java.io.File(Uri.parse(oldAvatarUri).path ?: "")
                    if (oldFile.exists()) {
                        oldFile.delete()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            SessionManager.saveAvatarUri(context, uri)
            _state.update { it.copy(
                currentUser = it.currentUser?.copy(avatarUri = uri)
            ) }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = "") }
    }
}
