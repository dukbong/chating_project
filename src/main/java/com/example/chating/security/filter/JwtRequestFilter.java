//package com.example.chating.security.filter;
//
//import io.fusionauth.jwt.InvalidJWTException;
//import io.fusionauth.jwt.Verifier;
//import io.fusionauth.jwt.domain.JWT;
//import io.fusionauth.jwt.hmac.HMACVerifier;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.time.ZoneOffset;
//import java.time.ZonedDateTime;
//import java.util.Map;
//
//@Component
//public class JwtRequestFilter extends OncePerRequestFilter {
//
//    @Value("${token.secret}")
//    private String secret;
//    @Value("${token.expiration_time}")
//    private long expiration;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        final String authorizationHeader = request.getHeader("Authorization");
//
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            final String token = authorizationHeader.substring(7);
//            JWT jwt = validateToken(token);
//            if (jwt != null) {
//                Map<String, Object> claims = jwt.getAllClaims();
//
//                if(SecurityContextHolder.getContext().getAuthentication() == null) {
//                    UserDetails userDetails = null;
//                }
//            }
//
//        }
//
//    }
//
//    private JWT validateToken(String token) {
//        Verifier verifier = HMACVerifier.newVerifier(secret);
//        try {
//            JWT jwt = JWT.getDecoder().decode(token, verifier);
//
//            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
//
//            if(jwt.expiration.isBefore(now)) {
//                throw new InvalidJWTException("token is expired");
//            }
//
//            return jwt;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
