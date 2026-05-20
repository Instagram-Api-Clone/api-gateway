package com.hemant.instagram.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Logging from Global Filter: Request URL -> {}", exchange.getRequest().getURI());
        return chain
                .filter(exchange)
                .then(Mono.fromRunnable(() -> log.info("Logging from Global Filter: Response Status Code -> {}", exchange.getResponse().getStatusCode())));
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
