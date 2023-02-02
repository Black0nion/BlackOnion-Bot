package com.github.black0nion.blackonionbot.commands.slash.impl.information;

import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Utils;
import com.github.black0nion.blackonionbot.config.immutable.api.Config;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherCommand extends SlashCommand {
	private static final String CITY_NAME = "city_name";

	public WeatherCommand(Config config) {
		super(builder(Commands.slash("weather", "Used to get weather information for a city.")
			.addOption(OptionType.STRING, CITY_NAME, "The city to get weather information for.", true)), config);
	}

	@Override
	public void execute(@NotNull SlashCommandEvent cmde, @NotNull SlashCommandInteractionEvent e, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		final String city = e.getOption(CITY_NAME, OptionMapping::getAsString);
		try {
			final JSONObject weather = getWeather(city);
			final JSONObject sys = weather.getJSONObject("sys");
			final JSONObject main = weather.getJSONObject("main");
			final Date sunrise = new Date(sys.getInt("sunrise") * 1000L);
			final Date sunset = new Date(sys.getInt("sunset") * 1000L);
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
			sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
			final JSONObject weatherObject = weather.getJSONArray(("weather")).getJSONObject(0);
			cmde.reply(cmde.success()
				.setThumbnail("https://openweathermap.org/img/w/" + weatherObject.getString("icon") + ".png")
				.setTitle(cmde.getTranslation("weatherfor") + " " + weather.getString("name"), "https://openweathermap.org")
				.addField(cmde.getTranslation("weather") + ": ", weatherObject.getString("main"), true)
				.addField("temperature", main.get("temp_min") + "° to " + main.get("temp_max") + "°", true)
				.addField("humidity", main.get("humidity") + "%", true).addField("windspeed", weather.getJSONObject("wind").get("speed") + " km/h", true)
				.addField("country", Utils.getCountryFromCode(sys.getString("country")) + " (" + sys.get("country") + ")", true)
				.addField("sunrise", sdf.format(sunrise), false)
				.addField("sunset", sdf.format(sunset), false));
		} catch (final IOException ex) {
			cmde.send("unknowncity");
		}
	}

	private JSONObject getWeather(final String query) throws IOException {
		final URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + query + "&units=metric&appid=" + config.getOpenWeatherMapApiKey());
		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		final StringBuilder content = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		con.disconnect();
		return new JSONObject(content.toString());
	}
}
