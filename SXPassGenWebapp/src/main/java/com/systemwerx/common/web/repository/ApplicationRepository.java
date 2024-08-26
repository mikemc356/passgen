package com.systemwerx.common.web.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import com.systemwerx.common.web.domain.Application;

/**
 * 
 */
public interface ApplicationRepository extends CrudRepository<Application, Long> {
   
    @Query(value = "SELECT * FROM application where application = :name", nativeQuery = true)
    Optional<Application> findByName(@Param("name") String name);
}
