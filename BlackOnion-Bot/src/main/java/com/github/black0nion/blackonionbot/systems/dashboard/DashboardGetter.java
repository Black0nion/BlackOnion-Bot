/**
 * Date: 27.05.2021
 * Copyright 2021 BlackOnion
 * File: DashboardGetter.java
 *
 * @author: _SIM_
 */
package com.github.black0nion.blackonionbot.systems.dashboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Date: 27.05.2021
 * Copyright 2021 BlackOnion
 * Class Name: DashboardGetter
 *
 * @author _SIM_
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public abstract @interface DashboardGetter {
    /**
     * How this works: Make a DashboardSetter and make properties for it. Then you make a
     * DashboardGetter and set the id to the id of the previously created DashboardSetter
     *
     * @return The id of the Getter
     */
    public String value();
}