package org.effective_mobile.task_management_system

import net.datafaker.Faker
import org.effective_mobile.task_management_system.database.entity.Role
import org.effective_mobile.task_management_system.database.entity.User
import org.effective_mobile.task_management_system.utils.constraints.length.user.password

object RandomUsers {

    fun user(passwordEncoding: () -> String) = user(password = passwordEncoding())

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

    private fun username() = internet.username()
    private fun email(username: String) = internet.emailAddress(username)

    fun safePassword(min: Int = password.MIN, max: Int = 50): String =
        internet.password(min, max, true, true, true)
}