package com.github.black0nion.blackonionbot.misc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a method with this annotation to make it run on startup.
 * The method must be static and have exactly one parameter of type {@link com.github.black0nion.blackonionbot.database.helpers.api.SQLHelperFactory}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SQLSetup {
	Class<?>[] after() default {};
}
