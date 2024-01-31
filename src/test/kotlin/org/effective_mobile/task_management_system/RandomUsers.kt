package org.effective_mobile.task_management_system

import net.datafaker.Faker
import org.effective_mobile.task_management_system.database.entity.Role
import org.effective_mobile.task_management_system.database.entity.User

object RandomUsers {
    fun user(passwordEncoding: () -> String) =
        user(password = passwordEncoding())

    fun user(username: String = username(),
             password: String = safePassword(),
             roles: List<Role> = listOf()
    ): User =
        User.builder()
            .email(email(username))
            .username(username)
            .password(password)
            .roles(roles)
            .build()

    private val internet = Faker().internet()

    internal fun username() = internet.username()
    private fun email(username: String) = internet.emailAddress(username)
    fun safePassword(min: Int = 20, max: Int = 30): String = internet.password(min, max, true, true, true)
}