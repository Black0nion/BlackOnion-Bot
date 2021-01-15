package com.github.black0nion.blackonionbot.systems;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.utils.Document;
import com.github.black0nion.blackonionbot.utils.ValueManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class BirthdaySystem {

	public static HashMap<String, String> birthdays = new HashMap<String, String>();
	
	public static void init() {
		reload();
		String guildID = "756810598158696458";
		String roleID = "776341810539659274";
		//String channelID = "";
		
		SimpleDateFormat df = new SimpleDateFormat("MM-dd");
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						try {
							ValueManager.getLong("bdayDelay");
						} catch (NumberFormatException ex) {
							ValueManager.save("bdayDelay", 1000);
						}
						Thread.sleep(ValueManager.getLong("bdayDelay"));
						String current = df.format(new Date());
						try { 
							for (Map.Entry<String, String> bdays : birthdays.entrySet()) {
								if (ValueManager.get("bday") != null && !current.equalsIgnoreCase(bdays.getKey())) {
									Bot.jda.getGuildById(guildID).removeRoleFromMember(ValueManager.getString("bday"), Bot.jda.getGuildById(guildID).getRoleById(roleID)).queue(); 
									ValueManager.remove("bday");
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						for (Map.Entry<String, String> bdays : birthdays.entrySet()) {
							if (current.equalsIgnoreCase(bdays.getKey())) {
								Bot.jda.getGuildById(guildID).addRoleToMember(bdays.getValue(), Bot.jda.getGuildById(guildID).getRoleById(roleID)).queue();
								ValueManager.save("bday", bdays.getValue());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		t.setName("BDayThread");
		t.start();
	}
	
	public static void reload() {
		File file = new File("files", "birthdays.json");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document doc = Document.loadDocument(file);
		JsonElement element = JsonParser.parseString(doc.convertToJson());
		JsonObject obj = element.getAsJsonObject();
		Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();
		birthdays.clear();
		for (Map.Entry<String, JsonElement> entry : entries) {
		    birthdays.put(entry.getKey(), entry.getValue().getAsString());
		}
	}

}
