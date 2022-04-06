package com.github.black0nion.blackonionbot.misc;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
/**
 * Annotate a Method with this annotation and it will automatically get
 * registered as a valid config setter for the
 * {@link com.github.black0nion.blackonionbot.commands.bot.ConfigCommand}!
 *
 * @author _SIM_
 */
public @interface ConfigSetter {
	String key();

	String[] argumentsDescription();

	Permission[] requiredPermissions() default {};

	boolean nullable() default false;
}
