package com.github.black0nion.blackonionbot.commands.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.enums.Category;
import com.github.black0nion.blackonionbot.oldcommands.Command;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WeatherCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		try {
			JSONObject weather = getWeather(query);
			JSONObject sys = weather.getJSONObject("sys");
			JSONObject main = weather.getJSONObject("main");
			Date sunrise = new Date(sys.getInt("sunrise") * 1000L);
			Date sunset = new Date(sys.getInt("sunset") * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); 
			sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); 
			JSONObject weatherObject = weather.getJSONArray(("weather")).getJSONObject(0);
			EmbedBuilder embed = EmbedUtils.getDefaultSuccessEmbed(author, guild)
					.setThumbnail("http://openweathermap.org/img/w/" + weatherObject.getString("icon") + ".png")
					.setTitle(LanguageSystem.getTranslatedString("weatherfor", author, guild) + " " + weather.getString("name"))
					.addField(LanguageSystem.getTranslatedString("weather", author, guild) + ": ", weatherObject.getString("main"), true)
					.addField(LanguageSystem.getTranslatedString("temperature", author, guild), main.get("temp_min") + "° to " + main.get("temp_max") + "°", true)
					.addField(LanguageSystem.getTranslatedString("humidity", author, guild), main.get("humidity") + "%", true)
					.addField(LanguageSystem.getTranslatedString("windspeed", author, guild), weather.getJSONObject("wind").get("speed") + " km/h", true)
					.addField(LanguageSystem.getTranslatedString("country", author, guild), Utils.getCountryFromCode(sys.getString("country")) + " (" + sys.get("country") + ")", true)
					.addField(LanguageSystem.getTranslatedString("sunrise", author, guild), sdf.format(sunrise), false)
					.addField(LanguageSystem.getTranslatedString("sunset", author, guild), sdf.format(sunset), false);
			channel.sendMessage(embed.build()).queue();
			return;
		} catch (IOException ex) {
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author, guild).addField(LanguageSystem.getTranslatedString("unknowncity", author, guild), query, false).build()).queue();
			return;
		}
	}

	public static JSONObject getWeather(String query) throws IOException {
		URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + query
				+ "&units=metric&appid=9f5fe633ea9efc307ea6629f3a12bd6c");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		con.disconnect();
		return new JSONObject(content.toString());
	}

	@Override
	public String getSyntax() {
		return "<City Name>";
	}
	
	@Override
	public int getRequiredArgumentCount() {
		return 1;
	}
	
	@Override
	public Category getCategory() {
		return Category.MISC;
	}
	
	@Override
	public String[] getCommand() {
		return new String[] {"weather"};
	}
}
