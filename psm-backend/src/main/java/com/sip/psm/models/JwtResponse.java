package com.sip.psm.models;

import java.util.List;

public record JwtResponse(
        String token,
        String refreshToken,
        Long id,
        String email,
        String nom,
        String prenom,
        String tel,
        List<String> roles
) {}
