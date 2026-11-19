package com.sip.psm.models;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {}
