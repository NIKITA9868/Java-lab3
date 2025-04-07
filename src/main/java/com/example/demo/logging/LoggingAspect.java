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
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Логирование всех методов в контроллерах
    @Around("execution(* com.example.demo.controller..*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        logger.info("Вызов метода: {}.{}() с аргументами: {}",
                className, methodName, joinPoint.getArgs());

        try {
            Object result = joinPoint.proceed();
            logger.info("Метод {}.{}() выполнен успешно", className, methodName);
            return result;
        } catch (Exception e) {
            String errorMessage = String.format("Ошибка в методе %s.%s(): %s",
                    className, methodName, e.getMessage());
            logger.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e); // Wrap with context
        }
    }

    // Логирование всех методов в сервисах
    @Around("execution(* com.example.demo.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        logger.debug("Сервисный метод {} вызван", methodName);
        return joinPoint.proceed();
    }
}