package io.hoon.springtoyprojectbasic.config.aop;

import io.hoon.springtoyprojectbasic.aop.trace.logaspect.LogTraceAspect;
import io.hoon.springtoyprojectbasic.aop.trace.logtrace.LogTrace;
import io.hoon.springtoyprojectbasic.aop.trace.logtrace.ThreadLocalLogTrace;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AopConfig {

    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }

    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }
}
