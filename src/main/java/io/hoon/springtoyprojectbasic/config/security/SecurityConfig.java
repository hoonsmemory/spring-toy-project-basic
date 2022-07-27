package io.hoon.springtoyprojectbasic.config.security;

import io.hoon.springtoyprojectbasic.service.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //각 페이지 권한 설정
        http.authorizeRequests()
            .antMatchers("/", "/users", "user/login/**").permitAll()
            .antMatchers("/mypage").hasRole("USER")
            .antMatchers("/messages").hasRole("MANAGER")
            .antMatchers("/config").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and()
            .formLogin();

        //유저 인증 처리
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return http.build();
    }

    /*
        DB에 직접 데이터를 넣기 때문에 InMemory 방식의 계정 생성을 하지 않는다.
     */
    //@Bean
    InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        String password = passwordEncoder().encode("1111");

        List<UserDetails> userDetailsList = new ArrayList<>();
        userDetailsList.add(User.withUsername("user")
                .password(password)
                .roles("USER").build());

        userDetailsList.add(User.withUsername("manager")
                .password(password)
                .roles("USER", "MANAGER").build());

        userDetailsList.add(User.withUsername("admin")
                .password(password)
                .roles("USER", "MANAGER", "ADMIN").build());

        return new InMemoryUserDetailsManager(userDetailsList);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //Web Ignore 설정(정적 파일들은 보안을 거치지 않는다.)
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
    }

}
