package com.centralway.assessment.phonebook.services;

import com.centralway.assessment.phonebook.model.User;
import com.centralway.assessment.phonebook.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * {@link User}-related operations.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        User user = userRepository.findOneByUsername(username);
        if (user == null) {
            //UserDetailsService specifies that we must throw an exception rather than returning `null`
            throw new UsernameNotFoundException(username);  
        }
        return user;
    }
}