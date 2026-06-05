package com.cts.config;

import com.cts.entity.User;
import com.cts.enums.Role;
import com.cts.repository.UserRepository;

import lombok.AllArgsConstructor;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Resolves the currently authenticated user from the security context and
 * enforces ownership rules. The JWT principal only carries the email, so the
 * backing {@link User} is loaded by email to obtain the userId and role.
 */
@Component
@AllArgsConstructor
public class AuthenticatedUserProvider {

    private final UserRepository userRepository;

    /** The User backing the current JWT, resolved by email from the principal. */
    public User current() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails userDetails)) {
            throw new AccessDeniedException("No authenticated user in context");
        }
        User user = userRepository.findByEmail(userDetails.getUsername());
        if (user == null) {
            throw new AccessDeniedException("Authenticated user no longer exists");
        }
        return user;
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
