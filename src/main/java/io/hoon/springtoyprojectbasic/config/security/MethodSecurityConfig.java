package io.hoon.springtoyprojectbasic.config.security;

import io.hoon.springtoyprojectbasic.security.factory.UrlResourceMapFactoryBean;
import io.hoon.springtoyprojectbasic.security.filter.PermitAllFilter;
import io.hoon.springtoyprojectbasic.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.hoon.springtoyprojectbasic.service.security.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Slf4j
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private String[] permitAllPattern = {"/", "/home", "/users", "/login"};

    private final SecurityResourceService securityResourceService;

    @Bean
    public PermitAllFilter permitAllFilter() throws Exception {
        /*
           인가처리를 할 수 있도록 filter interceptor 추가
         */
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllPattern);
        //URL 권한 정보를 추출하는 SecurityMetadataSource 적용
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        //접근 결정 관리자 : 3가지 타입이 있다.주로 기본 전략인 하나만 승인되도 허용되는 AffirmativeBased를 사용
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        //인증 관리자 : 권한 필터 같은 경우 인가하기 전 인증된 사용자가 있는지, 인증된 사용자인지 검사가 필요
        //permitAllFilter.setAuthenticationManager(authenticationManagerBean());

        return permitAllFilter;
    }

    @Bean
    public UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlresourcemapfactorybean().getObject(), securityResourceService);
    }

    @Bean
    public UrlResourceMapFactoryBean urlresourcemapfactorybean(){
        return new UrlResourceMapFactoryBean(securityResourceService);
    }

    @Bean
    public AccessDecisionManager affirmativeBased() {
        AffirmativeBased accessDecisionManager = new AffirmativeBased(getAccessDecisionVoters());
        return accessDecisionManager;
    }

    //RolVoter : SecurityConfig("ROLE_ADMIN") 와 같은 값을 가지고 인가처리를 하는 클래스
    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        return Arrays.asList(new RoleVoter());
    }

    //필터를 직접 만들어 정적 파일을 무시하는게 패스되었다.
    //따라서 해당 빈을 등록하여 정적 파일을 무시할 수 있도록 함
    @Bean
    public FilterRegistrationBean filterRegistrationBean() throws Exception {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(permitAllFilter());
        filterRegistrationBean.setEnabled(false);
        return filterRegistrationBean;
    }
}
