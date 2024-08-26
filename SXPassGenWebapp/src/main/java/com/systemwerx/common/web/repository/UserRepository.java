package com.systemwerx.common.web.repository;

import org.springframework.data.repository.CrudRepository;
import com.systemwerx.common.web.domain.User;

/**
 * 
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}

