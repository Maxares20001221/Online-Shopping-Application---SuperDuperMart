package com.example.superdupermart.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * 定义切点：匹配所有 controller 层的方法
     */
    @Pointcut("execution(* com.example.superdupermart.controller..*(..))")
    public void controllerMethods() {}

    /**
     * 定义切点：匹配所有 service 层的方法
     */
    @Pointcut("execution(* com.example.superdupermart.service..*(..))")
    public void serviceMethods() {}

    /**
     * 在方法执行前打印日志
     */
    @Before("controllerMethods() || serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("➡️ Entering: {} | Args: {}",
                joinPoint.getSignature().toShortString(),
                Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * 在方法执行成功后打印日志
     */
    @AfterReturning(pointcut = "controllerMethods() || serviceMethods()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info("✅ Exiting: {} | Return: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }

    /**
     * 在方法抛出异常时打印错误日志
     */
    @AfterThrowing(pointcut = "controllerMethods() || serviceMethods()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        logger.error("❌ Exception in: {} | Message: {}",
                joinPoint.getSignature().toShortString(),
                exception.getMessage(), exception);
    }

    /**
     * 计算方法执行时间
     */
    @Around("controllerMethods() || serviceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        logger.info("⏱️ Executed {} in {} ms", joinPoint.getSignature().toShortString(), duration);
        return result;
    }
}