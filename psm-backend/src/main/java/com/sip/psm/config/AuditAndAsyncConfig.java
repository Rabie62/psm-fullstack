package com.sip.psm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuration activant :
 * 1. L'exécution asynchrone (@EnableAsync) pour l'envoi d'emails non-bloquant.
 * 2. L'audit automatique JPA (@EnableJpaAuditing) pour tracer l'auteur et les dates de création/modification.
 */
@Configuration
@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditAndAsyncConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.of("SYSTEM");
            }
            return Optional.ofNullable(auth.getName());
        };
    }
}
