package com.systemwerx.common.web.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.systemwerx.common.web.domain.Application;
import com.systemwerx.common.web.domain.Response;
import com.systemwerx.common.web.repository.UserRepository;
import com.systemwerx.common.web.service.ApplicationService;
import com.systemwerx.common.web.service.impl.ApplicationServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/applications")
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public List<Application> getApplications() {
        List<Application> apps = applicationService.findApplications();
        // Filter out keys - we do not need to send across network
        for (Application app : apps) {
            app.setKey(ApplicationServiceImpl.OBFUSCATED_KEY);
        }
        return apps;
    }

    @RequestMapping(value = { "/credential/{app}/{sequence}", "/credential/{app}" })
    @PreAuthorize("hasAnyAuthority('STANDARD_USER','ADMIN_USER')")
    public Response getApplicationCredential(Principal principal, @PathVariable String app,
            @PathVariable(required = false) Long sequence) {
        OAuth2Authentication auth = (OAuth2Authentication) principal;
        try {

            //Optional<Application> application = applicationService.findApplicationById(id);
            Optional<Application> application = applicationService.findApplicationByName(app);

            if (!application.isPresent()) {
                return new Response(true, "Credential generate failed", null);
            }

            boolean accessGranted = false;
            String userAppRole = ApplicationService.USER_ROLE + "_" + application.get().getApplication();
            for (GrantedAuthority authority : auth.getAuthorities()) {
                if (authority.getAuthority().equals(ApplicationService.ADMIN_ROLE)) {
                    accessGranted = true;
                    break;
                }

                if (authority.getAuthority().equals(userAppRole)) {
                    accessGranted = true;
                    break;
                }
            }

            if (!accessGranted) {
                return new Response(false, "Access denied", null);
            }

            String credential = applicationService.getApplicationCredential(application.get().getId(), sequence);
            return new Response(true, "Credential generated",
                    "{ \"credential\":\"" + credential + "\", \"type\":\"" + application.get().getType() + "\"}");
        } catch (Exception e) {
            return new Response(false, e.getMessage(),null);
        }
    }

    @RequestMapping(value = "/application", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public Response saveApplication(@RequestBody Application app) {
        try {
            Application returnedApp = applicationService.saveApplication(app);
            return new Response(true, "Sucessfully saved", returnedApp);
        } catch (Exception ex) {
            return new Response(false, "Save failed", null);
        }
    }

    @RequestMapping(value = "/application/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public Response deleteApplication(@PathVariable Long id) {
        try {
            boolean result = applicationService.deleteApplicationById(id);
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
