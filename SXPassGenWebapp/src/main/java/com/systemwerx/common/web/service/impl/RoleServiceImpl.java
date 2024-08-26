package com.systemwerx.common.web.service.impl;

import com.systemwerx.common.web.domain.Role;
import com.systemwerx.common.web.repository.RoleRepository;
import com.systemwerx.common.web.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@Component
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> findRoles() {
        return StreamSupport.stream(roleRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Role saveRole(Role role) {

        return roleRepository.save(role);
    }

    @Override
    public Optional<Role> findRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public boolean deleteRoleById(Long id) {
        try {
            roleRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
