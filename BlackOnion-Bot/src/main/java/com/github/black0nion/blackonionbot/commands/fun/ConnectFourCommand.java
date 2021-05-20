package com.github.black0nion.blackonionbot.commands.fun;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.getTranslatedString;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFour;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFourGameManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ConnectFourCommand implements Command {
	
	@Override
	public String[] getCommand() {
		return new String[] { "connect4", "connectfour", "viergewinnt", "4gewinnt" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		if (message.getMentionedUsers().size() != 0) {
			User challenged = message.getMentionedUsers().get(0);
			if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
				message.reply(EmbedUtils.getDefaultErrorEmbed(author).addField(getTranslatedString("errorcantplayagainst", author, guild).replace("%enemy%", (challenged.isBot() ? getTranslatedString("bot", author, guild) : getTranslatedString("yourself", author, guild))), getTranslatedString("nofriends", author, guild), false).build()).queue();
				return;
			}
			if (ConnectFourGameManager.isIngame(author.getId()) || ConnectFourGameManager.isIngame(challenged.getId())) {
	    		  message.reply(EmbedUtils.getDefaultErrorEmbed(author).addField(getTranslatedString("alreadyingame", author, guild), getTranslatedString("nomultitasking", author, guild), false).build()).queue();
	    		  return;
	    	}
			message.reply(getTranslatedString("c4_askforaccept", author, guild).replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + getTranslatedString("answerwithyes", author, guild)).queue();
			CommandBase.waiter.waitForEvent(MessageReceivedEvent.class, 
					(event) -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), 
					event -> {
				  if (!event.getAuthor().isBot() && event.getAuthor().getId().equals(challenged.getId())) {
				      if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
				    	  message.reply(EmbedUtils.getDefaultSuccessEmbed(event.getAuthor()).addField(getTranslatedString("challengeaccepted", event.getAuthor(), guild), getTranslatedString("playingagainst", event.getAuthor(), guild).replace("%challenger%", author.getName()), false).build()).queue();
				    	  
				    	  //ANGENOMMEN
				    	  ConnectFour game = ConnectFourGameManager.createGame(channel, author, challenged);
				    	  rerun(game, e.getChannel());
				      } else if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
				    	  message.reply(EmbedUtils.getDefaultErrorEmbed(event.getAuthor()).setTitle(getTranslatedString("declined", event.getAuthor(), guild)).addField(getTranslatedString("challengedeclined", event.getAuthor(), guild), getTranslatedString("arentyoubraveenough", event.getAuthor(), guild), false).build()).queue();
				    	  return;
				      } else {
				    	  message.reply(EmbedUtils.getDefaultErrorEmbed(event.getAuthor()).addField(getTranslatedString("challengedeclined", event.getAuthor(), guild), getTranslatedString("answerwithyes", event.getAuthor(), guild), false).build()).queue();
				    	  return;
				      }
				  }
				}, 1, TimeUnit.MINUTES, () -> message.reply(EmbedUtils.getDefaultErrorEmbed(challenged).addField(getTranslatedString("timeout", challenged, guild), getTranslatedString("tooktoolong", author, guild), false).build()).queue());
		} else {
			message.reply(EmbedUtils.getDefaultErrorEmbed(author).addField(getTranslatedString("nousermentioned", author, guild), getTranslatedString("inputusertoplayagainst", author, guild), false).build()).queue();
			return;
		}
	}
	
	public void rerun(ConnectFour game, TextChannel channel) {
		CommandBase.waiter.waitForEvent(MessageReceivedEvent.class, 
	  			(answerEvent) -> game.isPlayer(answerEvent.getAuthor().getId()),
	  			(answerEvent) -> {
	  				final String msg = answerEvent.getMessage().getContentRaw();
	  				final User author = answerEvent.getAuthor();
	  				final Guild guild = answerEvent.getGuild();
	  				answerEvent.getMessage().delete().queue();
	  				if (Utils.equalsOneIgnoreCase(msg, "exit", "stop", "cancel", "leave")) {
	  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle(getTranslatedString("gaveup", author, guild)).addField(Utils.removeMarkdown(getTranslatedString("usergaveup", author, guild).replace("%user%", author.getAsMention())), getTranslatedString("sadloose", author, guild), false).build()).queue();
	  					ConnectFourGameManager.deleteGame(game);
	  					return;
	  				} else if (!author.getId().equals(game.currentUser == FieldType.X ? game.getPlayerX() : game.getPlayerY())) {
  						game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle(getTranslatedString("connectfour", author, guild) + " | " + getTranslatedString("currentplayer", author, guild) + " "  + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslatedString("currentstate", author, guild), game.getField(), false).setDescription(getTranslatedString("wrongturn", author, guild)).build()).queue();
  						rerun(game, channel);
  						return;
  					} else if (game.isValidInput(msg)) {
	  					Map.Entry<Integer, Integer> coords = game.getCoordinatesFromString(msg);
	  					FieldType[][] temp = new FieldType[ConnectFourGameManager.Y][ConnectFourGameManager.X];
	  					System.arraycopy(game.getfield(), 0, temp, 0, game.getfield().length);
	  					if (temp[coords.getKey()][coords.getValue()] != FieldType.EMPTY) {
	  						game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle(getTranslatedString("connectfour", author, guild) + " | " + getTranslatedString("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslatedString("currentstate", author, guild), game.getField(), false).setDescription(getTranslatedString("fieldoccopied", author, guild)).build()).queue();
	  						game.nextUser();
	  						rerun(game, channel);
	  						return;
	  					}
	  					
	  					temp[coords.getKey()][coords.getValue()] = game.currentUser;
	  					game.setfield(temp);
	  					game.nextUser();
	  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle(getTranslatedString("connectfour", author, guild) + " | " + getTranslatedString("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslatedString("currentstate", author, guild), game.getField(), false).build()).queue();

		  				if (game.getWinner() != FieldType.EMPTY) {
		  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().addField("WE HAVE A WINNER!", "And the winner is....\n" + Utils.removeMarkdown((game.getWinner() == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName())) + "!", false).build()).queue();
		  					ConnectFourGameManager.deleteGame(game);
		  					return;
		  				}
	  				} else {
	  					game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle(getTranslatedString("connectfour", author, guild) + " | " + getTranslatedString("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslatedString("currentstate", author, guild), game.getField(), false).setDescription(getTranslatedString("wronginput", author, guild)).build()).queue();
	  				}
	  				rerun(game, channel);
	  				return;
	  			}, 
	  		1, TimeUnit.MINUTES, () -> {game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().addField(LanguageSystem.getTranslatedString("timeout", channel.getGuild()), LanguageSystem.getTranslatedString("tooktoolong", channel.getGuild()), false).build()).queue(); ConnectFourGameManager.deleteGame(game); return;}
	  	);
	}
	
	@Override
	public Category getCategory() {
		return Category.FUN;
	}
}