package io.hoon.springtoyprojectbasic.security.vote;

import io.hoon.springtoyprojectbasic.service.security.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class IpAccessVoter implements AccessDecisionVoter<Object> {

    private List<String> accessIpList;
    private final SecurityResourceService securityResourceService;

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    //authentication : 인증정보, object : 요청정보, attributes : 권한정보
    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {

        //IP 가져오기
        WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();
        String remoteAddress = details.getRemoteAddress();

        if(accessIpList == null) {
            accessIpList = securityResourceService.getAccessIpList();
        }

        for (String accessIp : accessIpList) {
            if(remoteAddress.equals(accessIp)) {
                //다음 filter에서 심사를 해야하기 때문에 ACCESS_ABSTAIN 으로 결과를 줘야한다.
                return ACCESS_ABSTAIN;
            }
        }

        throw new AccessDeniedException("Invalid IpAddress");

    }
}
