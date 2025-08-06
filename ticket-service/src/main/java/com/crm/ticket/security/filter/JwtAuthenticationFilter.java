package com.crm.ticket.security.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    // NOT: Bu filtre şu anlık boş. Normalde burada token'ı valide edip,
    // içinden kullanıcı ID'sini ve rollerini alıp bir Authentication nesnesi oluşturarak
    // SecurityContext'e koymamız gerekir. Bu, bir sonraki adımlarda eklenecek.
    // Şimdilik sadece gelen isteği bir sonraki filtreye paslıyoruz.

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .filter(authentication -> authentication instanceof JwtAuthenticationToken)
                .map(authentication -> (JwtAuthenticationToken) authentication)
                .flatMap(authentication -> {
                    // Normalde burada token'dan gelen bilgilere göre
                    // kendi UserDetails nesnemizi veya principal'ımızı oluşturabiliriz.
                    // Şimdilik direkt devam ediyoruz.
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }
}