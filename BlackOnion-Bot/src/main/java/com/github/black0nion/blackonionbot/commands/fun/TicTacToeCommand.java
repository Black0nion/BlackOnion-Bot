package com.github.black0nion.blackonionbot.commands.fun;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.getTranslation;

import java.util.concurrent.TimeUnit;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.bot.CommandBase;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToe;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeBot;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeGameManager;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToePlayer;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.utils.Utils;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TicTacToeCommand extends Command {
	
	public TicTacToeCommand() {
		this.setCommand("tictactoe", "ttt")
			.setSyntax("<@User / mention me to play against me!>")
			.setRequiredBotPermissions(Permission.MESSAGE_MANAGE);
	}

	@Override
	public void execute(String[] args, CommandEvent cmde, GuildMessageReceivedEvent e, BlackMessage message, BlackMember member, BlackUser author, BlackGuild guild, TextChannel channel) {
		if (message.getMentionedBlackUsers().size() != 0) {
			final BlackUser challenged = message.getMentionedBlackUsers().get(0);
			if (challenged.getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
				rerun(TicTacToeGameManager.createGame(e.getChannel(), new TicTacToePlayer(author), new TicTacToePlayer()), e.getChannel());
				return;
			} else if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
				message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("errorcantplayagainst", author, guild).replace("%enemy%", (challenged.isBot() ? getTranslation("bot", author, guild) : getTranslation("yourself", author, guild))), getTranslation("nofriends", author, guild), false).build()).queue();
				return;
			} else if (TicTacToeGameManager.isIngame(author.getId()) || TicTacToeGameManager.isIngame(challenged.getId())) {
	    		  message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("alreadyingame", author, guild), getTranslation("nomultitasking", author, guild), false).build()).queue();
	    		  return;
	    	}
			message.reply(getTranslation("ttt_askforaccept", author, guild).replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + getTranslation("answerwithyes", author, guild)).queue();
			CommandBase.waiter.waitForEvent(MessageReceivedEvent.class, 
					(event) -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), 
					event -> {
				  final BlackUser eventAuthor = BlackUser.from(event.getAuthor());
				if (!eventAuthor.isBot() && eventAuthor.getId().equals(challenged.getId())) {
				      if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
				    	  
				    	  message.reply(EmbedUtils.getSuccessEmbed(eventAuthor, guild).addField(getTranslation("challengeaccepted", eventAuthor, guild), getTranslation("playingagainst", eventAuthor, guild).replace("%challenger%", author.getAsMention()), false).build()).queue();
				    	  
				    	  //ANGENOMMEN
				    	  TicTacToe game = TicTacToeGameManager.createGame(e.getChannel(), new TicTacToePlayer(author), new TicTacToePlayer(challenged));
				    	  rerun(game, e.getChannel());
				    	  return;
				      } else if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
				    	  message.reply(EmbedUtils.getErrorEmbed(author, guild).setTitle(getTranslation("declined", eventAuthor, guild)).addField(getTranslation("challengedeclined", eventAuthor, guild), getTranslation("arentyoubraveenough", eventAuthor, guild), false).build()).queue();
				    	  return;
				      } else {
				    	  message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("challengedeclined", eventAuthor, guild), getTranslation("answerwithyes", eventAuthor, guild), false).build()).queue();
				    	  return;
				      }
				  }
				}, 1, TimeUnit.MINUTES, () -> message.reply(EmbedUtils.getErrorEmbed(challenged, guild).addField(getTranslation("timeout", challenged, guild), getTranslation("tooktoolong", author, guild), false).build()).queue());
		} else {
			message.reply(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("nousermentioned", author, guild), getTranslation("inputusertoplayagainst", author, guild), false).build()).queue();
			return;
		}
	}
	
	public void rerun(TicTacToe game, TextChannel channel) {
		final BlackGuild guild = BlackGuild.from(channel.getGuild());
		CommandBase.waiter.waitForEvent(GuildMessageReceivedEvent.class, 
	  			(answerEvent) -> answerEvent.getGuild().getIdLong() == channel.getGuild().getIdLong() && game.isPlayer(answerEvent.getAuthor().getId()),
	  			(answerEvent) -> {
	  				final String msg = answerEvent.getMessage().getContentRaw();
	  				final BlackUser author = BlackUser.from(answerEvent.getAuthor());
	  				answerEvent.getMessage().delete().queue();
	  				if (Utils.equalsOneIgnoreCase(msg, "exit", "stop", "cancel", "leave")) {
	  					game.getMessage().editMessage(EmbedUtils.getSuccessEmbed().setTitle(getTranslation("gaveup", author, guild)).addField(getTranslation("usergaveup", author, guild).replace("%user%", Utils.removeMarkdown(author.getName())), getTranslation("sadloose", author, guild), false).build()).queue();
	  					TicTacToeGameManager.deleteGame(game);
	  					return;
	  				} else if (!author.getId().equals(game.currentPlayer == FieldType.X ? game.getPlayerX().getId() : game.getPlayerY().getId())) {
  						game.getMessage().editMessage(EmbedUtils.getErrorEmbed().setTitle(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " "  + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslation("currentstate", author, guild), game.getFieldString(), false).setDescription(getTranslation("wrongturn", author, guild)).build()).queue();
  						rerun(game, channel);
  						return;
  					} else if (game.isValidInput(msg)) {
						Pair<Integer, Integer> coords = TicTacToe.getCoordinatesFromString(msg);
	  					FieldType[][] temp = new FieldType[TicTacToeGameManager.SIZE][TicTacToeGameManager.SIZE];
	  					System.arraycopy(game.getField(), 0, temp, 0, game.getField().length);
	  					
	  					if (temp[coords.getKey()][coords.getValue()] != FieldType.EMPTY) {
	  						game.getMessage().editMessage(EmbedUtils.getErrorEmbed().setTitle(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslation("currentstate", author, guild), game.getFieldString(), false).setDescription(getTranslation("fieldoccopied", author, guild)).build()).queue();
	  						//game.nextUser();
	  						rerun(game, channel);
	  						return;
	  					}
	  					
	  					temp[coords.getKey()][coords.getValue()] = game.currentPlayer;
	  					game.setField(temp);
	  					
	  					if (handleWin(game, coords)) return;
	  					
	  					game.nextUser();
	  					game.getMessage().editMessage(EmbedUtils.getSuccessEmbed().setTitle(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslation("currentstate", author, guild), game.getFieldString(), false).build()).queue();
	  					
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
							
							game.getMessage().editMessage(EmbedUtils.getSuccessEmbed().setTitle(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslation("currentstate", author, guild), game.getFieldString(), false).build()).queue();
							rerun(game, channel);
							return;
	  					} else {
	  						rerun(game, channel);
	  						return;
	  					}
	  				} else {
	  					game.getMessage().editMessage(EmbedUtils.getErrorEmbed().setTitle(getTranslation("tictactoe", author, guild) + " | " + getTranslation("currentplayer", author, guild) + " " + Utils.removeMarkdown((game.currentPlayer == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(getTranslation("currentstate", author, guild), game.getFieldString(), false).setDescription(getTranslation("wronginput", author, guild)).build()).queue();
		  				rerun(game, channel);
		  				return;
	  				}
	  			}, 
	  		1, TimeUnit.MINUTES, () -> {game.getMessage().editMessage(EmbedUtils.getErrorEmbed().addField(LanguageSystem.getTranslation("timeout", guild), LanguageSystem.getTranslation("tooktoolong", guild), false).build()).queue(); TicTacToeGameManager.deleteGame(game); return;}
	  	);
	}
	
	private boolean handleWin(TicTacToe game, Pair<Integer, Integer> coords) {
		final FieldType firstWinner = game.getWinner(coords.getKey(), coords.getValue());
		if (firstWinner == null) {
			return false;
		} else if (firstWinner != FieldType.EMPTY) {
			game.getMessage().editMessage(EmbedUtils.getSuccessEmbed().addField("WE HAVE A WINNER!", "And the winner is....\n" + Utils.removeMarkdown((firstWinner == FieldType.X ? game.getPlayerX().getAsMention() : game.getPlayerY().getAsMention())) + "!", false).build()).queue();
			TicTacToeGameManager.deleteGame(game);
			return true;
		} else {
			game.getMessage().editMessage(EmbedUtils.getSuccessEmbed().addField("WE HAVE NO WINNER!", "u both succ, nobody won, lul", false).build()).queue();
			TicTacToeGameManager.deleteGame(game);
			return true;
		}
	}
}