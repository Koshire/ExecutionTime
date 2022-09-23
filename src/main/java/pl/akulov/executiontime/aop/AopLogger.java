package pl.akulov.executiontime.aop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import pl.akulov.executiontime.annotation.ExecutionTime;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Level.WARNING;
import static java.util.logging.Level.parse;

@Aspect
@Component
public class AopLogger {

    @Pointcut("@annotation(pl.akulov.executiontime.annotation.LogStartStop)")
    public void startStopLog() {}

    @Pointcut("within(pl.akulov.executiontime.scheduler..*)) || @annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void schedulerOperation() {}

    @Before("schedulerOperation() || startStopLog()")
    public void logBefore(JoinPoint jp) {
        getLogger(getClassName(jp)).log(INFO, "Method: " + getMethodName(jp) + " start");
    }

    @After("schedulerOperation() || startStopLog()")
    public void logAfter(JoinPoint jp) {
        getLogger(getClassName(jp)).log(INFO, "Method: " + getMethodName(jp) + " stop");
    }

    @Around("@annotation(pl.akulov.executiontime.annotation.ExecutionTime)")
    public Object setTimer(ProceedingJoinPoint pjp) throws Throwable {
        LocalDateTime start = LocalDateTime.now();
        Object value;
        try {
            value = pjp.proceed();
        } finally {
            LocalDateTime end = LocalDateTime.now();
            getLogger(getClassName(pjp)).log(
                    extractLogLevel(start, end, pjp),
                    getLogMessage(pjp, start, end));
        }
        return value;
    }

    private Level extractLogLevel(LocalDateTime start, LocalDateTime end, ProceedingJoinPoint pjp) {
        return isDynamicLevel(pjp)
                ? extractDynamicLogLevel(Duration.between(start, end).toMillis(), pjp)
                : extractLogLevel(pjp);
    }

    private Level extractLogLevel(ProceedingJoinPoint pjp) {
        try {
            return parse(getAnnotation(pjp).level());
        } catch (Exception e) {
            return INFO;
        }
    }

    private String getLogMessage(ProceedingJoinPoint pjp, LocalDateTime start, LocalDateTime end) {
        return String.format(extractAnnotationMessage(pjp)
                        + " >> Method %s went during: %s ms, start in: %s, stop in: %s",
                getMethodName(pjp),
                Duration.between(start, end).toMillis(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS").format(start),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSS").format(end));
    }

    Level extractDynamicLogLevel(long millis, ProceedingJoinPoint pjp) {
        if (millis < extractInfoLevelLimit(pjp)) {
            return INFO;
        }
        if (millis > extractInfoLevelLimit(pjp) && millis < extractWarnLevelLimit(pjp)) {
            return WARNING;
        }
        if (millis > extractWarnLevelLimit(pjp)) {
            return SEVERE;
        }
        return INFO;
    }

    private boolean isDynamicLevel(ProceedingJoinPoint pjp) {
        return getAnnotation(pjp).dynamicLevel();
    }

    private int extractInfoLevelLimit(ProceedingJoinPoint pjp) {
        return getAnnotation(pjp).infoLimit();
    }

    private int extractWarnLevelLimit(ProceedingJoinPoint pjp) {
        return getAnnotation(pjp).warnLimit();
    }

    private String extractAnnotationMessage(ProceedingJoinPoint pjp) {
        return getAnnotation(pjp).message();
    }

    private static ExecutionTime getAnnotation(ProceedingJoinPoint pjp) {
        return ((MethodSignature) pjp.getSignature()).getMethod().getAnnotation(ExecutionTime.class);
    }


    private String getClassName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringTypeName();
    }

    private String getMethodName(ProceedingJoinPoint proceedingJoinPoint) {
        return proceedingJoinPoint.getSignature().getName()
                + "()";
    }

    private String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName()
                + "()";
    }

    private Logger getLogger(String name) {
        return Logger.getLogger(name);
    }
}
