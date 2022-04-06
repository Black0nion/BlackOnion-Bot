package com.github.black0nion.blackonionbot.misc;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate a Method with this annotation and it will automatically get
 * registered as a valid config getter for the
 * {@link com.github.black0nion.blackonionbot.commands.bot.ConfigCommand}!
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ConfigGetter {

	String key();

	Permission[] requiredPermissions() default {};

	String description() default "EMPTY";
}
