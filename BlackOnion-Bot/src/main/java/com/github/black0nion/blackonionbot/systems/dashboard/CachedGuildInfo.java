package com.github.black0nion.blackonionbot.systems.dashboard;

import java.util.Date;
import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class CachedGuildInfo {
	
	Date refreshDate;
	List<Member> members;
	Guild guild;
	
	public CachedGuildInfo(final Guild guild) {
		this.refreshDate = new Date();
		this.guild = guild;
		this.members.addAll(guild.getMembers());
	}
	
	public Date getRefreshDate() {
		return refreshDate;
	}
	
	public Guild getGuild() {
		return guild;
	}
	
	public List<Member> getMembers() {
		return members;
	}
}
