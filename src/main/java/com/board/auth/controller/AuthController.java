package com.board.auth.controller;

import com.board.auth.dto.CodeDTO;
import com.board.auth.dto.TokenDTO;
import com.board.auth.service.AuthService;
import com.board.auth.dto.LoginDTO;
import com.board.auth.dto.RegisterDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {
    private final AuthService authService;
    @PostMapping("/token")
    public ResponseEntity<TokenDTO> getToken(@Valid @RequestBody CodeDTO codeDTO){
        return ResponseEntity.ok(authService.processGoogleAuthCode(codeDTO.getCode()));
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TokenDTO loginUser(@Valid @RequestBody LoginDTO loginDTO){
        return authService.login(loginDTO);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenDTO registerUser(@RequestBody RegisterDTO registerDTO){
        return authService.createUser(registerDTO);
    }
}
