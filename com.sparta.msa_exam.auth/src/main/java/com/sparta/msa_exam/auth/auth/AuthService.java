package com.sparta.msa_exam.auth.auth;

import com.sparta.msa_exam.auth.auth.dto.AuthUserResponse;
import com.sparta.msa_exam.auth.core.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j(topic = "AuthService")
@Service
public class AuthService {

    @Value("${spring.application.name}")
    private String issuer;

    @Value("${service.jwt.access-expiration}")
    private Long accessExpiration;

    private final SecretKey secretKey;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * AuthService 생성자.
     * Base64 URL 인코딩된 비밀 키를 디코딩하여 HMAC-SHA 알고리즘에 적합한 SecretKey 객체를 생성합니다.
     *
     * @param secretKey Base64 URL 인코딩된 비밀 키
     */
    public AuthService(@Value("${service.jwt.secret-key}") String secretKey,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 사용자 ID를 받아 JWT 액세스 토큰을 생성합니다.
     *
     * @param email 사용자 email
     * @param role 사용자 권한
     * @return 생성된 JWT 액세스 토큰
     */
    public String createAccessToken(String email, String role) {
        return "Bearer " + Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(secretKey, io.jsonwebtoken.SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 사용자 등록
     *
     * @param user 사용자 정보
     * @return 저장된 사용자
     */
    public UserEntity signUp(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * 사용자 인증
     *
     * @param email 사용자 email
     * @param password 비밀번호
     * @return JWT 액세스 토큰
     */
    public String signIn(String email, String password) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid user ID or password");
        }

        return createAccessToken(user.getEmail(), user.getRole().toString());
    }

    public AuthUserResponse checkAccessToken(String token) {
        String newToken = extractToken(token);

        UserEntity user = validateToken(newToken);

        if(user != null){

            log.info("user : {}" , user.getEmail());

            return AuthUserResponse.builder()
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .isValid(true)
                    .build();
        }

        return AuthUserResponse.builder()
                .isValid(false)
                .build();
    }

    private String extractToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    private UserEntity validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(secretKey)
                    .build().parseSignedClaims(token);
            log.info("#####payload :: " + claimsJws.getPayload().toString());
            Claims claims = claimsJws.getBody();
            String email = claims.get("email").toString();
            String role = claims.get("role").toString();

            UserEntity user = userRepository.findByEmail(email).orElseThrow(()
                    -> new IllegalArgumentException("Invalid user ID"));

            if(!user.getRole().toString().equals(role)){
                throw new IllegalArgumentException("Invalid user ID");
            }

            // 추가적인 검증 로직 (예: 토큰 만료 여부 확인 등)을 여기에 추가할 수 있습니다.
            return user;
        } catch (Exception e) {
            return null;
        }
    }
}