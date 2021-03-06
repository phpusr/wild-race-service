package com.phpusr.wildrace.domain

import org.springframework.security.core.GrantedAuthority

enum class Role : GrantedAuthority {
    ROLE_ADMIN;

    override fun getAuthority() = name
}