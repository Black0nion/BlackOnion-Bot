package com.github.black0nion.blackonionbot.systems.reload;

import com.github.black0nion.blackonionbot.utils.SeparationClassNamer;
import com.github.black0nion.blackonionbot.utils.SeparationClassNamerTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReloadableTest extends SeparationClassNamerTest {

	@Test
	void test_getReloadName() {
		Reloadable reloadable = () -> {};
		assertEquals(SeparationClassNamer.getInstanceClass(reloadable).getSimpleName(), reloadable.getReloadName());
		assertEquals(SeparationClassNamer.getInstanceClass(OnlyReloadable.class).getSimpleName(), new OnlyReloadable().getReloadName());
		assertEquals(SeparationClassNamer.getInstanceClass(ReloadableAndInterface.class).getSimpleName(), new ReloadableAndInterface().getReloadName());
	}
}
