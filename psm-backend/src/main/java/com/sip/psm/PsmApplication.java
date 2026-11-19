package com.sip.psm;

import com.sip.psm.entities.ERole;
import com.sip.psm.entities.Role;
import com.sip.psm.repositories.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PsmApplication {

    private static final Logger log = LoggerFactory.getLogger(PsmApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PsmApplication.class, args);
    }

    /**
     * Initialise uniquement les deux rôles de référence (ADMIN et COLLABORATEUR) dans la table `roles`
     * s'ils n'existent pas encore, sans aucun compte utilisateur pré-créé.
     */
    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            roleRepository.findByLibelle(ERole.ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(ERole.ADMIN)));

            roleRepository.findByLibelle(ERole.COLLABORATEUR)
                    .orElseGet(() -> roleRepository.save(new Role(ERole.COLLABORATEUR)));

            log.info("Rôles de référence (ADMIN, COLLABORATEUR) vérifiés en base.");
        };
    }
}
