package com.github.ntoj.app.server.service

import com.github.ntoj.app.server.exception.AppException
import com.github.ntoj.app.server.model.entities.Group
import com.github.ntoj.app.server.model.entities.User
import com.github.ntoj.app.server.repository.GroupRepository
import com.github.ntoj.app.server.repository.UserRepository
import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
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

    fun search(user: String): List<User>

    fun getUserGroups(user: User): List<Group>
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
    val groupRepository: GroupRepository,
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

    /**
     * search by username and displayName and email like with spec
     */
    override fun search(user: String): List<User> {
        val spec =
            Specification<User> { root, _, cb ->
                val list = mutableListOf<Predicate>()
                if (user.isNotBlank()) {
                    list.add(
                        cb.or(
                            cb.like(root.get("username"), "%$user%"),
                            cb.like(root.get("displayName"), "%$user%"),
                            cb.like(root.get("email"), "%$user%"),
                        ),
                    )
                }
                cb.and(*list.toTypedArray())
            }
        return userRepository.findAll(spec)
    }

    override fun getUserGroups(user: User): List<Group> {
        return groupRepository.findAllByUsersContains(user)
    }
}
