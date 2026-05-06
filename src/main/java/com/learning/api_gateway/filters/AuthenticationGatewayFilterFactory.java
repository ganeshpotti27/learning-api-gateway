package com.learning.api_gateway.filters;

import com.learning.api_gateway.services.JwtService;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationGatewayFilterFactory
    extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

  private final JwtService jwtService;

  public AuthenticationGatewayFilterFactory(JwtService jwtService) {
    super(Config.class);
    this.jwtService = jwtService;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      if (!config.isEnabled()) return chain.filter(exchange);
      String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
      if (Objects.isNull(authorizationHeader)) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }

      String token = authorizationHeader.substring(7);
      Long userId = jwtService.getUserIdFromToken(token);

      var request = exchange.getRequest().mutate().header("X-User-Id", userId.toString()).build();
      return chain.filter(exchange.mutate().request(request).build());
    };
  }

  public static class Config {

    private List<String> allowedRoles;
    private boolean enabled;

    public List<String> getAllowedRoles() {
      return allowedRoles;
    }

    public void setAllowedRoles(List<String> allowedRoles) {
      this.allowedRoles = allowedRoles;
    }

    public boolean isEnabled() {
      return enabled;
    }

    public void setEnabled(boolean enabled) {
      this.enabled = enabled;
    }
  }
}
