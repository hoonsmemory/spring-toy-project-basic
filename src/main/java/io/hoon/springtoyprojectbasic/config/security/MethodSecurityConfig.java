package io.hoon.springtoyprojectbasic.config.security;

import io.hoon.springtoyprojectbasic.security.factory.UrlResourceMapFactoryBean;
import io.hoon.springtoyprojectbasic.security.filter.PermitAllFilter;
import io.hoon.springtoyprojectbasic.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.hoon.springtoyprojectbasic.security.vote.IpAccessVoter;
import io.hoon.springtoyprojectbasic.service.security.impl.SecurityResourceServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Slf4j
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private String[] permitAllPattern = {"/", "/home", "/users", "/login"};

    private final SecurityResourceServiceImpl securityResourceService;

    @Bean
    public PermitAllFilter permitAllFilter() throws Exception {
        /*
           인가처리를 할 수 있도록 filter interceptor 추가
         */
        PermitAllFilter permitAllFilter = new PermitAllFilter(permitAllPattern);
        //URL 권한 정보를 추출하는 SecurityMetadataSource 적용
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource());
        //접근 결정 관리자 : 3가지 타입이 있다. 주로 기본 전략인 하나만 승인되도 허용되는 AffirmativeBased를 사용
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

    //인가 처리를 수행
    @Bean
    public AccessDecisionManager affirmativeBased() {
        AffirmativeBased accessDecisionManager = new AffirmativeBased(getAccessDecisionVoters());
        return accessDecisionManager;
    }

    //RolVoter : SecurityConfig("ROLE_ADMIN") 와 같은 값을 가지고 인가처리를 하는 클래스 (상위, 하위 개념이 없다.)
    //RoleHierarchyVoter : 계층형으로 권한이 높으면 그 이하의 권한을 가진 페이지의 접근이 가능하다.
    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        List<AccessDecisionVoter<? extends Object>> accessDecisionVoters = new ArrayList<>();
        //먼저 심사가 필요할 경우 add하는 위치를 위로 한다.
        accessDecisionVoters.add(new IpAccessVoter(securityResourceService));
        accessDecisionVoters.add(roleHierarchyVoter());

        return accessDecisionVoters;
    }

    @Bean
    public AccessDecisionVoter<? extends Object> roleHierarchyVoter() {

        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
        return roleHierarchyVoter;
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        return roleHierarchy;
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
