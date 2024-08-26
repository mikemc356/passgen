package com.systemwerx.common.web.repository;

import org.springframework.data.repository.CrudRepository;
import com.systemwerx.common.web.domain.Setting;

/**
 * 
 */
public interface SettingRepository extends CrudRepository<Setting, Long> {
    Setting findByName(String name); 
}

