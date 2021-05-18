package com.github.black0nion.blackonionbot.commands.fun;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.getTranslatedString;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.misc.Category;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToe;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeBot;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeGameManager;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToePlayer;
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

public class TicTacToeCommand implements Command {
	@Override
	public String[] getCommand() {
		return new String[] { "tictactoe", "ttt" };
	}

	@Override
	public void execute(String[] args, GuildMessageReceivedEvent e, Message message, Member member, User author, Guild guild, TextChannel channel) {
		if (message.getMentionedUsers().size() != 0) {
			User challenged = message.getMentionedUsers().get(0);
			if (challenged.getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
				rerun(TicTacToeGameManager.createGame(e.getChannel(), new TicTacToePlayer(author), new TicTacToePlayer()), e.getChannel());
				return;
			} else if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
				channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField(getTranslatedString("errorcantplayagainst", author, guild).replace("%enemy%", (challenged.isBot() ? getTranslatedString("bot", author, guild) : getTranslatedString("yourself", author, guild))), getTranslatedString("nofriends", author, guild), false).build()).queue();
				return;
			} else if (TicTacToeGameManager.isIngame(author.getId()) || TicTacToeGameManager.isIngame(challenged.getId())) {
	    		  channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField(getTranslatedString("alreadyingame", author, guild), getTranslatedString("nomultitasking", author, guild), false).build()).queue();
	    		  return;
	    	}
			channel.sendMessage(getTranslatedString("ttt_askforaccept", author, guild).replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + getTranslatedString("answerwithyes", author, guild)).queue();
			CommandBase.waiter.waitForEvent(MessageReceivedEvent.class, 
					(event) -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), 
					event -> {
				  if (!event.getAuthor().isBot() && event.getAuthor().getId().equals(challenged.getId())) {
				      if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
				    	  
				    	  channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(event.getAuthor()).addField(getTranslatedString("challengeaccepted", event.getAuthor(), guild), getTranslatedString("playingagainst", event.getAuthor(), guild).replace("%challenger%", author.getAsMention()), false).build()).queue();
				    	  
				    	  //ANGENOMMEN
				    	  TicTacToe game = TicTacToeGameManager.createGame(e.getChannel(), new TicTacToePlayer(author), new TicTacToePlayer(challenged));
				    	  rerun(game, e.getChannel());
				    	  return;
				      } else if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
				    	  channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor()).setTitle(getTranslatedString("declined", event.getAuthor(), guild)).addField(getTranslatedString("challengedeclined", event.getAuthor(), guild), getTranslatedString("arentyoubraveenough", event.getAuthor(), guild), false).build()).queue();
				    	  return;
				      } else {
				    	  channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(event.getAuthor()).addField(getTranslatedString("challengedeclined", event.getAuthor(), guild), getTranslatedString("answerwithyes", event.getAuthor(), guild), false).build()).queue();
				    	  return;
				      }
				  }
				}, 1, TimeUnit.MINUTES, () -> channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(challenged).addField(getTranslatedString("timeout", challenged, guild), getTranslatedString("tooktoolong", author, guild), false).build()).queue());
		} else {
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField(getTranslatedString("nousermentioned", author, guild), getTranslatedString("inputusertoplayagainst", author, guild), false).build()).queue();
			return;
		}
	}
	
	public void rerun(TicTacToe game, TextChannel channel) {
		CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, 
	  			(answerEvent) -> answerEvent.getGuild().getIdLong() == channel.getGuild().getIdLong() && game.isPlayer(answerEvent.getAuthor().getId()),
	  			(answerEvent) -> {
	  				final String msg = answerEvent.getMessage().getContentRaw();
	  				final User author = answerEvent.getAuthor();
	  				final Guild guild = answerEvent.getGuild();
	  				answerEvent.getMessage().delete().queue();
	  				if (Utils.equalsOneIgnoreCase(msg, "exit", "stop", "cancel", "leave")) {
	  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle(getTranslatedString("gaveup", author, guild)).addField(getTranslatedString("usergaveup", author, guild).replace("%user%", Utils.removeMarkdown(author.getName())), getTranslatedString("sadloose", author, guild), false).build()).queue();
	  					TicTacToeGameManager.deleteGame(game);
	  					return;
	  				} else if (!author.getId().equals(game.currentPlayer == FieldType.X ? game.getPlayerX().getId() : game.getPlayerY().getId())) {
  						game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle(getTranslatedString("tictactoe", author, guild) + " | " + getTranslatedString("currentplayer", author, guild) + " "  + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslatedString("currentstate", author, guild), game.getFieldString(), false).setDescription(getTranslatedString("wrongturn", author, guild)).build()).queue();
  						rerun(game, channel);
  						return;
  					} else if (game.isValidInput(msg)) {
	  					@SuppressWarnings("static-access")
						Map.Entry<Integer, Integer> coords = game.getCoordinatesFromString(msg);
	  					FieldType[][] temp = new FieldType[TicTacToeGameManager.SIZE][TicTacToeGameManager.SIZE];
	  					System.arraycopy(game.getField(), 0, temp, 0, game.getField().length);
	  					
	  					if (temp[coords.getKey()][coords.getValue()] != FieldType.EMPTY) {
	  						game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle(getTranslatedString("tictactoe", author, guild) + " | " + getTranslatedString("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslatedString("currentstate", author, guild), game.getFieldString(), false).setDescription(getTranslatedString("fieldoccopied", author, guild)).build()).queue();
	  						//game.nextUser();
	  						rerun(game, channel);
	  						return;
	  					}
	  					
	  					temp[coords.getKey()][coords.getValue()] = game.currentPlayer;
	  					game.setField(temp);
	  					
	  					if (handleWin(game, coords)) return;
	  					
	  					game.nextUser();
	  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle(getTranslatedString("tictactoe", author, guild) + " | " + getTranslatedString("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslatedString("currentstate", author, guild), game.getFieldString(), false).build()).queue();
	  					
	  					if (game.getPlayerY().isBot()) {
		  					try {
								Thread.sleep(Bot.random.nextInt(1500) + 1000 * (game.getMoves() / 2 + 1));
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
		  					
		  					coords = TicTacToeBot.move(game);
		  					temp[coords.getKey()][coords.getValue()] = game.currentPlayer;
		  					game.setField(temp);
		  					
		  					if (handleWin(game, coords)) return;
		  					game.nextUser();
							
							game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle(getTranslatedString("tictactoe", author, guild) + " | " + getTranslatedString("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslatedString("currentstate", author, guild), game.getFieldString(), false).build()).queue();
							rerun(game, channel);
							return;
	  					} else {
	  						rerun(game, channel);
	  						return;
	  					}
	  				} else {
	  					game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle(getTranslatedString("tictactoe", author, guild) + " | " + getTranslatedString("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslatedString("currentstate", author, guild), game.getFieldString(), false).setDescription(getTranslatedString("wronginput", author, guild)).build()).queue();
		  				rerun(game, channel);
		  				return;
	  				}
	  			}, 
	  		1, TimeUnit.MINUTES, () -> {game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().addField(LanguageSystem.getTranslatedString("timeout", channel.getGuild()), LanguageSystem.getTranslatedString("tooktoolong", channel.getGuild()), false).build()).queue(); TicTacToeGameManager.deleteGame(game); return;}
	  	);
	}
	
	private boolean handleWin(TicTacToe game, Map.Entry<Integer, Integer> coords) {
		final FieldType firstWinner = game.getWinner(coords.getKey(), coords.getValue());
		if (firstWinner == null) {
			return false;
		} else if (firstWinner != FieldType.EMPTY) {
			game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().addField("WE HAVE A WINNER!", "And the winner is....\n" + Utils.removeMarkdown((firstWinner == FieldType.X ? game.getPlayerX().getAsMention() : game.getPlayerY().getAsMention())) + "!", false).build()).queue();
			TicTacToeGameManager.deleteGame(game);
			return true;
		} else {
			game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().addField("WE HAVE NO WINNER!", "u both succ, nobody won, lul", false).build()).queue();
			TicTacToeGameManager.deleteGame(game);
			return true;
		}
	}
	
	@Override
	public Category getCategory() {
		return Category.FUN;
	}
	
	@Override
	public String getSyntax() {
		return "<@User / mention me to play against me!>";
	}
}