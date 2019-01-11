package com.phpusr.wildrace.domain

object Views {
    interface Id
    interface ProfileREST
    interface ConfigREST
    interface PostDtoREST : ProfileREST, ConfigREST
    interface StatDtoREST : ProfileREST
}