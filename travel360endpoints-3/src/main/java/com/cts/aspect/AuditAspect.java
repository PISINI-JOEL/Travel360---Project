package com.cts.aspect;

import com.cts.annotation.Audit;
import com.cts.config.AuthenticatedUserProvider;
import com.cts.entity.User;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final AuthenticatedUserProvider authUserProvider;

    @AfterReturning(pointcut = "@annotation(auditAnnotation)", returning = "result")
    public void auditAction(JoinPoint joinPoint, Audit auditAnnotation, Object result) {
        try {
            String action = auditAnnotation.action();
            AuditEntity entityType = auditAnnotation.entity();
            LogType logType = auditAnnotation.type();

            User user = authUserProvider.currentOrNull();
            Long entityId = resolveEntityId(joinPoint, result);

            auditLogService.logAction(action, entityType, entityId, user, logType);

            log.debug("Audit log recorded: Action={}, Entity={}, ID={}", action, entityType, entityId);
        } catch (Exception e) {
            log.error("Failed to record audit log: {}", e.getMessage());
            // We don't throw the exception to avoid breaking the business transaction
        }
    }

    private Long resolveEntityId(JoinPoint joinPoint, Object result) {
        // 1. Try to find a Long in the arguments (e.g., @PathVariable id)
        Object[] args = joinPoint.getArgs();
        if (args != null) {
            Optional<Long> idFromArgs = Arrays.stream(args)
                    .filter(arg -> arg instanceof Long)
                    .map(arg -> (Long) arg)
                    .findFirst();
            if (idFromArgs.isPresent()) return idFromArgs.get();
        }

        // 2. Try to extract ID from the returned object using reflection if it's a DTO
        if (result != null) {
            try {
                // Look for common ID patterns like getBookingId, getHotelId, etc.
                for (var method : result.getClass().getDeclaredMethods()) {
                    if (method.getName().startsWith("get") && method.getName().endsWith("Id")
                        && method.getReturnType().equals(Long.class)) {
                        method.setAccessible(true);
                        return (Long) method.invoke(result);
                    }
                }
            } catch (Exception ignored) {}
        }

        return null;
    }
}
