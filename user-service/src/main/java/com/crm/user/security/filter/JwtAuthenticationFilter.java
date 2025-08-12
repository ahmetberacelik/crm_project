package com.crm.user.security.filter;

import com.crm.user.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }

        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        final String jwt = authHeader.substring(7);

        try {
            // Subject: kullanıcı ID'si (String). Bunu principal olarak kullanıyoruz
            final String subject = jwtUtil.extractUsername(jwt);
            // Roller claim'inden yetkileri oluştur
            final String rolesString = jwtUtil.extractRoles(jwt);
            final java.util.List<SimpleGrantedAuthority> authorities = rolesString == null || rolesString.isBlank()
                    ? java.util.List.of()
                    : java.util.Arrays.stream(rolesString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(java.util.stream.Collectors.toList());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    subject,
                    null,
                    authorities
            );
            return chain.filter(exchange).contextWrite(
                    ReactiveSecurityContextHolder.withAuthentication(authToken)
            );
        } catch (Exception e) {
            // Token geçersiz/expire ise kimlik doğrulaması olmadan devam et
            return chain.filter(exchange);
        }
    }
} 