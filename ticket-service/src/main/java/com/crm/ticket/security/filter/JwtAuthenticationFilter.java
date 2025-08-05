package com.crm.ticket.security.filter;

import com.crm.ticket.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange); // Token yoksa devam et
        }

        String token = authHeader.substring(7);

        try {
            if (!jwtUtil.isTokenExpired(token)) {
                String username = jwtUtil.extractUsername(token); // Bu aslında user ID
                String rolesString = jwtUtil.extractRoles(token);

                List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesString.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                // Principal olarak user ID'yi (username) kullanıyoruz.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContext context = new SecurityContextImpl(authentication);
                // Güvenlik bağlamını oluşturup zincire devam et
                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
            }
        } catch (Exception e) {
            // Geçersiz token (örn. imza hatası), yetkilendirme yapmadan devam et
            // Sonraki filtreler (authorizeExchange) bunu yakalayacaktır.
        }

        return chain.filter(exchange);
    }
}
