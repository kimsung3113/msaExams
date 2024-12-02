package com.sparta.msa_exam.gateway;

import com.sparta.msa_exam.gateway.dto.AuthResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
@Slf4j
@Component
public class LocalJwtAuthenticationFilter implements GlobalFilter {

    private final WebClient webClient;

    @Value("${service.jwt.secret-key}")
    private String secretKey;

    public LocalJwtAuthenticationFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:19095").build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.equals("/auth/signIn") || path.equals("/auth/signUp")) {
            return chain.filter(exchange);  // /signIn 경로는 필터를 적용하지 않음
        }

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (token == null || token.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return webClient.get()
                .uri("/auth/validate-token")
                .header("Authorization", token)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError, // 에러 상태일 때
                        clientResponse -> Mono.error(new RuntimeException("Invalid Token"))
                )
                .bodyToMono(AuthResponse.class)
                .flatMap(authResponse  -> {
                    if (Boolean.FALSE.equals(authResponse.isValid())) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        log.info("AuthResponse 응답 실패");
                        return exchange.getResponse().setComplete();
                    }

                    log.info("AuthResponse 응답 성공");
                    log.info("authResponse.email : " + authResponse.getEmail() + " role : " + authResponse.getRole());

                    // 요청 객체 수정 (여기서 수정한 값을 이어서 사용)
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Email", authResponse.getEmail())
                            .header("X-Role", authResponse.getRole())
                            .build();

                    // 새로운 요청 객체를 사용한 exchange를 생성
                    ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

                    // 수정된 exchange를 사용하여 필터 체인 진행
                    return chain.filter(mutatedExchange); // 요청을 계속 진행
                });
    }

        // 여기서 FeignClient로 auth-service 갔다 와야 될듯! Db에서 데이터 가져와서 여기서 header에 넣는다!!

    }



