package com.github.black0nion.blackonionbot.utils;

import com.github.black0nion.blackonionbot.systems.reload.Reloadable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SeparationClassNamerTest {

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
		assertEquals(NoInterfaces.class, SeparationClassNamer.getInstanceClass(new NoInterfaces()));
		assertEquals(NoInterfaces.class, SeparationClassNamer.getInstanceClass(NoInterfaces.class));
	}

	@Test
	void test_getInstanceClass_onlyInterface() {
		assertEquals(Interface.class, SeparationClassNamer.getInstanceClass(new OnlyInterface()));
		assertEquals(Interface.class, SeparationClassNamer.getInstanceClass(OnlyInterface.class));
	}

	@Test
	void test_getInstanceClass_onlyReloadable() {
		assertEquals(OnlyReloadable.class, SeparationClassNamer.getInstanceClass(new OnlyReloadable()));
		assertEquals(OnlyReloadable.class, SeparationClassNamer.getInstanceClass(OnlyReloadable.class));
	}

	@Test
	void test_getInstanceClass_reloadableAndInterface() {
		assertEquals(Interface.class, SeparationClassNamer.getInstanceClass(new ReloadableAndInterface()));
		assertEquals(Interface.class, SeparationClassNamer.getInstanceClass(ReloadableAndInterface.class));
	}
}
