package com.mogujie.jarvis.web.auth.annotation;

import com.mogu.bigdata.admin.common.passport.conf.ResultType;
import com.mogujie.jarvis.web.auth.conf.JarvisAuthType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JarvisPassport {
    JarvisAuthType[] authTypes();
    ResultType resultType() default ResultType.page;
}
