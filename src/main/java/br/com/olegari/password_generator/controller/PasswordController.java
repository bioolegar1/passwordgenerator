package br.com.olegari.password_generator.controller;

import br.com.olegari.password_generator.domain.PasswordToken;
import br.com.olegari.password_generator.dto.PasswordResponse;
import br.com.olegari.password_generator.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/passwords")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;

    @GetMapping("/generate")
    public ResponseEntity<PasswordResponse> generatePassword() {
        PasswordToken savedToken = passwordService.generateUniquePasswordToken();

        PasswordResponse response = new PasswordResponse(
                savedToken.getTokenValue(),
                savedToken.getExpiresAt()
        );

        return ResponseEntity.ok(response);
    }
}