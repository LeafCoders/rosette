package se.leafcoders.rosette.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class MethodLogger {

    @Around("execution(* se.leafcoders.rosette.controller.*.*(..))")
    public Object timeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object retVal = joinPoint.proceed();

        stopWatch.stop();

        StringBuffer logMessageStringBuffer = new StringBuffer();
        logMessageStringBuffer.append(joinPoint.getTarget().getClass().getName());
        logMessageStringBuffer.append(".");
        logMessageStringBuffer.append(joinPoint.getSignature().getName());
        logMessageStringBuffer.append("(");
        
        Object[] args = joinPoint.getArgs();
        if (args != null) {
        	boolean first = true;
        	for (Object arg : args) {
        		if (!first) {
        			logMessageStringBuffer.append(", ");
        		}
        		logMessageStringBuffer.append(arg);
    			first = false;
			}
        }
        
        logMessageStringBuffer.append(")");
        logMessageStringBuffer.append(" execution time: ");
        logMessageStringBuffer.append(stopWatch.getTotalTimeMillis());
        logMessageStringBuffer.append(" ms");

        LoggerFactory.getLogger(this.getClass()).debug(logMessageStringBuffer.toString());
        return retVal;
    }

}
