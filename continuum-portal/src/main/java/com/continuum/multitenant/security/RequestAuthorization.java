package com.continuum.multitenant.security;

import java.lang.annotation.*;

/**
 * @author RK
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequestAuthorization {
}
