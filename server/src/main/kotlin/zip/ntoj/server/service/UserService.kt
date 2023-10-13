package zip.ntoj.server.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.User
import zip.ntoj.server.repository.UserRepository
import kotlin.jvm.optionals.getOrNull

interface UserService {
    fun isUsernameValid(username: String): Boolean {
        return username.length in 3..20 && username.all { it.isLetterOrDigit() }
    }

    fun get(
        page: Int = 1,
        pageSize: Int = Int.MAX_VALUE,
        desc: Boolean = false,
    ): List<User>
    fun newUser(user: User): User
    fun existsByUsername(username: String): Boolean
    fun existsById(id: Long): Boolean
    fun getUserByUsername(username: String): User
    fun getUserById(id: Long): User
    fun count(): Long
    fun updateUser(user: User): User
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
) : UserService {
    override fun get(page: Int, pageSize: Int, desc: Boolean): List<User> {
        return userRepository.findAll(
            PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(if (desc) Sort.Direction.DESC else Sort.Direction.ASC, "userId"),
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
}
