package com.systemwerx.common.web.service;

import java.util.List;
import com.systemwerx.common.web.domain.Notification;
import com.systemwerx.common.web.domain.User;

/**
 * 
 */
public interface NotificationService {
    List<Notification> findByUser(User username);
}
