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

import com.github.black0nion.blackonionbot.systems.dashboard.DashboardCategory;

/**
 * @author _SIM_ Date: 27.05.2021 Copyright 2021 BlackOnion Class Name:
 *         DashboardValue
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public abstract @interface DashboardValue {
    public String prettyName();

    public String id();

    public DashboardCategory category();

    public boolean nullable() default false;

    public boolean premiumFeature() default false;
}