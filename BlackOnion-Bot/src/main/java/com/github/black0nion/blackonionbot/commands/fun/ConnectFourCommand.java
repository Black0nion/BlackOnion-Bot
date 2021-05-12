package com.github.black0nion.blackonionbot.commands.fun;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFour;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFourGameManager;
import com.github.black0nion.blackonionbot.systems.games.connectfour.FieldType;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
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
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, MessageChannel channel) {
		if (message.getMentionedUsers().size() != 0) {
			User challenged = message.getMentionedUsers().get(0);
			if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
				channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField(getTranslation("c4_errorcantplayagainst", author, guild).replace("%enemy%", (challenged.isBot() ? getTranslation("c4_bot", author, guild) : getTranslation("c4_yourself", author, guild))), getTranslation("c4_nofriends", author, guild), false).build()).queue();
				return;
			}
			if (ConnectFourGameManager.isIngame(author.getId()) || ConnectFourGameManager.isIngame(challenged.getId())) {
	    		  channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField(getTranslation("c4_alreadyingame", author, guild), getTranslation("c4_nomultitasking", author, guild), false).build()).queue();
	    		  return;
	    	}
			channel.sendMessage(getTranslation("c4_askforaccept", author, guild).replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + getTranslation("c4_answerwithyes", author, guild)).queue();
			CommandBase.waiter.waitForEvent(MessageReceivedEvent.class, 
					(event) -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), 
					event -> {
				  if (!event.getAuthor().isBot() && event.getAuthor().getId().equals(challenged.getId())) {
				      if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
				    	  
				    	  channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(event.getAuthor()).addField(getTranslation("challengeaccepted", event.getAuthor(), guild), getTranslation("c4_playingagainst", event.getAuthor(), guild).replace("%challenger%", author.getAsMention()), false).build()).queue();
				    	  
				    	  //ANGENOMMEN
				    	  ConnectFour game = ConnectFourGameManager.createGame(channel, author.getId(), author.getName(), challenged.getId(), challenged.getName());
				    	  rerun(game, e.getChannel());
				      } else if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
				    	  channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor()).setTitle(getTranslation("declined", event.getAuthor(), guild)).addField(getTranslation("challengedeclined", event.getAuthor(), guild), getTranslation("arentyoubraveenough", event.getAuthor(), guild), false).build()).queue();
				    	  return;
				      } else {
				    	  channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor()).addField(getTranslation("challengedeclined", event.getAuthor(), guild), getTranslation("c4_answerwithyes", event.getAuthor(), guild), false).build()).queue();
				    	  return;
				      }
				  }
				}, 1, TimeUnit.MINUTES, () -> channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(challenged).addField(getTranslation("timeout", challenged, guild), getTranslation("tooktoolong", author, guild), false).build()).queue());
		} else {
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField(getTranslation("nousermentioned", author, guild), getTranslation("inputusertoplayagainst", author, guild), false).build()).queue();
			return;
		}
	}
	
	public void rerun(ConnectFour game, TextChannel channel) {
		CommandBase.waiter.waitForEvent(MessageReceivedEvent.class, 
	  			(answerEvent) -> game.isPlayer(answerEvent.getAuthor().getId()),
	  			(answerEvent) -> {
	  				final String msg = answerEvent.getMessage().getContentRaw();
	  				User author = answerEvent.getAuthor();
	  				Guild guild = answerEvent.getGuild();
	  				answerEvent.getMessage().delete().queue();
	  				if (msg.equalsIgnoreCase("exit") || msg.equalsIgnoreCase("stop") || msg.equalsIgnoreCase("cancel") || msg.equalsIgnoreCase("leave")) {
	  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle(getTranslation("gaveup", author, guild)).addField(Utils.removeMarkdown(getTranslation("usergaveup", author, guild).replace("%user%", answerEvent.getAuthor().getName())), getTranslation("sadloose", author, guild), false).build()).queue();
	  					ConnectFourGameManager.deleteGame(game);
	  					return;
	  				} else if (!answerEvent.getAuthor().getId().equals(game.currentUser == FieldType.X ? game.getPlayerX() : game.getPlayerY())) {
  						game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle(getTranslation("connectfour", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " "  + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY()))).addField(getTranslation("currentstate", author, guild), game.getField(), false).setDescription(getTranslation("c4_wrongturn", author, guild)).build()).queue();
  						rerun(game, channel);
  						return;
  					} else if (game.isValidInput(msg)) {
	  					Map.Entry<Integer, Integer> coords = game.getCoordinatesFromString(msg);
	  					FieldType[][] temp = new FieldType[ConnectFourGameManager.Y][ConnectFourGameManager.X];
	  					System.arraycopy(game.getfield(), 0, temp, 0, game.getfield().length);
	  					if (temp[coords.getKey()][coords.getValue()] != FieldType.EMPTY) {
	  						game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle(getTranslation("connectfour", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY()))).addField(getTranslation("currentstate", author, guild), game.getField(), false).setDescription(getTranslation("c4_fieldoccopied", author, guild)).build()).queue();
	  						game.nextUser();
	  						rerun(game, channel);
	  						return;
	  					}
	  					
	  					temp[coords.getKey()][coords.getValue()] = game.currentUser;
	  					game.setfield(temp);
	  					game.nextUser();
	  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle(getTranslation("connectfour", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY()))).addField(getTranslation("currentstate", author, guild), game.getField(), false).build()).queue();

		  				if (game.getWinner() != FieldType.EMPTY) {
		  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().addField("WE HAVE A WINNER!", "And the winner is....\n" + Utils.removeMarkdown((game.getWinner() == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY())) + "!", false).build()).queue();
		  					ConnectFourGameManager.deleteGame(game);
		  					return;
		  				}
	  				} else {
	  					game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle(getTranslation("connectfour", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY()))).addField(getTranslation("currentstate", author, guild), game.getField(), false).setDescription(getTranslation("wronginput", author, guild)).build()).queue();
	  				}
	  				rerun(game, channel);
	  				return;
	  			}, 
	  		1, TimeUnit.MINUTES, () -> {game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().addField(LanguageSystem.getTranslation("timeout", channel.getGuild()), LanguageSystem.getTranslation("tooktoolong", channel.getGuild()), false).build()).queue(); ConnectFourGameManager.deleteGame(game); return;}
	  	);
	}
	
	@Override
	public Category getCategory() {
		return Category.FUN;
	}
}