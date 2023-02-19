package com.github.black0nion.blackonionbot.misc.enums;

import java.util.*;
import java.util.stream.Collectors;

public enum CustomPermission {

	ADMIN, DEVELOPER(ADMIN), MODERATOR(DEVELOPER), ALPHA(ADMIN), BETA(ADMIN, ALPHA),

	SET_ACTIVITY(DEVELOPER), RELOAD(DEVELOPER), BAN_USAGE(MODERATOR);

	private final List<CustomPermission> links = new ArrayList<>();
	private final String name;

	CustomPermission(String name) {
		this.name = name;
	}

	/**
	 * <pre>
	 * {@code
	 * ADMIN
	 * DEVELOPER(ADMIN),
	 *
	 * SET_STATUS(DEVELOPER);
	 * }
	 * </pre>
	 * <p>
	 * Explanation: SET_STATUS is <i>INCLUDED in</i> DEVELOPER. DEVELOPERs also have
	 * the permission SET_STATUS, and because DEVELOPER has the permission ADMIN
	 * admins can also use it
	 *
	 * @param includedIn the minimum permission requirement.
	 */
	CustomPermission(CustomPermission... includedIn) {
		this.name = name().toLowerCase().replace('_', ' ');
		this.links.addAll(Arrays.asList(includedIn));
	}

	/**
	 * <pre>
	 * {@code
	 * ADMIN
	 * DEVELOPER(ADMIN),
	 *
	 * SET_STATUS(DEVELOPER);
	 * }
	 * </pre>
	 * <p>
	 * Explanation: SET_STATUS is <i>INCLUDED in</i> DEVELOPER. DEVELOPERs also have
	 * the permission SET_STATUS, and because DEVELOPER has the permission ADMIN
	 * admins can also use it
	 *
	 * @param includedIn the minimum permission requirement.
	 */
	CustomPermission(String name, final CustomPermission... includedIn) {
		this.name = name;
		this.links.addAll(Arrays.asList(includedIn));
	}

	/**
	 * Parses a semicolon separated list of permissions into an array of permissions
	 */
	public static CustomPermission[] parseListToArray(String permissionString) {
		return Arrays.stream(permissionString.split(";"))
			.map(CustomPermission::valueOf)
			.toArray(CustomPermission[]::new);
	}

	/**
	 * Parses a semicolon separated list of permissions into a list of permissions
	 */
	public static List<CustomPermission> parseListToList(String permissionString) {
		if (permissionString == null || permissionString.isEmpty()) {
			return new ArrayList<>();
		}
		return Arrays.stream(permissionString.split(";"))
			.map(CustomPermission::parse)
			.filter(Objects::nonNull)
			.collect(Collectors.toList()); // NOSONAR we need a mutable list
	}

	public String getName() {
		return name;
	}

	public static CustomPermission parse(final String input) {
		try {
			return valueOf(input.toUpperCase(Locale.ROOT));
		} catch (Exception ignored) {
			return null;
		}
	}

	public static List<CustomPermission> parse(final List<String> input) {
		return input.stream().map(perm -> {
			try {
				return valueOf(perm.toUpperCase());
			} catch (final Exception ignored) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static boolean hasRights(final CustomPermission requiredPermission, final List<CustomPermission> permissions) {
		if (permissions.contains(requiredPermission)) return true;
		if (!permissions.isEmpty()) {
			for (final CustomPermission perm : requiredPermission.links) {
				final boolean hasRights = hasRights(perm, permissions);
				if (hasRights) return true;
			}
		}
		return false;
	}

	public static boolean hasRights(final CustomPermission[] requiredPermissions, final CustomPermission[] permissions) {
		return Arrays.stream(requiredPermissions).allMatch(perm -> hasRights(perm, Arrays.asList(permissions)));
	}
}
