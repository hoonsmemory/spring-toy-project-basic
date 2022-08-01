package io.hoon.springtoyprojectbasic.service.security.impl;

import io.hoon.springtoyprojectbasic.domain.entity.Resource;
import io.hoon.springtoyprojectbasic.repository.DenyIpRepository;
import io.hoon.springtoyprojectbasic.repository.ResourceRepository;
import io.hoon.springtoyprojectbasic.service.security.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SecurityResourceServiceImpl implements SecurityResourceService {

    private final ResourceRepository resourceRepository;

    private final DenyIpRepository denyIpRepository;

    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList() {

        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        //DB로 부터 권한 정보를 가져온다.
        List<Resource> resourceList = resourceRepository.findAllResources();

        resourceList.forEach(resource -> {
            //DB에 가져온 ROLE 정보를 담는다.
            List<ConfigAttribute> configAttributeList = new ArrayList<>();
            resource.getRoleSet().forEach(role -> {
                configAttributeList.add(new SecurityConfig(role.getRoleName()));
                //1:다 형식으로 권한을 담는다.
                result.put(new AntPathRequestMatcher(resource.getResourceName()), configAttributeList);
            });
        });

        return result;
    }

    @Override
    public List<String> getDenyIpList() {
        List<String> accessIpList = denyIpRepository.findAll()
                .stream()
                .map(accessIp -> accessIp.getIpAddress())
                .collect(Collectors.toList());

        return accessIpList;
    }
}
