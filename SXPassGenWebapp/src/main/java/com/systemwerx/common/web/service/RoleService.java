package com.systemwerx.common.web.service;

import java.util.List;
import java.util.Optional;
import com.systemwerx.common.web.domain.Role;

/**
 * 
 */
public interface RoleService {
    List<Role> findRoles();
    Optional<Role> findRoleById(Long id);
    Role saveRole(Role role);
    boolean deleteRoleById(Long id);
}
