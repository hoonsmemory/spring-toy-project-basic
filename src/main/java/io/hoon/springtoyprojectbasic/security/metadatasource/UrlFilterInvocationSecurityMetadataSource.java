package io.hoon.springtoyprojectbasic.security.metadatasource;

import io.hoon.springtoyprojectbasic.service.security.impl.SecurityResourceServiceImpl;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 각 URL의 권한 정보를 추출한다.
 */
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    //RequestMatcher : Request URL, ConfigAttribute : 권한 정보를 담고있는 타입
    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> requestMap;

    private SecurityResourceServiceImpl securityResourceService;

    public UrlFilterInvocationSecurityMetadataSource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceMap, SecurityResourceServiceImpl securityResourceServiceImpl) {
        this.requestMap = resourceMap;
        this.securityResourceService = securityResourceServiceImpl;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        //현재 사용자가 요청하는 객체를 얻을 수 있다. (요청 URL 등)
        HttpServletRequest request = ((FilterInvocation) object).getRequest();

        //requestMap.put(new AntPathRequestMatcher("/mypage"), Arrays.asList(new SecurityConfig("ROLE_USER")));

        //권한 추출 로직
        if(requestMap != null) {
            for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : requestMap.entrySet()) {
                RequestMatcher matcher = entry.getKey();
                //사용자가 요청한 정보와 DB에 저장된 정보와 비교
                if(matcher.matches(request)) {
                    //권한 정보 리턴
                    return entry.getValue();
                }
            }

        }
        //값이 null일 경우 SecurityInterceptor에서 인가처리 하는 부분을 패스한다.
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        Set<ConfigAttribute> allAttributes = new HashSet<>();
        this.requestMap.values().forEach(allAttributes::addAll);
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        //타입 검사
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    //관리자가 권한에 대한 정보를 변경했을 때 requestMap의 내용을 다시 저장한다.
    public void reload() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = securityResourceService.getResourceList();
        Iterator<Map.Entry<RequestMatcher, List<ConfigAttribute>>> iterator = reloadedMap.entrySet().iterator();

        requestMap.clear();

        while (iterator.hasNext()) {
            Map.Entry<RequestMatcher, List<ConfigAttribute>> entry = iterator.next();
            requestMap.put(entry.getKey(), entry.getValue());
        }
    }
}
