package com.example.dynamicconsent.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class UserIdAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // OPTIONS 요청은 CORS preflight이므로 인증 체크를 건너뜀
        if ("OPTIONS".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String userId = request.getHeader("X-UserId");
        
        if (userId == null || userId.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":{\"code\":\"MISSING_USER_ID\",\"message\":\"X-UserId header is required\"}}");
            return;
        }
        
        // Create authentication token with user ID
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
        
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}


