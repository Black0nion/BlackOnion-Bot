package com.github.black0nion.blackonionbot.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CustomPermission {

	ADMIN,
	DEVELOPER(ADMIN), 

	SET_ACTIVITY(DEVELOPER),
	RELOAD(DEVELOPER);

	private final List<CustomPermission> links = new ArrayList<>();

	/**
	 * <pre>
	 * {@code
	 * ADMIN
	 * DEVELOPER(ADMIN),
	 * 
	 * SET_STATUS(DEVELOPER);
	 * }
	 * </pre>
	 * Explanation: SET_STATUS is <i>INCLUDED in</i> DEVELOPER. DEVELOPERs also have the permission SET_STATUS, and because DEVELOPER has the permission ADMIN admins can also use it
	 * @param includedIn the minimum permission requirement.
	 */
	private CustomPermission(final CustomPermission... includedIn) {
		this.links.addAll(Arrays.asList(includedIn));
	}

	public static List<CustomPermission> parse(final String... input) {
		return Arrays.asList(input).stream().map(perm -> valueOf(perm.toUpperCase())).collect(Collectors.toList());
	}
	
	public static List<CustomPermission> parse(final List<String> input) {
		return input.stream().map(perm -> valueOf(perm.toUpperCase())).collect(Collectors.toList());
	}
	
	/**
	 * Recursivesly searches through the hierarchy to check if a user has permission
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(final CustomPermission permission) {
		// TODO: recursively search
		return true;
	}
}