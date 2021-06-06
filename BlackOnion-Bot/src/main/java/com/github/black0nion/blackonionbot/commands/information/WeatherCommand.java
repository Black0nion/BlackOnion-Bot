package com.github.black0nion.blackonionbot.commands.information;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONObject;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class WeatherCommand extends Command {
	
	public WeatherCommand() {
		this.setCommand("weather")
			.setSyntax("<City Name>")
			.setRequiredArgumentCount(1);
	}

	@Override
	public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
		final String query = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
		try {
			final JSONObject weather = getWeather(query);
			final JSONObject sys = weather.getJSONObject("sys");
			final JSONObject main = weather.getJSONObject("main");
			final Date sunrise = new Date(sys.getInt("sunrise") * 1000L);
			final Date sunset = new Date(sys.getInt("sunset") * 1000L);
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); 
			sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); 
			final JSONObject weatherObject = weather.getJSONArray(("weather")).getJSONObject(0);
			cmde.reply(cmde.success()
					.setThumbnail("http://openweathermap.org/img/w/" + weatherObject.getString("icon") + ".png")
					.setTitle(LanguageSystem.getTranslation("weatherfor", author, guild) + " " + weather.getString("name"), "https://openweathermap.org")
					.addField(LanguageSystem.getTranslation("weather", author, guild) + ": ", weatherObject.getString("main"), true)
					.addField("temperature",  main.get("temp_min") + "� to " + main.get("temp_max") + "�", true)
					.addField("humidity",  main.get("humidity") + "%", true)
					.addField("windspeed", weather.getJSONObject("wind").get("speed") + " km/h", true)
					.addField("country", Utils.getCountryFromCode(sys.getString("country")) + " (" + sys.get("country") + ")", true)
					.addField("sunrise", sdf.format(sunrise), false)
					.addField("sunset", sdf.format(sunset), false));
			return;
		} catch (final IOException ex) {
			cmde.error("unknowncity",  query);
			return;
		}
	}

	public static JSONObject getWeather(final String query) throws IOException {
		final URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + query
				+ "&units=metric&appid=9f5fe633ea9efc307ea6629f3a12bd6c");
		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		final StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null)
			content.append(inputLine);
		in.close();
		con.disconnect();
		return new JSONObject(content.toString());
	}
}