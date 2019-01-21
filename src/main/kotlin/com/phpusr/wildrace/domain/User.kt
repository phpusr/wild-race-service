package com.phpusr.wildrace.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity(name = "user_table")
data class User(
    @field:Id
    val id: Long?,

    private var username: String,

    private var password: String,

    private var enabled: Boolean,

    private var passwordExpired :Boolean,

    private var accountExpired: Boolean,

    private var accountLocked: Boolean,

    @field:ElementCollection(targetClass = Role::class, fetch = FetchType.EAGER)
    @field:CollectionTable(name = "user_role", joinColumns = [JoinColumn(name = "user_id")])
    @field:Enumerated(EnumType.ORDINAL)
    @field:Column(name = "role_id")
    val roles: MutableSet<Role> = mutableSetOf()
): UserDetails {

    override fun getUsername() = username
    fun setUsername(username: String) {
        this.username = username
    }

    override fun getPassword() = password
    fun setPassword(password: String) {
        this.password = password
    }

    override fun getAuthorities() = roles

    override fun isEnabled() = enabled

    override fun isCredentialsNonExpired() = !passwordExpired

    override fun isAccountNonExpired() = !accountExpired

    override fun isAccountNonLocked() = !accountLocked
}

interface UserRepo : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}