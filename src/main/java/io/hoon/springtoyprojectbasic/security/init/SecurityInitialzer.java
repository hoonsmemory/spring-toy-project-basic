package io.hoon.springtoyprojectbasic.security.init;

import io.hoon.springtoyprojectbasic.service.security.RoleHierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Component;

/**
 * DB로 부터 계층권한 정보를 가져와 포맷팅된 결과값을 RoleHierarchy 에 넣어준다.
 */
@RequiredArgsConstructor
@Component
public class SecurityInitialzer implements ApplicationRunner {

    private final RoleHierarchyService roleHierarchyService;
    private final RoleHierarchyImpl roleHierarchy;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String allHierarchy = roleHierarchyService.findAllHierarchy();
        roleHierarchy.setHierarchy(allHierarchy);
    }
}
