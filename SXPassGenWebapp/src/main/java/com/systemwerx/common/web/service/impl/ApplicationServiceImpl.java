package com.systemwerx.common.web.service.impl;

import com.systemwerx.PassGen.PassTicketPureBean;
import com.systemwerx.PassGen.SKeyBean;
import com.systemwerx.PassGen.SKeyException;
import com.systemwerx.common.web.domain.Application;
import com.systemwerx.common.web.domain.Role;
import com.systemwerx.common.web.domain.Setting;
import com.systemwerx.common.web.repository.ApplicationRepository;
import com.systemwerx.common.web.repository.RoleRepository;
import com.systemwerx.common.web.repository.SettingRepository;
import com.systemwerx.common.web.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

// Application service

@Component
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Application> findApplications() {
        return StreamSupport.stream(applicationRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public String getApplicationCredential(Long id, Long sequence)  {
        Optional<Application> appSaved = applicationRepository.findById(id);
        if (!appSaved.isPresent()) {
            // Should not have OBFUSCATED Key and no app in DB
            throw new IllegalArgumentException("Application not found");
        }

        if (appSaved.get().getType().equals(Application.PASSTICKET)) {
            Setting license = settingRepository.findByName(LICENSE);

            if (license == null) {
                throw new IllegalArgumentException("License not found");
            }

            try {
                PassTicketPureBean passticket = new PassTicketPureBean();
                passticket.setApplication(appSaved.get().getApplication());
                passticket.setUserID(appSaved.get().getUserName());
                passticket.setSessionKey(appSaved.get().getKey());
                passticket.setGmtOffset(appSaved.get().getGmtOffset().intValue());
                passticket.setLicense(license.getValue());
                return passticket.getPassTicket();
            } catch (Exception ex) {
                throw new IllegalArgumentException(ex.getMessage());
            }
        } else if (appSaved.get().getType().equals(Application.SKEY)) {
            Setting license = settingRepository.findByName(LICENSE);
            SKeyBean skeyBean = new SKeyBean();
            skeyBean.setApplication(appSaved.get().getApplication());
            skeyBean.setPassword(appSaved.get().getKey());
            try {
                skeyBean.setLicense(license.getValue());
            } catch (Exception e1) {
                throw new IllegalArgumentException("S/Key generation error");
            }

            skeyBean.setSeed(appSaved.get().getSeed());  
            try {
                skeyBean.setHashAlgorithm(skeyBean.MD5);
                String credential = skeyBean.generatePassword(sequence.intValue());
                return credential;
            } catch (SKeyException e) {
               throw new IllegalArgumentException("sKey generation error");
            }
        } else if (appSaved.get().getType().equals(Application.PASSWORD)) {
            return appSaved.get().getKey();
        } else {
            throw new IllegalArgumentException("Invalid Application found");
        }
    }

    @Override
    public Application saveApplication(Application app) {
        boolean add = false;
        if (app.getKey().equals(OBFUSCATED_KEY) && app.getId() != -1) {
            // Get key from DB if obfuscated value presented
            Optional<Application> appSaved = applicationRepository.findById(app.getId());
            if (!appSaved.isPresent()) {
                // Should not have OBFUSCATED Key and no app in DB
                throw new IllegalArgumentException("Application requires a key");
            }

            // Add value into app
            app.setKey(appSaved.get().getKey());
        } else {
            // We are adding a new app
            add = true;
        }

        // Save app and role if not present
        Application returnedApp = applicationRepository.save(app);
        if (add) {
            // Add role if it does not already exist
            Role returnedRole = roleRepository.findByRoleName(ApplicationService.USER_ROLE+"_"+app.getApplication());
            if (returnedRole == null) {
                // Create role
                Long id = new Long(-1);
                Role newRole = new Role();
                newRole.setId(id);
                newRole.setRoleName(ApplicationService.USER_ROLE+"_"+app.getApplication());
                newRole.setDescription("Application " + app.getApplication());

                Role savedRole = roleRepository.save(newRole);
                if (savedRole == null) {
                    throw new IllegalArgumentException("Role save failed");
                }
            }

        }
        return returnedApp;
    }

    @Override
    public Optional<Application> findApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    @Override
    public Optional<Application> findApplicationByName(String name) {
        return applicationRepository.findByName(name);
    }
    
    @Override
    public Optional<Application> findApplicationByIdObfuscated(Long id) {
        Optional<Application> app = applicationRepository.findById(id);
        app.get().setKey(OBFUSCATED_KEY);
        return app;
    }

    @Override
    public boolean deleteApplicationById(Long id) {
        try {
            Optional<Application> returnedApp = applicationRepository.findById(id);
            if (!returnedApp.isPresent()) {
                throw new IllegalArgumentException("Application delete failed - application not found");
            }
            applicationRepository.deleteById(id);
            // Delete application role
            Role returnedRole = roleRepository.findByRoleName(returnedApp.get().getApplication());
            if (returnedRole != null) {
                roleRepository.deleteById(returnedRole.getId());
            }

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
