package com.systemwerx.common.web.service.impl;

import com.systemwerx.common.web.service.ConfigurationService;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationServiceImpl implements ConfigurationService {

    @Override
    public boolean updateProperty(String key, String value) {

        try {
            PropertiesConfiguration conf = new PropertiesConfiguration("application.properties");
            conf.setProperty(key, value);
            conf.save(); 

            return true;
        } catch (ConfigurationException cex) {
            return false;
        }
    }

}
