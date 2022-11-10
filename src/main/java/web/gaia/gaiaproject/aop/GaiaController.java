// Copyright 2019 Baidu Inc. All rights reserved.

package web.gaia.gaiaproject.aop;

/**
 * fileDesc
 *
 * @author Zixiong Gan(ganzixiong01@baidu.com)
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 盖亚服务。
 *
 * @author Huang Jiakun (huangjiakun@baidu.com).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface GaiaController {
}

