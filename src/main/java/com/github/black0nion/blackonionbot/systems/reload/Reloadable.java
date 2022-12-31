package com.github.black0nion.blackonionbot.systems.reload;

import com.github.black0nion.blackonionbot.utils.SeparationClassNamer;

public interface Reloadable {
	default String getReloadName() {
		return SeparationClassNamer.getInstanceClass(this).getSimpleName();
	}

	void reload();
}
