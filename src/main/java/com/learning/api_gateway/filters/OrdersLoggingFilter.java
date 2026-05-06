package com.learning.api_gateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrdersLoggingFilter extends AbstractGatewayFilterFactory<OrdersLoggingFilter.Config> {

  public OrdersLoggingFilter() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      log.info("Order Filter Pre: {}", exchange.getRequest().getURI());
      return chain.filter(exchange);
    };
  }

  public static class Config {}
}
