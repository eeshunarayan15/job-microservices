package com.micro.company.logging;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class LoggingAspect {
    //return type ,class-name.method-name(args)
    @Before("execution(* com.micro.company.service.CompanyServiceImpl.*(..))")
    public  void logMethodCall(){
        log.info("This is a method call");
    }
}
