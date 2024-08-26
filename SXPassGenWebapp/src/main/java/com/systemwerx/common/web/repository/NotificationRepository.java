package com.systemwerx.common.web.repository;

import java.util.List;
import com.systemwerx.common.web.domain.Notification;
import com.systemwerx.common.web.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, Long> {
    List<Notification> findByUser(User user);
}
