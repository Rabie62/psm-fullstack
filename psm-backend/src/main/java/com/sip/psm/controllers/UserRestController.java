package com.sip.psm.controllers;

import com.sip.psm.entities.Role;
import com.sip.psm.entities.User;
import com.sip.psm.repositories.RoleRepository;
import com.sip.psm.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST de gestion des utilisateurs et des rôles (réservé à l'ADMIN).
 */
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserRestController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    public UserRestController(UserService userService, RoleRepository roleRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping({"", "/list"})
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String newRole = payload.getOrDefault("role", "COLLABORATEUR");
        return ResponseEntity.ok(userService.updateRole(id, newRole));
    }

    @PutMapping("/{id}/enable")
    public ResponseEntity<User> enableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleActive(id, true));
    }

    @PutMapping("/{id}/disable")
    public ResponseEntity<User> disableUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleActive(id, false));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
    }
}
