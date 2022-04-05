package com.github.black0nion.blackonionbot.commands.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.SlashCommandBase;
import com.github.black0nion.blackonionbot.commands.SlashCommand;
import com.github.black0nion.blackonionbot.commands.SlashCommandEvent;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackMember;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatCommand extends SlashCommand {

  public CatCommand() {
    super(builder(Commands.slash("cat", "cato").addOption(OptionType.STRING, "breed",
        "The breed of the cat to show", false, true)));

    Bot.getInstance().getHttpClient()
        .sendAsync(
            HttpRequest.newBuilder(URI.create("https://api.thecatapi.com/v1/breeds")).GET()
                .header("Content-Type", "application/json").build(),
            HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body).thenAccept(response -> {
          final JSONArray responseAsJSONArray = new JSONArray(response);
          List<String> results = new ArrayList<>();
          for (int i = 0; i < responseAsJSONArray.length(); i++) {
            final JSONObject jsonObjectForThisPage = responseAsJSONArray.getJSONObject(i);
            results.add(jsonObjectForThisPage.getString("name"));
            nameToId.put(jsonObjectForThisPage.getString("name"),
                jsonObjectForThisPage.getString("id"));
          }
          this.updateAutoComplete("breed", results);
        });
  }

  private final Map<String, String> nameToId = new HashMap<>();

  @Override
  public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, BlackMember member,
      BlackUser author, BlackGuild guild, TextChannel channel) {
    String breed = e.getOption("breed", OptionMapping::getAsString);
    // check if breed is already a breed id, if it's not, check if it's a breed name
    if (breed != null && !nameToId.isEmpty() && !nameToId.containsValue(breed)) {
      breed = nameToId.get(breed);
    }
    Bot.getInstance().getHttpClient()
        .sendAsync(
            HttpRequest.newBuilder(URI.create("https://api.thecatapi.com/v1/images/search"
                + (breed != null ? "?breed_ids=" + breed : ""))).build(),
            HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body).thenAccept(response -> {
          if (response == null || response.isEmpty() || response.equalsIgnoreCase("[]")) {
            cmde.error("catnotfound", "catbreednotfound", new Placeholder("command",
                guild.getPrefix() + SlashCommandBase.getCommand(CatBreedsCommand.class).getName()));
          } else {
            final JSONArray responseAsJSONArray = new JSONArray(response);
            final JSONObject responseAsJSON = responseAsJSONArray.getJSONObject(0);
            cmde.reply(cmde.success().setTitle("Cat").setImage(responseAsJSON.getString("url")));
          }
        });
  }
}
