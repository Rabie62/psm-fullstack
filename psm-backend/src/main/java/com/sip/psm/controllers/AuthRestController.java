package com.sip.psm.controllers;

import com.sip.psm.config.JwtTokenUtil;
import com.sip.psm.entities.RefreshToken;
import com.sip.psm.entities.User;
import com.sip.psm.models.*;
import com.sip.psm.services.JwtUserDetailsService;
import com.sip.psm.services.RefreshTokenService;
import com.sip.psm.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtUserDetailsService userDetailsService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthRestController(AuthenticationManager authenticationManager,
                              JwtTokenUtil jwtTokenUtil,
                              JwtUserDetailsService userDetailsService,
                              UserService userService,
                              RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody JwtRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.email(),
                            authenticationRequest.password()
                    )
            );
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Compte utilisateur désactivé. Veuillez contacter l'administrateur."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou mot de passe incorrect."));
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email());
        final String token = jwtTokenUtil.generateToken(userDetails);
        final User user = userService.findByEmail(authenticationRequest.email());
        final RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getLibelle().name())
                .toList();

        return ResponseEntity.ok(new JwtResponse(
                token,
                refreshToken.getToken(),
                user.getId(),
                user.getEmail(),
                user.getNom(),
                user.getPrenom(),
                user.getTel(),
                roles
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return refreshTokenService.findByToken(request.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    String accessToken = jwtTokenUtil.generateToken(userDetails);
                    return ResponseEntity.ok(new TokenRefreshResponse(accessToken, request.refreshToken()));
                })
                .orElseThrow(() -> new IllegalArgumentException("Refresh token introuvable en base !"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        User created = userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Inscription réussie ! Un email de notification asynchrone a été envoyé à l'administrateur.",
                "user", created
        ));
    }
}
