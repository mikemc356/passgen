package com.systemwerx.common.web.service;

import java.util.List;
import java.util.Optional;
import com.systemwerx.common.web.domain.Setting;

/**
 * 
 */
public interface SettingService {
    List<Setting> findSettings();
    Optional<Setting> findSettingById(Long id);
    Setting saveSetting(Setting setting);
    boolean deleteSettingById(Long id);
}
