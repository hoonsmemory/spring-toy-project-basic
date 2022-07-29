package io.hoon.springtoyprojectbasic.repository;

import io.hoon.springtoyprojectbasic.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByRoleName(String name);

    @Override
    void delete(Role role);

}
