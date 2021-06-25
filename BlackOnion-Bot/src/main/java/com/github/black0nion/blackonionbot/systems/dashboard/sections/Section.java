/**
 *
 */
package com.github.black0nion.blackonionbot.systems.dashboard.sections;

/**
 * @author _SIM_
 */
public enum Section {
    JOIN("Join", "join"), LEAVE("Leave", "leave"), LANGUAGE("Language", "language"), GUILD_TYPE("GuildType", "guild_type"), PREFIX("Prefix", "prefix"), ANTI_SPOILER_TYPE("AntiSpoiler Type", "antispoilertype"), SUGGESTIONSCHANNEL("Suggestions Channel", "suggestionschannel");

    private final String id;
    private final String name;

    private Section(final String name, final String id) {
	this.name = name;
	this.id = id;
    }

    /**
     * @return the id
     */
    public String getId() {
	return this.id;
    }

    /**
     * @return the pretty name
     */
    public String getName() {
	return this.name;
    }
}