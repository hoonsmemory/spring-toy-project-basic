package io.hoon.springtoyprojectbasic.repository;

import io.hoon.springtoyprojectbasic.domain.entity.RoleHierarchy;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RoleHierarchyRepository extends JpaRepository<RoleHierarchy, Long> {

    RoleHierarchy findByChildName(String roleName);
}
