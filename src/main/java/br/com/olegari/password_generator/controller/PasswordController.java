package br.com.olegari.password_generator.controller;

import br.com.olegari.password_generator.dto.PasswordResponse; // Verifique o import
import br.com.olegari.password_generator.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/passwords")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @GetMapping("/current")
    public ResponseEntity<PasswordResponse> getCurrentPassword() {
        var activeToken = passwordService.getCurrentActiveToken();
        PasswordResponse response = new PasswordResponse(
                activeToken.getTokenValue(),
                activeToken.getExpiresAt()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validatePassword(@RequestBody Map<String, String> payload) {
        String tokenToValidate = payload.get("token");
        boolean isValid = passwordService.isTokenValid(tokenToValidate);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
}