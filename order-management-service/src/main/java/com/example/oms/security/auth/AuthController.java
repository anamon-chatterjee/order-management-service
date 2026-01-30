package com.example.oms.security.auth;

import com.example.oms.domain.entity.UserEntity;
import com.example.oms.repository.UserRepository;
import com.example.oms.security.CustomUserDetails;
import com.example.oms.security.auth.dto.LoginRequest;
import com.example.oms.security.auth.dto.LoginResponse;
import com.example.oms.security.auth.dto.RefreshRequest;
import com.example.oms.security.jwt.JwtUtil;
import com.example.oms.security.refresh.RefreshToken;
import com.example.oms.security.refresh.RefreshTokenRepository;
import com.example.oms.security.refresh.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserDetailsService userDetailsService,
                          UserRepository userRepository,
                          RefreshTokenService refreshTokenService,
                          RefreshTokenRepository refreshTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserEntity user =
                userRepository.findByUsername(request.username())
                        .orElseThrow();

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getUsername());

        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(
                new LoginResponse(accessToken, refreshToken.getToken())
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestBody RefreshRequest request
    ) {

        RefreshToken newRefreshToken =
                refreshTokenService.verifyAndRotate(request.refreshToken());

        UserEntity user = newRefreshToken.getUser();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        String newAccessToken =
                jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(
                new LoginResponse(newAccessToken, request.refreshToken())

        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {

        UserEntity user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

        refreshTokenRepository.revokeAllByUser(user);

        return ResponseEntity.noContent().build();
    }

}
