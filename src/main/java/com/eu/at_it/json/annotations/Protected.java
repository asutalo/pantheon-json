package com.eu.at_it.json.annotations;

import com.eu.at_it.json.validation.HeaderValidation;
import com.eu.at_it.json.validation.ParamValidation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation specifies that the field cannot be sent back to unauthorised users, and can only be updated by authorised users
 * todo actually start doing the validation as now no one can get it...
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Protected {
    Class<? extends HeaderValidation>[] headerValidation() default {};

    Class<? extends ParamValidation>[] paramValidation() default {};
}