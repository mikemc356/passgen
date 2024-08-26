package com.systemwerx.common.web.controller;

import java.util.List;

import com.systemwerx.common.web.domain.Role;
import com.systemwerx.common.web.domain.Response;
import com.systemwerx.common.web.repository.RoleRepository;
import com.systemwerx.common.web.service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    RoleRepository roleRepository;

    @RequestMapping(value = "/roles")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public List<Role> getApplications() {
        List<Role> roles = roleService.findRoles();
        return roles;
    }

    @RequestMapping(value = "/role", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public Response saveApplication(@RequestBody Role role) {
        try {
            Role returnedRole = roleService.saveRole(role);
            return new Response(true, "Sucessfully saved", returnedRole);
        } catch (Exception ex) {
            return new Response(false, "Save failed", null);
        }
    }

    @RequestMapping(value = "/role/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public Response deleteApplication(@PathVariable Long id) {
        try {
            boolean result = roleService.deleteRoleById(id);
            if (result) {
                return new Response(true, "Delete successful", null);
            } else {
                return new Response(false, "Delete failed", null);
            }
        } catch (Exception ex) {
            return new Response(false, "Delete failed", null);
        }
    }
}
