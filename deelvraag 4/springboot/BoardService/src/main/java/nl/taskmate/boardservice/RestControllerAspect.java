package nl.taskmate.boardservice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class RestControllerAspect {

    @Around("execution(* nl.taskmate.boardservice.presentation.controllers.*.*(..))")
    public Object beforeRestControllerMethods(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object object = pjp.proceed();
        long endTime = System.currentTimeMillis();
        log.info(pjp.toShortString() + " took " + (endTime - startTime) + "ms");
        return object;
    }

}
