package io.hoon.springtoyprojectbasic.aop.trace.logaspect;

import io.hoon.springtoyprojectbasic.aop.trace.TraceStatus;
import io.hoon.springtoyprojectbasic.aop.trace.logtrace.LogTrace;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class LogTraceAspect {

    private final LogTrace logTrace;

    public LogTraceAspect(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Around("execution(* io.hoon.springtoyprojectbasic.controller..*(..)) " +
            "|| execution(* io.hoon.springtoyprojectbasic.service..*(..)) " +
            "|| execution(* io.hoon.springtoyprojectbasic.repository..*(..))") //pointcut
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable { //advice
        TraceStatus status = null;
        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);
            //target 호출
            Object result = joinPoint.proceed(); //실제 호출 대상
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }


}
