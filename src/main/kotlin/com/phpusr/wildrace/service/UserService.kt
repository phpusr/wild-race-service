package com.phpusr.wildrace.service

import com.phpusr.wildrace.domain.User
import com.phpusr.wildrace.domain.UserRepo
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
        private val userRepo: UserRepo
) : UserDetailsService {

    override fun loadUserByUsername(username: String): User {
        return userRepo.findByUsername(username) ?: throw UsernameNotFoundException("user_not_found")
    }

}