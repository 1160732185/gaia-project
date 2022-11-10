// Copyright 2019 Baidu Inc. All rights reserved.

package web.gaia.gaiaproject.aop;

import java.util.ArrayList;
import java.util.List;

import io.swagger.models.auth.In;
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
    public Object monitorAround(ProceedingJoinPoint pjp) throws Throwable {

        //监控慢调用
        String methodName = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();
        long startTime = System.currentTimeMillis();
        Object re = pjp.proceed();
        long exeTime = System.currentTimeMillis() - startTime;
        if (exeTime > 3000) {
            List<String> arggs = new ArrayList<>();
            for (Object arg : args) {
                if (arg instanceof String) {
                    arggs.add((String) arg);
                } else if (arg instanceof Integer) {
                    arggs.add(String.valueOf(arg));
                }
            }
            System.out.println("慢语句" + methodName + arggs + exeTime);
        }
        return re;
    }
}
