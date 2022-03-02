package com.github.black0nion.blackonionbot.misc;

import java.util.*;
import java.util.stream.Collectors;

public enum CustomPermission {

	ADMIN, DEVELOPER(ADMIN), MODERATOR(DEVELOPER), ALPHA(ADMIN), BETA(ADMIN, ALPHA),

	SET_ACTIVITY(DEVELOPER), RELOAD(DEVELOPER), BAN_USAGE(MODERATOR);

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
	 * <p>
	 * Explanation: SET_STATUS is <i>INCLUDED in</i> DEVELOPER. DEVELOPERs also have
	 * the permission SET_STATUS, and because DEVELOPER has the permission ADMIN
	 * admins can also use it
	 *
	 * @param includedIn the minimum permission requirement.
	 */
	CustomPermission(final CustomPermission... includedIn) {
		this.links.addAll(Arrays.asList(includedIn));
	}

	public static CustomPermission parse(final String input) {
		try {
			return valueOf(input.toUpperCase(Locale.ROOT));
		} catch (Exception ignored) {
			return null;
		}
	}

	public static List<CustomPermission> parse(final String... input) {
		return Arrays.stream(input).map(perm -> {
			try {
				return valueOf(perm.toUpperCase(Locale.ROOT));
			} catch (final Exception ignored) {
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());
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
		if (permissions.size() != 0) {
			for (final CustomPermission perm : requiredPermission.links) {
				final boolean hasRights = hasRights(perm, permissions);
				if (hasRights) return true;
			}
		}
		return false;
	}
}