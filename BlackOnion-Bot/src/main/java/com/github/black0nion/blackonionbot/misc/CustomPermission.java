package com.github.black0nion.blackonionbot.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

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
	 * @param links the minimum permission requirement.
	 */
	private CustomPermission(CustomPermission... links) {
		this.links.addAll(Arrays.asList(links));
	}

	public static List<CustomPermission> parse(String... input) {
		return Arrays.asList(input).stream().map(perm -> valueOf(perm.toUpperCase())).collect(Collectors.toList());
	}
	
	public static List<CustomPermission> parse(List<String> input) {
		return input.stream().map(perm -> valueOf(perm.toUpperCase())).collect(Collectors.toList());
	}
	
	/**
	 * Recursivesly searches through the hierarchy to check if a user has permission
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(BlackUser user, CustomPermission permission) {
		if (user.getPermissions().contains(permission)) {
            return true;
        } else {
        	Iterable<CustomPermission> i = user.getPermissions();
            return Iterables.any(i, new Predicate<CustomPermission>() {
                @Override 
                public boolean apply(CustomPermission s) {
                    return s.hasPermission(user, permission);
                }
            });            
        }
	}
}