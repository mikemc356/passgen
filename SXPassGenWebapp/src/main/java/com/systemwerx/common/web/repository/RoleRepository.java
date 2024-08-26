package com.systemwerx.common.web.repository;

import org.springframework.data.repository.CrudRepository;
import com.systemwerx.common.web.domain.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByRoleName(String roleName);
}
