package io.hoon.springtoyprojectbasic.config.security;

import io.hoon.springtoyprojectbasic.security.common.FormAuthenticationDetailsSource;
import io.hoon.springtoyprojectbasic.security.filter.PermitAllFilter;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final FormAuthenticationDetailsSource formAuthenticationDetailsSource;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final PermitAllFilter permitAllFilter;

    //Web Ignore 설정(정적 파일들은 보안을 거치지 않는다.)
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                //설정 시 구체적인 경로가 먼저 오고 그것 보다 큰 범위의 경로가 뒤에 오도록 해야한다.
                .authorizeRequests()
                .anyRequest().authenticated()//인증된 사용자가 아니면 어떠한 자원에도 접근이 되지 않는다는 의미의 권한으로 정의
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
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .accessDeniedPage("/denied")
                .accessDeniedHandler(accessDeniedHandler())
            .and()
                //기존에 만들어진 FilterSecurityInterceptor 앞에 위치해서 내가 만든 필터를 먼저 거칠 수 있도록 한다.
                //사실상 기존에 제공되던 것은 무용지물된다.
                //.addFilterBefore(permitAllFilter, FilterSecurityInterceptor.class)
                .addFilterAt(permitAllFilter, FilterSecurityInterceptor.class)
        ;
        http.csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(customUserDetailsService, passwordEncoder());
    }

    private AccessDeniedHandler accessDeniedHandler() {
        CustomAccessDeniedHandler customAccessDeniedHandler = new CustomAccessDeniedHandler();
        customAccessDeniedHandler.setErrorPage("/denied");
        return customAccessDeniedHandler;
    }




}
