package com.systemwerx.common.web.controller;

import java.util.List;

import com.systemwerx.common.web.domain.Notification;
import com.systemwerx.common.web.domain.User;
import com.systemwerx.common.web.repository.UserRepository;
import com.systemwerx.common.web.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/notification") 
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    UserRepository userRepository;
    
    @RequestMapping(value = "/getForUser")
    @PreAuthorize("hasAnyAuthority('STANDARD_USER','ADMIN_USER')")
    public List<Notification> getNotifications() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User user = userRepository.findByUsername(username);
        return notificationService.findByUser(user);
    }
}
