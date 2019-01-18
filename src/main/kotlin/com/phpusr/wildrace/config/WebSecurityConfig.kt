package com.phpusr.wildrace.config

import com.phpusr.wildrace.service.UserService
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
        private val userService: UserService,
        private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers("/post/sync")
                .hasRole("ADMIN")
            .antMatchers(HttpMethod.GET, "/wild-race-ws/**", "/", "/post", "/post/*")
                .permitAll()
            .anyRequest()
                .hasRole("ADMIN")

        http.formLogin()
                .loginPage("/login").permitAll()
                .successHandler(FormLoginSuccessHandler())
                .failureHandler { _, response, exception ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Fail login: ${exception.message}")
                }
                .and()
            .csrf().disable()
            .logout().permitAll()

        http.exceptionHandling().authenticationEntryPoint { _, response, authException ->
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: ${authException.message}")
        }
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        //TODO auth.userDetailsService(userService).passwordEncoder(passwordEncoder)
        val passwordEncoder = object : PasswordEncoder {
            override fun encode(rawPassword: CharSequence?) = rawPassword.toString()
            override fun matches(rawPassword: CharSequence?, encodedPassword: String?) = rawPassword.toString() == encodedPassword
        }
        auth.inMemoryAuthentication().passwordEncoder(passwordEncoder)
                .withUser("user").password("1").roles("USER").and()
                .withUser("admin").password("1").roles("ADMIN");
    }
}