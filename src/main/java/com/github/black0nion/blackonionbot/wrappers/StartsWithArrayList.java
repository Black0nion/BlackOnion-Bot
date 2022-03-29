package com.github.black0nion.blackonionbot.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StartsWithArrayList extends ArrayList<String> {

	public StartsWithArrayList(Collection<String> initial) {
		super(initial);
	}

	public List<String> getElementsStartingWith(String prefix) {
		return getElementsStartingWith(prefix, false);
	}

	public List<String> getElementsStartingWith(String prefix, boolean ignoreCase) {
		if (ignoreCase) prefix = prefix.toLowerCase();
		List<String> result = new ArrayList<>();
		for (String element : this) {
			if ((ignoreCase ? element.toLowerCase() : element).startsWith(prefix)) {
				result.add(element);
			}
		}
		return result;
	}
}