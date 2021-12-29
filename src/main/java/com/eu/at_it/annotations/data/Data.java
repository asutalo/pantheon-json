package com.eu.at_it.annotations.data;

import com.eu.at_it.service.data.DataService;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Data {
    Class<? extends DataService> servedBy();
}