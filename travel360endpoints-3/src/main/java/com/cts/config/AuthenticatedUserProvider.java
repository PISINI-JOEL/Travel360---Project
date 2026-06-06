package com.cts.config;

import com.cts.entity.User;
import com.cts.enums.Role;
import jakarta.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Resolves the currently authenticated user from the security context and
 * enforces ownership rules. This implementation extracts user details
 * directly from the JWT claims to avoid redundant database calls.
 */
@Component
@AllArgsConstructor
public class AuthenticatedUserProvider {

    private final JWTUtil jwtUtil;
    private final HttpServletRequest request;

    /**
     * Resolves the current user by extracting claims from the JWT in the request header.
     */
    public User current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            throw new AccessDeniedException("No authenticated user in context");
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Token not found in request");
        }
        String token = authHeader.substring(7);

        try {
            return User.builder()
                    .userId(jwtUtil.extractUserId(token))
                    .email(jwtUtil.extractUsername(token))
                    .role(Role.valueOf(jwtUtil.extractUserRole(token)))
                    .build();
        } catch (Exception e) {
            throw new AccessDeniedException("Invalid token claims: " + e.getMessage());
        }
    }

    /**
     * Like {@link #current()} but returns {@code null} instead of throwing when
     * no authenticated user is available. Safe to use for best-effort audit logging
     * where a missing principal should not abort the main operation.
     */
    public User currentOrNull() {
        try {
            return current();
        } catch (AccessDeniedException e) {
            return null;
        }
    }

    /**
     * Enforces that the caller owns the resource (whose owner is {@code ownerUserId}).
     * ADMIN, TRAVEL_AGENT and FINANCE_OFFICER bypass the check; everyone else is
     * restricted to acting only on their own resources.
     */
    public void assertCanActAs(Long ownerUserId) {
        User caller = current();
        if (caller.getRole() == Role.ADMIN || caller.getRole() == Role.TRAVEL_AGENT
                || caller.getRole() == Role.FINANCE_OFFICER) {
            return;
        }
        if (!caller.getUserId().equals(ownerUserId)) {
            throw new AccessDeniedException("You can only access your own resources");
        }
    }
}
