package org.effective_mobile.task_management_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    public User getByUsername(String userName) {
        Optional<User> user = userRepository.findByUsername(userName);
        user.orElseThrow(() -> new EntityNotFoundException("User (username = %s) was not found in database"));
        return user.get();
    }
}
