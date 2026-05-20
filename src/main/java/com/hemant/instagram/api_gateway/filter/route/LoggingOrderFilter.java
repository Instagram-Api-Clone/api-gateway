package com.hemant.instagram.api_gateway.filter.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingOrderFilter extends AbstractGatewayFilterFactory<LoggingOrderFilter.Config> {

    public LoggingOrderFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("LoggingOrderFilter: Pre-processing request to {}", exchange.getRequest().getURI());
            return chain.filter(exchange);
        };
    }

    public static class Config {

    }
}
