package com.systemwerx.common.web.controller;

import java.util.List;

import com.systemwerx.common.web.domain.Response;
import com.systemwerx.common.web.domain.User;
import com.systemwerx.common.web.repository.UserRepository;
import com.systemwerx.common.web.service.impl.ApplicationServiceImpl;
import com.systemwerx.common.web.service.impl.UserService;
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
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/users")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public List<User> getUsers() {
        //return 
        List<User> users = userService.findUsers();
        for (User user : users) {
            user.setPassword(ApplicationServiceImpl.OBFUSCATED_KEY);
        }

        return users;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public Response saveUser(@RequestBody User user) {
        try {
            User returnedUser = userService.saveUser(user);
            return new Response(true, "Sucessfully saved", returnedUser);
        } catch (Exception ex) {
            return new Response(false, "Save failed", ex.getMessage());
        }
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public Response deleteUser(@PathVariable Long id) {
        try {
            boolean result = userService.deleteUserById(id);
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
