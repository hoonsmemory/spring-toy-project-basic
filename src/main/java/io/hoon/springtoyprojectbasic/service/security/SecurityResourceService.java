package io.hoon.springtoyprojectbasic.service.security;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;
import java.util.List;

public interface SecurityResourceService {

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList();

    List<String> getAccessIpList();
}
