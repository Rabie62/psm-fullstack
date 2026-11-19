package com.sip.psm.controllers;

import com.sip.psm.entities.*;
import com.sip.psm.models.ClientBudgetDto;
import com.sip.psm.models.DashboardStatsDto;
import com.sip.psm.repositories.ClientRepository;
import com.sip.psm.repositories.ProjetRepository;
import com.sip.psm.repositories.TacheRepository;
import com.sip.psm.repositories.UserRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur REST fournissant les indicateurs analytiques (KPIs) pour le Dashboard Argon (Recommandation 1).
 */
@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardRestController {

    private final ClientRepository clientRepository;
    private final ProjetRepository projetRepository;
    private final UserRepository userRepository;
    private final TacheRepository tacheRepository;

    public DashboardRestController(ClientRepository clientRepository,
                                   ProjetRepository projetRepository,
                                   UserRepository userRepository,
                                   TacheRepository tacheRepository) {
        this.clientRepository = clientRepository;
        this.projetRepository = projetRepository;
        this.userRepository = userRepository;
        this.tacheRepository = tacheRepository;
    }

    @GetMapping("/stats")
    public DashboardStatsDto getStats() {
        long totalClients = clientRepository.count();
        long totalProjets = projetRepository.count();
        long totalUsers = userRepository.count();
        long totalTaches = tacheRepository.count();
        Double budgetSum = projetRepository.sumTotalBudget();
        double budgetTotalEngage = budgetSum != null ? budgetSum : 0.0;

        Map<String, Long> projetsParStatut = new LinkedHashMap<>();
        for (EProjectStatus st : EProjectStatus.values()) {
            projetsParStatut.put(st.name(), projetRepository.countByStatut(st));
        }

        Map<String, Long> tachesParStatut = new LinkedHashMap<>();
        for (ETaskStatus ts : ETaskStatus.values()) {
            tachesParStatut.put(ts.name(), tacheRepository.countByStatut(ts));
        }

        List<Projet> allProjets = projetRepository.findAll();
        List<ClientBudgetDto> topClients = allProjets.stream()
                .filter(p -> p.getClient() != null)
                .collect(Collectors.groupingBy(p -> p.getClient().getNom()))
                .entrySet().stream()
                .map(entry -> new ClientBudgetDto(
                        entry.getKey(),
                        entry.getValue().size(),
                        entry.getValue().stream().mapToDouble(Projet::getBudget).sum()
                ))
                .sorted(Comparator.comparingDouble(ClientBudgetDto::budgetTotal).reversed())
                .limit(5)
                .toList();

        return new DashboardStatsDto(
                totalClients,
                totalProjets,
                totalUsers,
                totalTaches,
                budgetTotalEngage,
                projetsParStatut,
                tachesParStatut,
                topClients
        );
    }
}
