package com.github.black0nion.blackonionbot.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CustomPermission {

    ADMIN, DEVELOPER(ADMIN), ALPHA, BETA(ADMIN, ALPHA),

    SET_ACTIVITY(DEVELOPER), RELOAD(BETA, DEVELOPER);

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
     *
     * Explanation: SET_STATUS is <i>INCLUDED in</i> DEVELOPER. DEVELOPERs also have
     * the permission SET_STATUS, and because DEVELOPER has the permission ADMIN
     * admins can also use it
     *
     * @param includedIn the minimum permission requirement.
     */
    private CustomPermission(final CustomPermission... includedIn) {
	this.links.addAll(Arrays.asList(includedIn));
    }

    public static List<CustomPermission> parse(final String... input) {
	return Arrays.asList(input).stream().map(perm -> {
	    try {
		return valueOf(perm.toUpperCase());
	    } catch (final Exception ignored) {
		return null;
	    }
	}).filter(perm -> perm != null).collect(Collectors.toList());
    }

    public static List<CustomPermission> parse(final List<String> input) {
	return input.stream().map(perm -> {
	    try {
		return valueOf(perm.toUpperCase());
	    } catch (final Exception ignored) {
		return null;
	    }
	}).filter(perm -> perm != null).collect(Collectors.toList());
    }

    public static boolean hasRights(final CustomPermission requiredPermission, final List<CustomPermission> permissionsDieErHat) {
	if (permissionsDieErHat.contains(requiredPermission)) return true;
	if (permissionsDieErHat.size() != 0) {
	    for (final CustomPermission perm : requiredPermission.links) {
		final boolean hasRights = hasRights(perm, permissionsDieErHat);
		if (hasRights) return true;
	    }
	}
	return false;
    }
}