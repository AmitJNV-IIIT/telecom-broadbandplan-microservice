package com.excitel.config;

import com.excitel.exception.custom.DatabaseConnectionException;
import com.excitel.exception.custom.DuplicatePhoneNumberException;
import com.excitel.exception.custom.NoPlanFoundException;
import com.excitel.exception.custom.UserAccessDeniedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class AspectLogging {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("execution(* com.excitel.controller..*.*(..)) || execution(* com.excitel.serviceimpl..*.*(..))")
    public void controllerAndServiceImplMethods() {}

    @Pointcut("execution(@org.springframework.web.bind.annotation.GetMapping * com.excitel.controller..*.*(..))")
    public void getMappingMethods() {}

    @Pointcut("execution(@org.springframework.web.bind.annotation.PostMapping * com.excitel.controller..*.*(..))")
    public void postMappingMethods() {}

    @Pointcut("execution(@org.springframework.web.bind.annotation.PutMapping * com.excitel.controller..*.*(..))")
    public void putMappingMethods() {}

    @Pointcut("execution(@org.springframework.web.bind.annotation.DeleteMapping * com.excitel.controller..*.*(..))")
    public void deleteMappingMethods() {}

    // Advice for @GetMapping methods
    @Before("getMappingMethods()")
    public void logGetRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("GET request to method: {}", methodName);
    }

    @Before("postMappingMethods()")
    public void logPostRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("POST request to method: {}", methodName);
    }

    @Before("putMappingMethods()")
    public void logPutRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("PUT request to method: {}", methodName);
    }

    @Before("deleteMappingMethods()")
    public void logDeleteRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("DELETE request to method: {}", methodName);
    }

    @Before("controllerAndServiceImplMethods()")
    public void logControllerMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Calling method: {}", methodName);
    }

    @After("getMappingMethods()")
    public void afterGetRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed GET request to method: {}", methodName);
    }

    @After("postMappingMethods()")
    public void afterPostRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed POST request to method: {}", methodName);
    }

    @After("putMappingMethods()")
    public void afterPutRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed PUT request to method: {}", methodName);
    }

    @After("deleteMappingMethods()")
    public void afterDeleteRequest(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed DELETE request to method: {}", methodName);
    }

    @After("controllerAndServiceImplMethods()")
    public void afterControllerMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("Completed call to method: {}", methodName);
    }

    @AfterThrowing(pointcut = "controllerAndServiceImplMethods()", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, DatabaseConnectionException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("Error occured in database connection inside method: {}, with message: {}", methodName, exception.getMessage());
    }

    @AfterThrowing(pointcut = "controllerAndServiceImplMethods()", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, DuplicatePhoneNumberException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("Phone number already exists error occured in: {}, with message: {}", methodName, exception.getMessage());
    }

    @AfterThrowing(pointcut = "controllerAndServiceImplMethods()", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, NoPlanFoundException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("No plans found, for this method: {}, with message: {}", methodName, exception.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* com.excitel.controller..*.*(..)) || execution(* com.excitel.serviceimpl..*.*(..))", throwing = "exception")
    public void logUserAccessDeniedException(JoinPoint joinPoint, UserAccessDeniedException exception) {
        String methodName = joinPoint.getSignature().toShortString();
        logger.warn("Unauthorized access attempted in method: {}, with message: {}", methodName, exception.getMessage());
    }


}