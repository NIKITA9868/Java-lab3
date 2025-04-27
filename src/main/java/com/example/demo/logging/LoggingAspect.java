package com.example.demo.logging;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger("ApplicationLog");

    @Around("execution(* com.example.demo.service..*(..)) "
            + "|| execution(* com.example.demo.controller..*(..)) "
            + "|| execution(* com.example.demo.repository..*(..))")
    public Object logMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Executing: {}", methodName);
        try {
            Object result = joinPoint.proceed();
            logger.info("Successfully executed: {}", methodName);
            return result;
        } catch (Exception e) {
            logger.error("Error in method: {}", methodName, e);
            throw e;
        }
    }
}
