package io.hoon.springtoyprojectbasic.security.factory;

import io.hoon.springtoyprojectbasic.service.security.impl.SecurityResourceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;
import java.util.List;

//DB로부터 얻은 권한, 자원 정보를 UrlFilterInvocationSecurityMetadataSource에 제공
@RequiredArgsConstructor
public class UrlResourceMapFactoryBean implements FactoryBean<LinkedHashMap<RequestMatcher, List<ConfigAttribute>>> {

    //DB로부터 가져온 데이터를 맵핑하는 작업을 하는 서비스
    private final SecurityResourceServiceImpl securityResourceService;

    //권한, 자원 정보를 담을 Map
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceMap;

    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getObject() throws Exception {

        if(resourceMap == null) {
            init();
        }

        return resourceMap;
    }

    private void init() {
        resourceMap = securityResourceService.getResourceList();
    }

    @Override
    public Class<?> getObjectType() {
        return LinkedHashMap.class;
    }

    @Override
    public boolean isSingleton() {
        //true
        return FactoryBean.super.isSingleton();
    }
}
