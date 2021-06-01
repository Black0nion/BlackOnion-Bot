/**
 * Author: _SIM_
 * Date: 27.05.2021
 * Copyright 2021 BlackOnion
 * File: DashboardValue.java
 */
package com.github.black0nion.blackonionbot.misc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author _SIM_
 * Date: 27.05.2021
 * Copyright 2021 BlackOnion
 * Class Name: DashboardValue
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public abstract @interface DashboardValue {
	public String value();
	
	/**
	 * @return true if you should be able to select the channel in the dashboard, will get sent as a long for the bot to parse!
	 */
	public boolean channelSelector() default false;
}