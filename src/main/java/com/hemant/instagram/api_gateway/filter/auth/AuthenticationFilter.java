package com.hemant.instagram.api_gateway.filter.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hemant.instagram.api_gateway.models.ApiError;
import com.hemant.instagram.api_gateway.models.ApiResponse;
import com.hemant.instagram.api_gateway.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public AuthenticationFilter(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("AuthenticationGatewayFilterFactory: Authenticating request to {}", exchange.getRequest().getURI());

            // Authentication logic would go here
            HttpHeaders headers = exchange.getRequest().getHeaders();
            final String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);

            if(authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                log.error("Missing or invalid Authorization header");
                return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
            }

            final String token = authHeader.split(BEARER_PREFIX)[1];

            try {
                String userId = jwtService.getUserIdFromToken(token);

                ServerWebExchange mutatedExchange = exchange
                        .mutate()
                        .request(r -> r.header("X-User-Id", userId))
                        .build();

                return chain.filter(mutatedExchange);
            }
            catch (ExpiredJwtException e) {
                log.error("Expired JWT token: {}", e.getMessage());
                return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, e.getMessage());
            }
            catch (JwtException e) {
                log.error("Invalid JWT token: {}", e.getMessage());
                return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, "Invalid token");
            }
        };
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiError apiError = ApiError.builder()
                .httpStatus(status)
                .message(message)
                .build();
        ApiResponse<Void> apiResponse = new ApiResponse<>(apiError);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error serializing error response: {}", e.getMessage());
            return response.setComplete();
        }
    }

    public static class Config {
        private boolean isEnabled;
    }
}
