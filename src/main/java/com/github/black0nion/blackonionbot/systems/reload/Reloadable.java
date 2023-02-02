package com.github.black0nion.blackonionbot.systems.reload;

import com.github.black0nion.blackonionbot.utils.ClassDescriptor;

public interface Reloadable {
	default String getReloadName() {
		return ClassDescriptor.getInstanceClass(this).getSimpleName();
	}

	void reload();
}
