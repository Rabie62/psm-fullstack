package com.sip.psm.services;

import com.sip.psm.entities.ERole;
import com.sip.psm.entities.Role;
import com.sip.psm.entities.User;
import com.sip.psm.exceptions.ResourceNotFoundException;
import com.sip.psm.models.RegisterRequest;
import com.sip.psm.repositories.RoleRepository;
import com.sip.psm.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable avec l'email : " + email));
    }

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre compte !");
        }

        User user = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.tel(),
                request.nom(),
                request.prenom()
        );
        user.setActive(1);

        Set<String> strRoles = request.roles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role defaultRole = roleRepository.findByLibelle(ERole.COLLABORATEUR)
                    .orElseThrow(() -> new ResourceNotFoundException("Rôle COLLABORATEUR introuvable"));
            roles.add(defaultRole);
        } else {
            strRoles.forEach(roleStr -> {
                ERole targetRole = "ADMIN".equalsIgnoreCase(roleStr) ? ERole.ADMIN : ERole.COLLABORATEUR;
                Role resolvedRole = roleRepository.findByLibelle(targetRole)
                        .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable : " + targetRole));
                roles.add(resolvedRole);
            });
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        emailService.notifyAdminOnRegistration(savedUser);

        return savedUser;
    }

    public User updateRole(Long userId, String newRoleStr) {
        User user = findById(userId);
        ERole eRole = "ADMIN".equalsIgnoreCase(newRoleStr) ? ERole.ADMIN : ERole.COLLABORATEUR;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && user.getEmail().equalsIgnoreCase(auth.getName()) && eRole != ERole.ADMIN) {
            throw new IllegalArgumentException("Vous ne pouvez pas retirer le rôle ADMIN de votre propre compte actif !");
        }

        Role role = roleRepository.findByLibelle(eRole)
                .orElseThrow(() -> new ResourceNotFoundException("Rôle introuvable : " + newRoleStr));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public User toggleActive(Long userId, boolean activate) {
        User user = findById(userId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!activate && auth != null && user.getEmail().equalsIgnoreCase(auth.getName())) {
            throw new IllegalArgumentException("Vous ne pouvez pas désactiver votre propre compte administrateur actif !");
        }

        user.setActive(activate ? 1 : 0);
        User updated = userRepository.save(user);
        emailService.sendAccountStatusEmail(updated.getEmail(), activate);
        return updated;
    }

    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}
