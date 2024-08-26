package com.systemwerx.common.web.service.impl;

import com.systemwerx.common.web.domain.Setting;
import com.systemwerx.common.web.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.stream.StreamSupport;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;
import com.systemwerx.common.web.repository.SettingRepository;

@Component
public class SettingServiceImpl implements SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Override
    public List<Setting> findSettings() {
        return StreamSupport.stream(settingRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public Setting saveSetting(Setting setting) {
        Setting saved = settingRepository.findByName(setting.getName());
        if ( saved != null ) {
            saved.setValue(setting.getValue());
            setting = saved;
        }
        return settingRepository.save(setting);
    }

    @Override
    public Optional<Setting> findSettingById(Long id) {
        return settingRepository.findById(id);
    }

    @Override
    public boolean deleteSettingById(Long id) {
        try {
            settingRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
