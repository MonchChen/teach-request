package com.monch.teach_request.api;

import com.monch.remote.api.ApiRequest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author 陈磊.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Network {
    String url();
    int type() default ApiRequest.GET;
}
