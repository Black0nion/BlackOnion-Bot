/**
 * Author: _SIM_
 * Date: 27.05.2021
 * Copyright 2021 BlackOnion
 * File: DashboardValue.java
 */
package com.github.black0nion.blackonionbot.systems.dashboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.black0nion.blackonionbot.systems.dashboard.sections.Category;

/**
 * Date: 27.05.2021
 * Copyright 2021 BlackOnion
 * Class Name: DashboardReadonlyGetter
 *
 * @author _SIM_
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public abstract @interface DashboardReadonlyGetter {
    public String prettyName();

    public String id();

    public Category category();
}