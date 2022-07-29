package io.hoon.springtoyprojectbasic.service.security.impl;

import io.hoon.springtoyprojectbasic.domain.entity.RoleHierarchy;
import io.hoon.springtoyprojectbasic.repository.RoleHierarchyRepository;
import io.hoon.springtoyprojectbasic.service.security.RoleHierarchyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

    @Autowired
    private RoleHierarchyRepository roleHierarchyRepository;

    @Transactional
    @Override
    public String findAllHierarchy() {

        List<RoleHierarchy> rolesHierarchy = roleHierarchyRepository.findAll();

        Iterator<RoleHierarchy> itr = rolesHierarchy.iterator();
        StringBuffer roleHierarchy = new StringBuffer();

        /**
         * 1순위 > 2순위
         * 2순위 > 3순위
         * 3순위 > 4순위 ... 방식으로 나올 수 있도록 구현
         */
        while (itr.hasNext()) {
            RoleHierarchy model = itr.next();
            if (model.getParentName() != null) {
                roleHierarchy.append(model.getParentName().getChildName());
                roleHierarchy.append(" > ");
                roleHierarchy.append(model.getChildName());
                roleHierarchy.append("\n");
            }
        }

        log.info("권한 계층 : {}", roleHierarchy);
        return roleHierarchy.toString();
    }
}
