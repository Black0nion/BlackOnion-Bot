package com.github.black0nion.blackonionbot.commands.fun;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.getTranslation;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFour;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFourGameManager;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ConnectFourCommand extends Command {
	
	public ConnectFourCommand() {
		this.setCommand("connect4", "connectfour", "viergewinnt", "4gewinnt");
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (message.getMentionedBlackUsers().size() != 0) {
			BlackUser challenged = message.getMentionedBlackUsers().get(0);
			if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("errorcantplayagainst", author, guild).replace("%enemy%", (challenged.isBot() ? getTranslation("bot", author, guild) : getTranslation("yourself", author, guild))), getTranslation("nofriends", author, guild), false).build()).queue();
				return;
			}
			if (ConnectFourGameManager.isIngame(author.getId()) || ConnectFourGameManager.isIngame(challenged.getId())) {
	    		  message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("alreadyingame", author, guild), getTranslation("nomultitasking", author, guild), false).build()).queue();
	    		  return;
	    	}
			message.reply(getTranslation("c4_askforaccept", author, guild).replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + getTranslation("answerwithyes", author, guild)).queue();
			CommandBase.waiter.waitForEvent(MessageReceivedEvent.class, 
					(event) -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), 
					event -> {
				  final BlackUser answerUser = BlackUser.from(event.getAuthor());
				if (!answerUser.isBot() && answerUser.getId().equals(challenged.getId())) {
				      if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
				    	  message.reply(EmbedUtils.getSuccessEmbed(answerUser, guild).addField(getTranslation("challengeaccepted", answerUser, guild), getTranslation("playingagainst", answerUser, guild).replace("%challenger%", author.getName()), false).build()).queue();
				    	  
				    	  //ANGENOMMEN
				    	  ConnectFour game = ConnectFourGameManager.createGame(channel, author, challenged);
				    	  rerun(game, e.getChannel(), cmde);
				      } else if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
				    	  message.reply(EmbedUtils.getErrorEmbed(answerUser, guild).setTitle(getTranslation("declined", answerUser, guild)).addField(getTranslation("challengedeclined", answerUser, guild), getTranslation("arentyoubraveenough", answerUser, guild), false).build()).queue();
				    	  return;
				      } else {
				    	  message.reply(EmbedUtils.getErrorEmbed(answerUser, guild).addField(getTranslation("challengedeclined", answerUser, guild), getTranslation("answerwithyes", answerUser, guild), false).build()).queue();
				    	  return;
				      }
				  }
				}, 1, TimeUnit.MINUTES, () -> message.reply(EmbedUtils.getErrorEmbed(challenged, guild).addField(getTranslation("timeout", challenged, guild), getTranslation("tooktoolong", author, guild), false).build()).queue());
		} else {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("nousermentioned", author, guild), getTranslation("inputusertoplayagainst", author, guild), false).build()).queue();
			return;
		}
	}
	
	public void rerun(ConnectFour game, TextChannel channel, CommandEvent cmde) {
		CommandBase.waiter.waitForEvent(MessageReceivedEvent.class, 
	  			(answerEvent) -> game.isPlayer(answerEvent.getAuthor().getId()),
	  			(answerEvent) -> {
	  				final String msg = answerEvent.getMessage().getContentRaw();
	  				final BlackUser author = BlackUser.from(answerEvent.getAuthor());
	  				final BlackGuild guild = BlackGuild.from(answerEvent.getGuild());
	  				answerEvent.getMessage().delete().queue();
	  				if (Utils.equalsOneIgnoreCase(msg, "exit", "stop", "cancel", "leave")) {
	  					game.getMessage().editMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle(getTranslation("gaveup", author, guild)).addField(Utils.removeMarkdown(getTranslation("usergaveup", author, guild).replace("%user%", author.getAsMention())), getTranslation("sadloose", author, guild), false).build()).queue();
	  					ConnectFourGameManager.deleteGame(game);
	  					return;
	  				} else if (!author.getId().equals(game.currentUser == FieldType.X ? game.getPlayerX() : game.getPlayerY())) {
  						game.getMessage().editMessage(EmbedUtils.getErrorEmbed(author, guild).setTitle(getTranslation("connectfour", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " "  + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslation("currentstate", author, guild), game.getField(), false).setDescription(getTranslation("wrongturn", author, guild)).build()).queue();
  						rerun(game, channel, cmde);
  						return;
  					} else if (game.isValidInput(msg)) {
	  					Map.Entry<Integer, Integer> coords = game.getCoordinatesFromString(msg);
	  					FieldType[][] temp = new FieldType[ConnectFourGameManager.Y][ConnectFourGameManager.X];
	  					System.arraycopy(game.getfield(), 0, temp, 0, game.getfield().length);
	  					if (temp[coords.getKey()][coords.getValue()] != FieldType.EMPTY) {
	  						game.getMessage().editMessage(EmbedUtils.getErrorEmbed(author, guild).setTitle(getTranslation("connectfour", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslation("currentstate", author, guild), game.getField(), false).setDescription(getTranslation("fieldoccopied", author, guild)).build()).queue();
	  						game.nextUser();
	  						rerun(game, channel, cmde);
	  						return;
	  					}
	  					
	  					temp[coords.getKey()][coords.getValue()] = game.currentUser;
	  					game.setfield(temp);
	  					game.nextUser();
	  					game.getMessage().editMessage(EmbedUtils.getSuccessEmbed(author, guild).setTitle(getTranslation("connectfour", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslation("currentstate", author, guild), game.getField(), false).build()).queue();

		  				if (game.getWinner() != FieldType.EMPTY) {
		  					game.getMessage().editMessage(EmbedUtils.getSuccessEmbed(author, guild).addField("WE HAVE A WINNER!", "And the winner is....\n" + Utils.removeMarkdown((game.getWinner() == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName())) + "!", false).build()).queue();
		  					ConnectFourGameManager.deleteGame(game);
		  					return;
		  				}
	  				} else {
	  					game.getMessage().editMessage(EmbedUtils.getErrorEmbed(author, guild).setTitle(getTranslation("connectfour", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslation("currentstate", author, guild), game.getField(), false).setDescription(getTranslation("wronginput", author, guild)).build()).queue();
	  				}
	  				rerun(game, channel, cmde);
	  				return;
	  			}, 
	  		1, TimeUnit.MINUTES, () -> {game.getMessage().editMessage(EmbedUtils.getErrorEmbed(null, cmde.getGuild()).addField(LanguageSystem.getTranslation("timeout", cmde.getGuild()), LanguageSystem.getTranslation("tooktoolong", BlackGuild.from(channel.getGuild())), false).build()).queue(); ConnectFourGameManager.deleteGame(game); return;}
	  	);
	}
}