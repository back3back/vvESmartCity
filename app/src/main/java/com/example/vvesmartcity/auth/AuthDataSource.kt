package com.example.vvesmartcity.auth

data class User(
    val username: String,
    val password: String,
    val displayName: String,
    val role: String,
    var avatarUri: String? = null
)

object AuthDataSource {
    private val users = listOf(
        User(
            username = "admin",
            password = "123456",
            displayName = "系统管理员",
            role = "管理员"
        ),
        User(
            username = "user",
            password = "123456",
            displayName = "普通用户",
            role = "用户"
        )
    )

    fun login(username: String, password: String): User? {
        return users.find { it.username == username && it.password == password }
    }
}
