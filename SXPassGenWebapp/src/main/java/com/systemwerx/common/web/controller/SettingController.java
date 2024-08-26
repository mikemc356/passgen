package com.systemwerx.common.web.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.systemwerx.common.license.license;
import com.systemwerx.common.web.domain.Response;
import com.systemwerx.common.web.domain.Setting;
import com.systemwerx.common.web.repository.UserRepository;
import com.systemwerx.common.web.service.ConfigurationService;
import com.systemwerx.common.web.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/setting")
public class SettingController {

    private static String CA_FILE = "truststore.jks";
    private static String SERVER_FILE = "server.jks";
    private static String LICENSE = "license";
    private static String LICENSE_MESSAGE = "licenseMessage";
    private static String PRODUCT_ID = "05";

    @Autowired
    private SettingService settingService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/settings", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public Response getSettings() {
        List<Setting> settings = settingService.findSettings();
        license license = new license(PRODUCT_ID);

        // Get license info if its present
        Setting licenseMessage = new Setting();
        licenseMessage.setName(LICENSE_MESSAGE);
        settings.add(licenseMessage);
        
        for (Setting setting : settings) {
            if (setting.getName() != null && setting.getName().equals(LICENSE)) {

                try {
                    if (setting.getValue() != null && !setting.getValue().equals("")) {
                        if (license.verifyLicense(setting.getValue().substring(0, 5),
                                setting.getValue().substring(5, 9), setting.getValue().substring(9))) {
                            licenseMessage.setValue("License is valid - expires "
                                    + license.getExpireDateString(setting.getValue().substring(0, 5)));
                        } else {
                            licenseMessage.setValue("License is invalid");
                        }
                    } else {
                        licenseMessage.setValue("License is invalid");
                    }
                } catch (Exception ex) {
                    licenseMessage.setValue("License is invalid");
                }

            }
        }

        // Get file info
        File caFile = new File(CA_FILE);
        if (!caFile.exists()) {
            Setting setting = new Setting();
            setting.setName("caCertFileMessage");
            setting.setValue("No trustore installed");
            settings.add(setting);
        } else {
            Setting setting = new Setting();
            setting.setName("caCertFileMessage");
            setting.setValue("Trustore installed - last updated " + new Date(caFile.lastModified()));
            settings.add(setting);
        }

        File serverCertFile = new File(SERVER_FILE);
        if (!serverCertFile.exists()) {
            Setting setting = new Setting();
            setting.setName("serverCertFileMessage");
            setting.setValue("No Server keystore installed");
            settings.add(setting);
        } else {
            Setting setting = new Setting();
            setting.setName("serverCertFileMessage");
            setting.setValue("Keystore installed - last updated " + new Date(serverCertFile.lastModified()));
            settings.add(setting);
        }

        return new Response(true, "Settings successfuly retrieved", settings);
    }

    @RequestMapping(value = "/settings", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public Response saveSettings(@RequestPart(value = "caFile", required = false) MultipartFile caFile,
            @RequestPart(value = "serverCertFile", required = false) MultipartFile serverCertFile,
            @RequestPart(value = "caFilePassword", required = false) String caFilePassword,
            @RequestPart(value = "serverCertFilePassword", required = false) String serverCertFilePassword,
            @RequestPart(value = "license", required = false) String license,
            @RequestPart(value = "port", required = false) String port,
            @RequestPart(value = "tls", required = false) String tls) {
        try {
            String cwd = System.getProperty("user.dir");
            boolean result = true;
            List<Setting> setings = new ArrayList<Setting>();
            if (license != null) {
                Setting setting = new Setting();
                setting.setName(LICENSE);
                setting.setValue(license);
                if (settingService.saveSetting(setting) == null) {
                    result = false;
                }
            }

            if (port != null) {
                Setting setting = new Setting();
                setting.setName("port");
                setting.setValue(port);
                if (settingService.saveSetting(setting) == null) {
                    result = false;
                }
                configurationService.updateProperty("server.port", port);
            }

            if (tls != null) {
                Setting setting = new Setting();
                setting.setName("tls");
                setting.setValue(tls);
                if (settingService.saveSetting(setting) == null) {
                    result = false;
                }
                configurationService.updateProperty("server.ssl.enabled", tls);
            }

            if (serverCertFilePassword != null) {
                Setting setting = new Setting();
                setting.setName("serverCertFilePassword");
                setting.setValue(serverCertFilePassword);
                if (settingService.saveSetting(setting) == null) {
                    result = false;
                }
                configurationService.updateProperty("server.ssl.key-store-password", serverCertFilePassword);
            }

            if (caFilePassword != null) {
                Setting setting = new Setting();
                setting.setName("caFilePassword");
                setting.setValue(caFilePassword);
                if (settingService.saveSetting(setting) == null) {
                    result = false;
                }
                configurationService.updateProperty("server.ssl.trust-store-password", caFilePassword);
            }

            if (caFile != null) {
                Files.copy(caFile.getInputStream(), Paths.get(CA_FILE), StandardCopyOption.REPLACE_EXISTING);
                configurationService.updateProperty("server.ssl.trust-store", CA_FILE);
            }

            if (serverCertFile != null) {
                Files.copy(caFile.getInputStream(), Paths.get(SERVER_FILE), StandardCopyOption.REPLACE_EXISTING);
                configurationService.updateProperty("server.ssl.key-store", SERVER_FILE);
            }

            System.out.println("Settings update complete - ");
            return new Response(true, "Sucessfully saved", null);
        } catch (Exception ex) {
            return new Response(false, "Save failed - " + ex.getMessage(), null);
        }
    }

    @RequestMapping(value = "/application/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('ADMIN_USER')")
    public Response deleteApplication(@PathVariable Long id) {
        try {
            // boolean result = applicationService.deleteApplicationById(id);
            boolean result = true;
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
