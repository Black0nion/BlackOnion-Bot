package com.github.black0nion.blackonionbot.systems;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.Reloadable;
import com.github.black0nion.blackonionbot.utils.Document;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BirthdaySystem {

	public static HashMap<String, String> birthdays = new HashMap<String, String>();
	
	public static void init() {
		reload();
		final String guildID = "756810598158696458";
		final String roleID = "776341810539659274";
		//String channelID = "";
		
		final SimpleDateFormat df = new SimpleDateFormat("MM-dd");
		
		final Thread t = new Thread(() -> {
			while (true)
				try {
					try {
						ValueManager.getLong("bdayDelay");
					} catch (final NumberFormatException ex) {
						ValueManager.save("bdayDelay", 1000);
					}
					Thread.sleep(ValueManager.getLong("bdayDelay"));
					final String current = df.format(new Date());
					try { 
						for (final Map.Entry<String, String> bdays1 : birthdays.entrySet())
							if (ValueManager.get("bday") != null && !current.equalsIgnoreCase(bdays1.getKey())) {
								Bot.jda.getGuildById(guildID).removeRoleFromMember(ValueManager.getString("bday"), Bot.jda.getGuildById(guildID).getRoleById(roleID)).queue(); 
								ValueManager.remove("bday");
							}
					} catch (final Exception e1) {
						e1.printStackTrace();
					}
					for (final Map.Entry<String, String> bdays2 : birthdays.entrySet())
						if (current.equalsIgnoreCase(bdays2.getKey())) {
							Bot.jda.getGuildById(guildID).addRoleToMember(bdays2.getValue(), Bot.jda.getGuildById(guildID).getRoleById(roleID)).queue();
							ValueManager.save("bday", bdays2.getValue());
						}
				} catch (final Exception e2) {
					e2.printStackTrace();
				}
		});
		t.setName("BDayThread");
		t.start();
	}
	
	@Reloadable("birthday")
	public static void reload() {
		final File file = new File("files", "birthdays.json");
		try {
			file.createNewFile();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		final Document doc = Document.loadDocument(file);
		final JsonElement element = JsonParser.parseString(doc.convertToJson());
		final JsonObject obj = element.getAsJsonObject();
		final Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
		birthdays.clear();
		for (final Map.Entry<String, JsonElement> entry : entries)
			birthdays.put(entry.getKey(), entry.getValue().getAsString());
	}

}
