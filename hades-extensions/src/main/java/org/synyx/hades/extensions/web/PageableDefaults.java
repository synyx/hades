package org.synyx.hades.extensions.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.synyx.hades.domain.Pageable;


/**
 * Annotation to set defaults when injecting a {@link Pageable} into a
 * controller method.
 * 
 * @author Marc Kannegiesser
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PageableDefaults {

    /**
     * The default-size the injected {@link Pageable} should get if no
     * corresponding parameter defined in request (default is 10).
     */
    int value() default 10;


    /**
     * The default-pagenumber the injected {@link Pageable} should get if no
     * corresponding parameter defined in request (default is 0).
     */
    int pageNumber() default 0;

}
