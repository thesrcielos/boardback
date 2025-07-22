package com.board.auth.service;

import com.board.auth.dto.GoogleTokenResponse;
import com.board.auth.dto.GoogleUserInfo;
import com.board.auth.dto.TokenDTO;
import com.board.auth.jwt.JwtUtil;
import com.board.auth.dto.LoginDTO;
import com.board.auth.dto.RegisterDTO;
import com.board.auth.user.Role;
import com.board.auth.user.UserEntity;
import com.board.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtService;

    public TokenDTO createUser(RegisterDTO registerDTO) {
        // Should check if the user already exist.
        if(userRepository.existsByEmail(registerDTO.getEmail())){
            throw new RuntimeException("Email in use");
        }
        UserEntity user = new UserEntity(
                null,
                registerDTO.getName(),
                passwordEncoder.encode(registerDTO.getPassword()),
                registerDTO.getEmail(), Role.USER);
        userRepository.save(user);
        return TokenDTO.builder().token(jwtService.getToken(user.getUsername())).build();
    }

    public TokenDTO login(LoginDTO loginDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
        UserEntity user = userRepository.findByEmail(loginDTO.getEmail()).orElseThrow(()->new UsernameNotFoundException("The user with email not found." + loginDTO.getEmail()));
        return TokenDTO.builder().token(jwtService.getToken(user.getUsername())).build();
    }
}
