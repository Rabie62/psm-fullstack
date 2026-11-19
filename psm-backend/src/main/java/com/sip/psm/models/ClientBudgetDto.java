package com.sip.psm.models;

public record ClientBudgetDto(
        String clientNom,
        long nombreProjets,
        double budgetTotal
) {}
