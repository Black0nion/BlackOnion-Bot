package com.github.black0nion.blackonionbot.utils;

import com.github.black0nion.blackonionbot.systems.reload.Reloadable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassDescriptorTest { // NOSONAR dude i extend from this in another package

	private interface Interface {}

	private static class NoInterfaces {}

	private static class OnlyInterface implements Interface {}

	public static class OnlyReloadable implements Reloadable {
		@Override
		public void reload() {}
	}

	public static class ReloadableAndInterface implements Reloadable, Interface {
		@Override
		public void reload() {}
	}

	@Test
	void test_getInstanceClass_noInterface() {
		assertEquals(NoInterfaces.class, ClassDescriptor.getInstanceClass(new NoInterfaces()));
		assertEquals(NoInterfaces.class, ClassDescriptor.getInstanceClass(NoInterfaces.class));
	}

	@Test
	void test_getInstanceClass_onlyInterface() {
		assertEquals(Interface.class, ClassDescriptor.getInstanceClass(new OnlyInterface()));
		assertEquals(Interface.class, ClassDescriptor.getInstanceClass(OnlyInterface.class));
	}

	@Test
	void test_getInstanceClass_onlyReloadable() {
		assertEquals(OnlyReloadable.class, ClassDescriptor.getInstanceClass(new OnlyReloadable()));
		assertEquals(OnlyReloadable.class, ClassDescriptor.getInstanceClass(OnlyReloadable.class));
	}

	@Test
	void test_getInstanceClass_reloadableAndInterface() {
		assertEquals(Interface.class, ClassDescriptor.getInstanceClass(new ReloadableAndInterface()));
		assertEquals(Interface.class, ClassDescriptor.getInstanceClass(ReloadableAndInterface.class));
	}
}
