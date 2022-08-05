package io.hoon.springtoyprojectbasic.aop.trace.logtrace;

import io.hoon.springtoyprojectbasic.aop.trace.TraceStatus;

public interface LogTrace {

    TraceStatus begin(String message);

    void end(TraceStatus status);

    void exception(TraceStatus status, Exception e);
}
