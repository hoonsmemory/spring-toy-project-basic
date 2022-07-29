package io.hoon.springtoyprojectbasic.config.security;

import io.hoon.springtoyprojectbasic.security.common.FormAuthenticationDetailsSource;
import io.hoon.springtoyprojectbasic.security.factory.UrlResourceMapFactoryBean;
import io.hoon.springtoyprojectbasic.security.filter.PermitAllFilter;
import io.hoon.springtoyprojectbasic.security.handler.CustomAccessDeniedHandler;
import io.hoon.springtoyprojectbasic.security.handler.CustomAuthenticationFailureHandler;
import io.hoon.springtoyprojectbasic.security.handler.CustomAuthenticationSuccessHandler;
import io.hoon.springtoyprojectbasic.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.hoon.springtoyprojectbasic.security.provider.CustomAuthenticationProvider;
import io.hoon.springtoyprojectbasic.security.service.CustomUserDetailsService;
import io.hoon.springtoyprojectbasic.service.security.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final FormAuthenticationDetailsSource formAuthenticationDetailsSource;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final SecurityResourceService securityResourceService;
    private String[] permitAllResourceService = {"/", "/login", "/user/login/**"};

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
                .accessDeniedPage("/denied")
                .accessDeniedHandler(accessDeniedHandler())
            .and()
                //기존에 만들어진 FilterSecurityInterceptor 앞에 위치해서 내가 만든 필터를 먼저 거칠 수 있도록 한다.
                //사실상 기존에 제공되던 것은 무용지물된다.
                .addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
        ;
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //Web Ignore 설정(정적 파일들은 보안을 거치지 않는다.)
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    //필터를 직접 만들어 정적 파일을 무시하는게 패스되었다.
    //따라서 해당 빈을 등록하여 정적 파일을 무시할 수 있도록 함
    @Bean
    public FilterRegistrationBean filterRegistrationBean() throws Exception {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(customFilterSecurityInterceptor());
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }

    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {

        /*
           인가처리를 할 수 있도록 filter interceptor 추가
         */
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllResourceService);
        //URL 권한 정보를 추출하는 SecurityMetadataSource 적용
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        //접근 결정 관리자 : 3가지 타입이 있다.주로 기본 전략인 하나만 승인되도 허용되는 AffirmativeBased를 사용
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        //인증 관리자 : 권한 필터 같은 경우 인가하기 전 인증된 사용자가 있는지, 인증된 사용자인지 검사가 필요
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());

        return permitAllFilter;
    }

    private AccessDecisionManager affirmativeBased() {
        AffirmativeBased affirmativeBased = new AffirmativeBased(getAccessDecistionVoters());
        return affirmativeBased;
    }

    //RolVoter : SecurityConfig("ROLE_ADMIN") 와 같은 값을 가지고 인가처리를 하는 클래스
    private List<AccessDecisionVoter<?>> getAccessDecistionVoters() {
        return Arrays.asList(new RoleVoter());
    }

    @Bean
    public FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(UrlResourceMapFactoryBean().getObject(), securityResourceService);
    }

    private UrlResourceMapFactoryBean UrlResourceMapFactoryBean() {
        return new UrlResourceMapFactoryBean(securityResourceService);
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
