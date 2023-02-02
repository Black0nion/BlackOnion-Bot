package com.github.black0nion.blackonionbot.utils;

import com.github.black0nion.blackonionbot.systems.reload.Reloadable;

/**
 * Utility class that provides methods to get the class of an instance.
 * This includes the case where the instance implements {@link Reloadable}.
 */
public class ClassDescriptor {

	private ClassDescriptor() {}

	/**
	 * This method is used to get the class of the passed instance.<br>
	 * <br>
	 * Algorithm used to get the class:<br>
	 * <pre>
	 * if exactly one interface is implemented
	 *   if the interface is Reloadable, use the class of the passed instance
	 *   else, use the interface
	 * else
	 *     if the instance implements {@link Reloadable} and exactly one other interface, use that
	 *     else, use the class of the passed instance
	 * </pre>
	 *
	 * @return the class of the instance
	 */
	public static Class<?> getInstanceClass(Object instance) {
		Class<?> instanceClass = instance instanceof Class<?> clazz ? clazz : instance.getClass();
		Class<?>[] interfaces = instanceClass.getInterfaces();
		if (interfaces.length == 1) {
			return interfaces[0] == Reloadable.class ? instanceClass : interfaces[0];
		} else {
			// ignore the Reloadable interface
			if (interfaces.length == 2) {
				for (Class<?> iface : interfaces) {
					if (!iface.equals(Reloadable.class)) return iface;
				}
			}
			return instanceClass;
		}
	}
}
