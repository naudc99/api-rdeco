package es.ndc.api_rdeco.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    JwtUtilityService jwtUtilityService;
    CustomUserDetailsService userDetailsService;

    public JwtAuthorizationFilter(JwtUtilityService jwtUtilityService, CustomUserDetailsService userDetailsService) {
        this.jwtUtilityService = jwtUtilityService;
        this.userDetailsService = userDetailsService;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(7);
        try {
            JWTClaimsSet claims = jwtUtilityService.parseJWT(token);
            Long userId = Long.parseLong(claims.getSubject());

            String roleName = (String) claims.getClaim("role");

            UserDetails userDetails = userDetailsService.loadUserById(userId);

            GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
            List<GrantedAuthority> authorities = Collections.singletonList(authority);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
        filterChain.doFilter(request, response);
    }
}
