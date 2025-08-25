package com.crm.user.security.filter; // Paket adınızın doğru olduğundan emin olun

import com.crm.user.security.jwt.JwtUtil; // Kendi JwtUtil import'unuzu kullanın
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 1. Authorization header'ını al.
        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 2. Eğer header yoksa veya "Bearer " ile başlamıyorsa, hiçbir şey yapma.
        // İstek "anonymous" olarak devam eder ve SecurityConfig'deki kurallar devreye girer.
        // Bu, /actuator ve /api/auth/login gibi public endpoint'lerin çalışmasını sağlar.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        // 3. "Bearer " kısmını atarak sadece token'ı al.
        final String jwt = authHeader.substring(7);

        try {
            // 4. Token'ı doğrula ve içindeki bilgileri (kullanıcı ID ve roller) al.
            final String subject = jwtUtil.extractUsername(jwt);
            final String rolesString = jwtUtil.extractRoles(jwt);
            final List<SimpleGrantedAuthority> authorities = rolesString == null || rolesString.isBlank()
                    ? List.of()
                    : Arrays.stream(rolesString.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // 5. Spring Security Context'i için bir Authentication objesi oluştur.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    subject,
                    null,
                    authorities
            );

            // 6. İsteğin geri kalanına bu kimlik bilgisiyle devam etmesini sağla.
            return chain.filter(exchange).contextWrite(
                    ReactiveSecurityContextHolder.withAuthentication(authToken)
            );
        } catch (Exception e) {
            // 7. Token geçersizse (süresi dolmuş, imza hatası vb.), yine de isteğin devam etmesine izin ver.
            // Bu istek "anonymous" olarak devam eder ve SecurityConfig'deki .authenticated() kuralına takılır.
            return chain.filter(exchange);
        }
    }
}