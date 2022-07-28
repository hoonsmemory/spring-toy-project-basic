package io.hoon.springtoyprojectbasic.config.security;

import io.hoon.springtoyprojectbasic.security.common.FormAuthenticationDetailsSource;
import io.hoon.springtoyprojectbasic.security.handler.CustomAccessDeniedHandler;
import io.hoon.springtoyprojectbasic.security.handler.CustomAuthenticationFailureHandler;
import io.hoon.springtoyprojectbasic.security.handler.CustomAuthenticationSuccessHandler;
import io.hoon.springtoyprojectbasic.security.provider.CustomAuthenticationProvider;
import io.hoon.springtoyprojectbasic.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final FormAuthenticationDetailsSource formAuthenticationDetailsSource;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //각 페이지 권한 설정
        http
                .authorizeRequests()
                .antMatchers("/", "/users", "user/login/**", "/login*").permitAll()
                .antMatchers("/mypage").hasRole("USER")
                .antMatchers("/messages").hasRole("MANAGER")
                .antMatchers("/config").hasRole("ADMIN")
                .anyRequest().authenticated()
            .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .authenticationDetailsSource(formAuthenticationDetailsSource)
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(customAuthenticationFailureHandler)
                .permitAll()
            .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
        ;

        //유저 인증 처리
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authenticationProvider());

        return http.build();
    }

    private AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler customAccessDeniedHandler = new CustomAccessDeniedHandler();
        customAccessDeniedHandler.setErrorPage("/denied");
        return customAccessDeniedHandler;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(customUserDetailsService, passwordEncoder());
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
