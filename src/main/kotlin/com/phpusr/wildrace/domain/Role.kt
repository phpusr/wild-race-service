package com.phpusr.wildrace.domain

import org.springframework.security.core.GrantedAuthority

enum class Role(val id: Long) : GrantedAuthority {
    ADMIN(1);

    override fun getAuthority() = name
}