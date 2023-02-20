package com.github.black0nion.blackonionbot.wrappers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class StartsWithLinkedList extends LinkedList<String> {

	public StartsWithLinkedList() {
		super();
	}

	public StartsWithLinkedList(Collection<String> initial) {
		super(initial);
	}

	public List<String> getElementsStartingWith(String prefix) {
		return getElementsStartingWith(prefix, false);
	}

	public List<String> getElementsStartingWith(String prefix, boolean ignoreCase) {
		if (ignoreCase) prefix = prefix.toLowerCase();
		List<String> result = new LinkedList<>();
		for (String element : this) {
			if ((ignoreCase ? element.toLowerCase() : element).startsWith(prefix)) {
				result.add(element);
			}
		}
		return result;
	}
}
