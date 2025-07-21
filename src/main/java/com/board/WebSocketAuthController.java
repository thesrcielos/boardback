package com.board;

import com.board.auth.WebSocketAuthService;
import com.board.auth.dto.TokenDTO;
import com.board.auth.user.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ws-auth")
public class WebSocketAuthController {

    private final WebSocketAuthService authService;

    public WebSocketAuthController(WebSocketAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/token")
    public ResponseEntity<TokenDTO> generate(@RequestParam String id) {
        String token = authService.generateToken(id);
        return ResponseEntity.ok(new TokenDTO(token));
    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<UserDTO> validate(@PathVariable String token) {
        UserDTO user = authService.getUserFromToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(user);
    }
}
