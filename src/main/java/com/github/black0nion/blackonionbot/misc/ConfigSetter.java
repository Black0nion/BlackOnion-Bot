package com.github.black0nion.blackonionbot.misc;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import net.dv8tion.jda.api.Permission;

@Retention(RUNTIME)
@Target(METHOD)
/**
 * Annotate a Method with this annotation and it will automatically get
 * registered as a valid config setter for the
 * {@link com.github.black0nion.blackonionbot.slashcommands.bot.ConfigCommand}!
 *
 * @author _SIM_
 */
public @interface ConfigSetter {
    String key();

    String[] argumentsDescription();

    Permission[] requiredPermissions() default {};

    boolean nullable() default false;
}