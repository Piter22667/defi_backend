package com.example.defi.defi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtService jwtService; //Сервіс для роботи з JWT токенами
    private final UserDetailsService userDetailsService; //Сервіс для завантаження деталей користувача


    @Override
    protected void doFilterInternal
            ( @NonNull HttpServletRequest request,
              @NonNull HttpServletResponse response,
              @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization"); //Отримуємо заголовок авторизації
        final String jwt;
        final String userEmail;

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); //Якщо заголовок не існує або не починається з Bearer, пропускаємо фільтр
            return;
        }

        String path = request.getServletPath();
        if (path.startsWith("/api/v1/") || path.startsWith("/cr/")) {
            filterChain.doFilter(request, response);
            return;
        }
        //Якщо заголовок існує і починається з Bearer
        jwt = authorizationHeader.substring(7); //Витягуємо токен з заголовка
        userEmail = jwtService.extractUsername(jwt); //Отримуємо ім'я користувача з токена

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //Якщо ім'я користувача не null і аутентифікація ще не виконана
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail); //Завантажуємо деталі користувача з бази даних
            if (jwtService.isTokenValid(jwt, userDetails)){
                //Якщо токен валідний, створюємо аутентифікацію
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //Додаємо деталі аутентифікації (
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response); //Продовжуємо фільтрацію
    }
}
