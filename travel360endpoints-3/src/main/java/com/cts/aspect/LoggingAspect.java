package com.cts.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // ✅ Apply to controller layer AND service implementation layer
    @Pointcut("within(com.cts.controller..*) || within(com.cts.serviceimpl..*)")
    public void loggingPointcut() {
    }

    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Object[] args = joinPoint.getArgs();

        // ⚠️ Avoid logging sensitive data (like passwords)
        Object[] safeArgs = Arrays.stream(args)
                .map(arg -> (arg != null && arg.toString().toLowerCase().contains("password"))
                        ? "*****"
                        : arg)
                .toArray();

        log.info(">> Entering {}.{}() with arguments = {}",
                className,
                methodName,
                Arrays.toString(safeArgs));

        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long time = System.currentTimeMillis() - start;

            log.info("<< Exiting {}.{}() (Execution Time: {} ms)",
                    className,
                    methodName,
                    time);

            return result;

        } catch (Exception e) {
            log.error("Exception in {}.{}() : {}",
                    className,
                    methodName,
                    e.getMessage());

            throw e;
        }
    }
}