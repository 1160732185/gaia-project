// Copyright 2019 Baidu Inc. All rights reserved.

package web.gaia.gaiaproject.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Service;

/**
 * fileDesc
 *
 * @author Zixiong Gan(ganzixiong01@baidu.com)
 */
@Service
@Aspect
public class ExecutionAop {
    @Around("@within(web.gaia.gaiaproject.aop.GaiaController)")
    public void monitorAround(ProceedingJoinPoint pjp) throws Throwable {

        //监控慢调用
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        long startTime = System.currentTimeMillis();
        Object re = pjp.proceed();
        long exeTime = System.currentTimeMillis() - startTime;
        if (exeTime > 3000) {
            System.out.println("慢语句" + methodName + (String[])args + exeTime);
        }

    }
}
