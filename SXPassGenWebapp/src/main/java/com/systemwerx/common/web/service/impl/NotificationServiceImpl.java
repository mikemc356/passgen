package com.systemwerx.common.web.service.impl;

import com.systemwerx.common.web.domain.Notification;
import com.systemwerx.common.web.domain.User;
import com.systemwerx.common.web.repository.UserRepository;
import com.systemwerx.common.web.repository.NotificationRepository;
import com.systemwerx.common.web.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<Notification> findByUser(User user) {
        return notificationRepository.findByUser(user);
    }
}
