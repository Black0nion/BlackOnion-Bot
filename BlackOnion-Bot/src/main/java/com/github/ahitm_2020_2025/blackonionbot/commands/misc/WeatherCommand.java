package com.github.ahitm_2020_2025.blackonionbot.commands.misc;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONObject;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class WeatherCommand implements Command {

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
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
			EmbedBuilder embed = new EmbedBuilder()
					.setColor(Color.GREEN)
					.setThumbnail("http://openweathermap.org/img/w/" + weatherObject.getString("icon") + ".png")
					.setTitle("Weather for " + weather.getString("name"))
					.addField("Weather: ", weatherObject.getString("main"), true)
					.addField("Temperature", main.get("temp_min") + "° to " + main.get("temp_max") + "°", true)
					.addField("Humidity", main.get("humidity") + "%", true)
					.addField("Wind speed", weather.getJSONObject("wind").get("speed") + " km/h", true)
					.addField("Country", Utils.getCountryFromCode(sys.getString("country")) + " (" + sys.get("country") + ")", true)
					.addField("Sunrise", sdf.format(sunrise), false)
					.addField("Sunset", sdf.format(sunset), false)
					.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
					.setTimestamp(Instant.now());;
			channel.sendMessage(embed.build()).queue();
			return;
		} catch (IOException ex) {
			EmbedBuilder embed = new EmbedBuilder()
					.setColor(Color.RED)
					.setTitle("Error!")
					.addField("Unknown City:", query, true)
					.setFooter(author.getName() + author.getDiscriminator(), author.getEffectiveAvatarUrl())
					.setTimestamp(Instant.now());
			channel.sendMessage(embed.build()).queue();
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
	public String getDescription() {
		return "Gibt dir das Wetter an einem Ort aus";
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
