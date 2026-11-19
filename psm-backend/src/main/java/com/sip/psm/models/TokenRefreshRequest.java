package com.sip.psm.models;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequest(
        @NotBlank(message = "Le refresh token est obligatoire")
        String refreshToken
) {}
