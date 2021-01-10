package com.github.ahitm_2020_2025.blackonionbot.commands.fun;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.NotNull;

import com.github.ahitm_2020_2025.blackonionbot.enums.Category;
import com.github.ahitm_2020_2025.blackonionbot.oldcommands.Command;
import com.github.ahitm_2020_2025.blackonionbot.systems.games.connectfour.ConnectFour;
import com.github.ahitm_2020_2025.blackonionbot.systems.games.connectfour.ConnectFourGameManager;
import com.github.ahitm_2020_2025.blackonionbot.systems.games.connectfour.FieldType;
import com.github.ahitm_2020_2025.blackonionbot.utils.EmbedUtils;
import com.github.ahitm_2020_2025.blackonionbot.utils.Utils;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ConnectFourCommand implements Command {
	
	private final EventWaiter waiter;
	
	public ConnectFourCommand(EventWaiter waiter) {
		this.waiter = waiter;
	}

	@Override
	public String[] getCommand() {
		return new String[] {"connect4"};
	}

	@Override
	public void execute(String[] args, MessageReceivedEvent e, Message message, Member member, User author, MessageChannel channel) {
		if (message.getMentionedUsers().size() != 0) {
			User challenged = message.getMentionedUsers().get(0);
			if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
				channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Du kannst nicht gegen " + (challenged.isBot() ? "einen Bot" : "dich selbst") + " spielen!", "Wenn du keine Freunde hast, mit denen du spielen kannst, kaufe dir welche auf Fiverr!", false).build()).queue();
				return;
			}
			if (ConnectFourGameManager.isIngame(author.getId()) || ConnectFourGameManager.isIngame(challenged.getId())) {
	    		  channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Du bist bereits ingame!", "Multitasking ist leider nicht möglich. :(", false).build()).queue();
	    		  return;
	    	  }
			channel.sendMessage(challenged.getAsMention() + ", willst du gegen " + author.getAsMention() + " eine Runde 4 Gewinnt spielen? Antworte mit \"yes\", um die Challenge anzunehmen!").queue();
			waiter.waitForEvent(MessageReceivedEvent.class, 
					(event) -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), 
					event -> {
				  if (!event.getAuthor().isBot() && event.getAuthor().getId().equals(challenged.getId())) {
				      if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
				    	  
				    	  channel.sendMessage(EmbedUtils.getDefaultSuccessEmbed(event.getAuthor()).addField("Herausforderung angenommen!", "Du wirst gegen " + author.getAsMention() + " spielen!", false).build()).queue();
				    	  
				    	  //ANGENOMMEN
				    	  ConnectFour game = ConnectFourGameManager.createGame(channel, author.getId(), author.getName(), challenged.getId(), challenged.getName());
				    	  rerun(game, channel);
				      } else if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
				    	  channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).setTitle("Abgelehnt!").addField("Herausforderung abgelehnt!", "Traust du dich etwa nicht?", false).build()).queue();
				    	  return;
				      } else {
				    	  channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Herausforderung abgelehnt!", "Antworte mit \"yes\", um die Challenge anzunehmen!", false).build()).queue();
				    	  return;
				      }
				  }
				}, 1, TimeUnit.MINUTES, () -> channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Timeout", "Du hast zu lange gebraucht, um zu antworten!", false).build()).queue());
		} else {
			channel.sendMessage(EmbedUtils.getDefaultErrorEmbed(author).addField("Kein User angegeben!", "Bitte gebe einen User an, gegen den du spielen willst!", false).build()).queue();
			return;
		}
	}
	
	public void rerun(ConnectFour game, MessageChannel channel) {
		waiter.waitForEvent(MessageReceivedEvent.class, 
	  			(answerEvent) -> game.isPlayer(answerEvent.getAuthor().getId()),
	  			(answerEvent) -> {
	  				final String msg = answerEvent.getMessage().getContentRaw();
	  				if (msg.equalsIgnoreCase("exit")) {
	  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle("Aufgegeben!").addField(Utils.removeMarkdown(answerEvent.getAuthor().getName()) + " hat aufgegeben!", "Was eine erbärmliche Niederlage!", false).build()).queue();
	  					return;
	  				}
	  				
	  				if (!answerEvent.getAuthor().getId().equals(game.currentUser == FieldType.X ? game.getPlayerX() : game.getPlayerY())) {
  						game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle("Connect 4 | Aktueller Spieler: " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY()))).addField("Current State:", game.getField(), false).setDescription("Du bist nicht an der Reihe!").build()).queue();
  						rerun(game, channel);
  						return;
  					}
	  				
	  				if (game.isValidInput(msg)) {
	  					Map.Entry<Integer, Integer> coords = game.getCoordinatesFromString(msg);
	  					FieldType[][] temp = new FieldType[ConnectFourGameManager.Y][ConnectFourGameManager.X];
	  					System.arraycopy(game.getfield(), 0, temp, 0, game.getfield().length);
	  					
	  					if (temp[coords.getKey()][coords.getValue()] != FieldType.EMPTY) {
	  						game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle("Connect 4 | Aktueller Spieler: " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY()))).addField("Current State:", game.getField(), false).setDescription("This field is already occopied!").build()).queue();
	  						rerun(game, channel);
	  						return;
	  					}
	  					
	  					temp[coords.getKey()][coords.getValue()] = game.currentUser;
	  					game.setfield(temp);
	  					game.nextUser();
	  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().setTitle("Connect 4 | Aktueller Spieler: " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY()))).addField("Current State:", game.getField(), false).build()).queue();

		  				if (game.getWinner() != FieldType.EMPTY) {
		  					game.getMessage().editMessage(EmbedUtils.getDefaultSuccessEmbed().addField("WIR HABEN EINEN GEWINNER!", "Und der Gewinner ist....\n" + Utils.removeMarkdown((game.getWinner() == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY())) + "!", false).build()).queue();
		  					ConnectFourGameManager.deleteGame(game);
		  					return;
		  				}
	  				} else {
	  					game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().setTitle("Connect 4 | Aktueller Spieler: " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerNameX() : game.getPlayerNameY()))).addField("Current State:", game.getField(), false).setDescription("Falsche Eingabe!!").build()).queue();
	  				}
	  				rerun(game, channel);
	  				return;
	  			}, 
	  		30, TimeUnit.SECONDS, () -> {game.getMessage().editMessage(EmbedUtils.getDefaultErrorEmbed().addField("Timeout", "Du hast zu lange gebraucht, um zu antworten!", false).build()).queue(); ConnectFourGameManager.deleteGame(game); return;}
	  	);
	}

	@Override
	public @NotNull String getDescription() {
		return "Play a Game of Connect 4 against another player!";
	}
	
	@Override
	public Category getCategory() {
		return Category.FUN;
	}

}
