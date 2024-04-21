package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.entities.User
import com.github.ntoj.app.server.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

interface UserService {
    fun isUsernameValid(username: String): Boolean {
        return username.length in 3..20 && username.all { it.isLetterOrDigit() }
    }

    fun get(
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
    ): List<User>

    fun newUser(user: User): User

    fun existsByUsername(username: String): Boolean

    fun existsById(id: Long): Boolean

    fun getUserByUsername(username: String): User

    fun getUserById(id: Long): User

    fun count(): Long

    fun updateUser(user: User): User

    fun deleteUserById(id: Long)
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
) : UserService {
    override fun get(
        page: Int,
        pageSize: Int,
    ): List<User> {
        return userRepository.findAll(
            PageRequest.of(
                page - 1,
                pageSize,
            ),
        ).toList()
    }

    override fun newUser(user: User): User {
        if (!isUsernameValid(user.username)) {
            throw AppException("用户名不合法", 400)
        }
        // 判断用户是否存在
        if (existsByUsername(user.username)) {
            throw AppException("用户已存在", 400)
        }
        return userRepository.save(user)
    }

    override fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    override fun existsById(id: Long): Boolean {
        return userRepository.existsById(id)
    }

    override fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username).orElseThrow { AppException("用户不存在", 404) }
    }

    override fun getUserById(id: Long): User {
        return userRepository.findById(id).orElseThrow { AppException("用户不存在", 404) }
    }

    override fun count(): Long {
        return userRepository.count()
    }

    override fun updateUser(user: User): User {
        val userInDb = userRepository.findByUsername(user.username).getOrNull()
        if (userInDb != null && userInDb.userId != user.userId) {
            throw AppException("用户名已存在", 400)
        }
        return userRepository.save(user)
    }

    override fun deleteUserById(id: Long) {
        userRepository.deleteById(id)
    }
}
