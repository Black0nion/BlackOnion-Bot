package com.github.black0nion.blackonionbot.commands.old;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HypixelCommand extends Command {

	//private static HypixelAPI api;

	public HypixelCommand() {
//		if (ValueManager.getString("hypixelapikey") != null)
//		if (Bot.getCredentialsManager().has("hypixel"))
//			api = new HypixelAPI(UUID.fromString(Bot.getCredentialsManager().getString("hypixel")));
		this.dontAutoRegister();
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
//		if (args[1].toLowerCase().equalsIgnoreCase("stats")) {
//			api.getPlayerByName(args[2]).whenComplete((response, error) -> {
//
//                // Check if there was an API error
//                if (error != null) {
//                    error.printStackTrace();
//                    return;
//                }
//
//                // Get the player object from the response. This object stores any available information about a given player
//                JsonObject player = response.getPlayer();
//
//                if (player != null) {
//
//                    /*
//                    The player was found; print some basic info about them
//                    Not every player has all of these fields (for example, staff members don't have
//                    "mostRecentGameType"), so I added a handy little method to check if a field
//                    exists. If it doesn't, it returns "N/A" rather than throwing a
//                    NullPounterException
//                     */
//                    System.out.println("Name: " + getFieldOrNA("displayname", player));
//                    JSONObject obj = new JSONObject(player.get("Social Media"));
//                    System.out.println(obj.get("YOUTUBE"));
//                    System.out.println("UUID: " + getFieldOrNA("uuid", player));
//                    System.out.println("Network Experience: " + getFieldOrNA("networkExp", player));
//                    System.out.println("Social Media:" + player.get("socialMedia"));
//
//                   System.out.println("Most Recent Game: " + getFieldOrNA("mostRecentGameType", player));
//
//                } else {
//
//                    // If we're here, it means that Hypixel has no info on this player
//                    System.err.println("That player was not found");
//                }
//            });
//		}
	}

	 @SuppressWarnings("unused")
	private static String getFieldOrNA(final String field, final JsonObject json) {
	        final JsonElement value = json.get(field);
	        if (value != null)
				// If the field was found, return its value
	            return value.getAsString();
			else
				// Otherwise, return "N/A"
	            return "N/A";
    }

	@Override
	public Progress getProgress() {
		return Progress.PLANNED;
	}

	@Override
	public String[] getCommand() {
		return new String[] {"hypixel"};
	}

	@Override
	public Category getCategory() {
		return Category.FUN;
	}

}