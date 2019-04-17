package com.wildbeeslabs.sensiblemetrics.supersolr.annotation;

import com.wildbeeslabs.sensiblemetrics.supersolr.enumeration.VersionType;

import javax.validation.Constraint;
import javax.validation.OverridesAttribute;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Min;
import java.lang.annotation.*;

/**
 * Api version constraint
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Min(0)
@ReportAsSingleViolation
@Constraint(validatedBy = {})
public @interface ApiVersion {

    String message() default "{ApiVersion.message}";

    VersionType value() default VersionType.DEFAULT;

    @OverridesAttribute(
        constraint = Min.class,
        name = "value"
    )
    int minor() default 0;

    @OverridesAttribute(
        constraint = Min.class,
        name = "value"
    )
    int major() default 0;

    @OverridesAttribute(
        constraint = Min.class,
        name = "value"
    )
    int patch() default 0;
}
