package org.effective_mobile.task_management_system.service;

import jakarta.persistence.EntityNotFoundException;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.UserRole;
import org.effective_mobile.task_management_system.exception.SuchUserAlreadyExistsException;
import org.effective_mobile.task_management_system.pojo.SignupRequest;
import org.effective_mobile.task_management_system.repository.RoleRepository;
import org.effective_mobile.task_management_system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(
        PasswordEncoder passwordEncoder,
        UserRepository userRepository,
        RoleRepository roleRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public void validateNewUser(SignupRequest signUpRequest) {
        String username = signUpRequest.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new SuchUserAlreadyExistsException(String.format("username '%s' is already taken", username));
        }

        String email = signUpRequest.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new SuchUserAlreadyExistsException(String.format("email '%s' is already taken", email));
        }
    }

    public Long createNewUser(SignupRequest signUpRequest) {
        UserRole defaultRole = UserRole.USER;
        Role usualUserRole = roleRepository
            .findByName(defaultRole)
            .orElseThrow(() -> {
                String message = String.format(
                    Role.class.getSimpleName() + " (name = '%s') was not found", defaultRole.name()
                );
                return new EntityNotFoundException(message);
            });

        User user = User.builder()
            .username(signUpRequest.getUsername())
            .roles(List.of(usualUserRole))
            .email(signUpRequest.getEmail())
            .password(passwordEncoder.encode(signUpRequest.getPassword())).build();

        userRepository.save(user);
        return user.getId();
    }

    public User getByUsername(String userName) {
        Optional<User> user = userRepository.findByUsername(userName);
        user.orElseThrow(() -> new EntityNotFoundException("User (username = %s) was not found in database"));
        return user.get();
    }
}
