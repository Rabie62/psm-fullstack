package com.sip.psm.models;

import java.util.List;
import java.util.Map;

/**
 * Record Java 17 agrégeant tous les KPIs du Tableau de Bord Analytique (Recommandation 1).
 */
public record DashboardStatsDto(
        long totalClients,
        long totalProjets,
        long totalUsers,
        long totalTaches,
        double budgetTotalEngage,
        Map<String, Long> projetsParStatut,
        Map<String, Long> tachesParStatut,
        List<ClientBudgetDto> topClientsParBudget
) {}
