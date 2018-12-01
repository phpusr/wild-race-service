package com.phpusr.wildrace.domain

object Views {
    interface ProfileREST
    interface ConfigREST
    interface PostREST : ProfileREST, ConfigREST
}