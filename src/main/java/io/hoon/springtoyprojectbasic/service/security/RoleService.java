package io.hoon.springtoyprojectbasic.service.security;

import io.hoon.springtoyprojectbasic.domain.entity.Role;

import java.util.List;

public interface RoleService {

    Role getRole(long id);

    List<Role> getRoles();

    void createRole(Role role);

    void deleteRole(long id);
}
