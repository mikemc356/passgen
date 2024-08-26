package com.systemwerx.common.web.service.impl;

import com.systemwerx.common.web.domain.User;
import com.systemwerx.common.web.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * 
 */
@Component
public class UserService implements UserDetailsService {

    private final String EMPTY_STRING = "";

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(s);

        if (user == null) {
            throw new UsernameNotFoundException(String.format("The username %s doesn't exist", s));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        });

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), authorities);

        return userDetails;
    }

    public User saveUser(User user) {
        if (user.getPassword().equals(ApplicationServiceImpl.OBFUSCATED_KEY)) {
            Optional<User> userSaved = userRepository.findById(user.getId());
            if (!userSaved.isPresent()) {
                // Should not have OBFUSCATED Password and no user in DB
                throw new IllegalArgumentException("User requires a password");
            }

            // Add value into app and save
            user.setPassword(userSaved.get().getPassword());
        }
        else {
            // Crypt password
            user.setCryptedPassword(user.getPassword());
        }

        if (user.getId() == null || user.getId() == -1 ) {
            User userSaved = userRepository.findByUsername(user.getUsername());
            if (userSaved != null) {
                throw new IllegalArgumentException("User exists");
            }
        }

        user.setFirstName(EMPTY_STRING);
        user.setLastName(EMPTY_STRING);
        return userRepository.save(user);
    }

    public List<User> findUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public boolean deleteUserById(Long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
