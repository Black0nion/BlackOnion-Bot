package com.github.black0nion.blackonionbot.systems.reload;

import com.github.black0nion.blackonionbot.utils.ClassDescriptor;
import com.github.black0nion.blackonionbot.utils.ClassDescriptorTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReloadableTest extends ClassDescriptorTest {

	@Test
	void test_getReloadName() {
		Reloadable reloadable = () -> {};
		assertEquals(ClassDescriptor.getInstanceClass(reloadable).getSimpleName(), reloadable.getReloadName());
		assertEquals(ClassDescriptor.getInstanceClass(OnlyReloadable.class).getSimpleName(), new OnlyReloadable().getReloadName());
		assertEquals(ClassDescriptor.getInstanceClass(ReloadableAndInterface.class).getSimpleName(), new ReloadableAndInterface().getReloadName());
	}
}
