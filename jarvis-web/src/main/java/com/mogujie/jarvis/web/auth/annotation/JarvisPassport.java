package com.mogujie.jarvis.web.auth.annotation;

import com.mogu.bigdata.admin.passport.conf.ResultType;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JarvisPassport {
    JarvisAuthType[] authTypes();
    ResultType resultType() default ResultType.page;
    boolean isMenu() default true;
    boolean needCheck() default true;
}