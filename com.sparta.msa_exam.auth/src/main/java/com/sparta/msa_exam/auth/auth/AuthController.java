package com.sparta.msa_exam.auth.auth;


import com.sparta.msa_exam.auth.auth.dto.AuthUserResponse;
import com.sparta.msa_exam.auth.auth.dto.LoginRequest;
import com.sparta.msa_exam.auth.core.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signIn")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest signInRequest){
        String token = authService.signIn(signInRequest.getEmail(), signInRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK)
                .body(token);
    }

    @PostMapping("/auth/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserEntity user) {
        UserEntity createdUser = authService.signUp(user);
        return ResponseEntity.ok(createdUser);
    }


    @GetMapping("/auth/validate-token")
    public ResponseEntity<AuthUserResponse> validateToken(@RequestHeader("Authorization") String token) {
        AuthUserResponse response = authService.checkAccessToken(token);
        System.out.println("validate-token 끝!! @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ " + response);
        return ResponseEntity.ok(response);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class AuthResponse {
        private String access_token;
    }

}




