package com.systemwerx.common.web.service;

import java.util.List;
import java.util.Optional;
import com.systemwerx.common.web.domain.Application;

/**
 * 
 */
public interface ApplicationService {

    public final static String OBFUSCATED_KEY = "----------------";
    public final static String LICENSE = "license";
    public static String ADMIN_ROLE = "ADMIN_USER";
    public static String USER_ROLE = "STANDARD_USER";

    List<Application> findApplications();
    Optional<Application> findApplicationById(Long id);
    Optional<Application> findApplicationByName(String name);
    Optional<Application> findApplicationByIdObfuscated(Long id);
    Application saveApplication(Application app);
    boolean deleteApplicationById(Long id);
    String getApplicationCredential(Long id, Long sequence);
}
