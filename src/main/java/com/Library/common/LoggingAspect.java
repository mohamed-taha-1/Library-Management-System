package com.Library.common;import lombok.extern.slf4j.Slf4j;import org.aspectj.lang.JoinPoint;import org.aspectj.lang.ProceedingJoinPoint;import org.aspectj.lang.annotation.AfterThrowing;import org.aspectj.lang.annotation.Around;import org.aspectj.lang.annotation.Aspect;import org.springframework.stereotype.Component;@Aspect@Component@Slf4jpublic class LoggingAspect {  @Around("execution(* com.Library.controllers.*.*(..))")  public Object logMethodCallAndPerformance(ProceedingJoinPoint joinPoint) throws Throwable {    long startTime = System.currentTimeMillis();    String className = joinPoint.getTarget().getClass().getSimpleName();    String methodName = joinPoint.getSignature().getName();    log.info("Executing method '{}' in class '{}'", methodName, className);    Object result = joinPoint.proceed();    long executionTime = System.currentTimeMillis() - startTime;    log.info("Method '{}' execution time: {} ms", methodName, executionTime);    return result;  }  @AfterThrowing(pointcut = "execution(* com.Library.controllers.*.*(..))", throwing = "exception")  public void logException(JoinPoint joinPoint, Throwable exception) {    String className = joinPoint.getTarget().getClass().getSimpleName();    String methodName = joinPoint.getSignature().getName();    log.error("Exception in method '{}' of class '{}': ",className ,methodName );  }}