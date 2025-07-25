package com.board.auth.service;

import com.board.auth.dto.GoogleTokenResponse;
import com.board.auth.dto.GoogleUserInfo;
import com.board.auth.dto.TokenDTO;
import com.board.auth.jwt.JwtUtil;
import com.board.auth.user.Role;
import com.board.auth.user.UserEntity;
import com.board.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GoogleService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public TokenDTO processGoogleAuthCode(String code) {
        GoogleTokenResponse googleTokens = exchangeCodeForTokens(code);
        GoogleUserInfo userInfo = getUserInfo(googleTokens.getAccess_token());

        UserEntity user = findOrCreateUser(userInfo);

        String jwtToken = jwtUtil.getToken(user.getEmail());

        return new TokenDTO(jwtToken);
    }

    private GoogleTokenResponse exchangeCodeForTokens(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<GoogleTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    GoogleTokenResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            System.out.println(e.getMessage());
            throw new OAuth2AuthenticationException("Error exchanging code for tokens: " + e.getMessage());
        }
    }

    private GoogleUserInfo getUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<GoogleUserInfo> response = restTemplate.exchange(
                    userInfoUrl,
                    HttpMethod.GET,
                    request,
                    GoogleUserInfo.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new OAuth2AuthenticationException("Error getting user info: " + e.getMessage());
        }
    }

    private UserEntity findOrCreateUser(GoogleUserInfo userInfo) {
        return userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(userInfo.getEmail());
                    newUser.setName(userInfo.getName());
                    //newUser.setPicture(userInfo.getPicture());
                    //newUser.setProvider(AuthProvider.GOOGLE);
                    newUser.setRole(Role.USER);
                    return userRepository.save(newUser);
                });
    }

}
