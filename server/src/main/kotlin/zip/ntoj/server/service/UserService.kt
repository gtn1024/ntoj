package zip.ntoj.server.service

import org.springframework.stereotype.Service
import zip.ntoj.server.exception.AppException
import zip.ntoj.server.model.User
import zip.ntoj.server.repository.UserRepository

interface UserService {
    fun newUser(user: User): User
    fun existsByUsername(username: String): Boolean
    fun getUserByUsername(username: String): User
    fun getUserById(loginIdAsLong: Long): User
    fun count(): Long
    fun updateUser(user: User): User
}

@Service
class UserServiceImpl(
    val userRepository: UserRepository,
) : UserService {
    override fun newUser(user: User) = userRepository.save(user)
    override fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    override fun getUserByUsername(username: String): User {
        return userRepository.findByUsername(username).orElseThrow { AppException("用户不存在", 404) }
    }

    override fun getUserById(loginIdAsLong: Long): User {
        return userRepository.findById(loginIdAsLong).orElseThrow { AppException("用户不存在", 404) }
    }

    override fun count(): Long {
        return userRepository.count()
    }

    override fun updateUser(user: User): User {
        return userRepository.save(user)
    }
}
