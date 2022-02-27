package com.github.black0nion.blackonionbot.commands.fun;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFour;
import com.github.black0nion.blackonionbot.systems.games.connectfour.ConnectFourGameManager;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Utils;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.black0nion.blackonionbot.systems.language.LanguageSystem.getTranslation;

public class ConnectFourCommand extends Command {

    public ConnectFourCommand() {
	this.setCommand("connect4", "connectfour", "viergewinnt", "4gewinnt");
    }

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final MessageReceivedEvent e, final Message message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	if (message.getMentionedUsers().size() != 0) {
	    final BlackUser challenged = BlackUser.from(message.getMentionedUsers().get(0));
		assert challenged != null;
		if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
		message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("errorcantplayagainst", author, guild).replace("%enemy%", (challenged.isBot() ? getTranslation("bot", author, guild) : getTranslation("yourself", author, guild))), getTranslation("nofriends", author, guild), false).build()).queue();
		return;
	    }
	    if (ConnectFourGameManager.isIngame(author.getId()) || ConnectFourGameManager.isIngame(challenged.getId())) {
		message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("alreadyingame", author, guild), getTranslation("nomultitasking", author, guild), false).build()).queue();
		return;
	    }
	    message.reply(getTranslation("c4_askforaccept", author, guild).replace("%challenged%", challenged.getAsMention()).replace("%challenger%", author.getAsMention()) + " " + getTranslation("answerwithyes", author, guild)).queue();
	    Bot.waiter.waitForEvent(MessageReceivedEvent.class, event -> event.getChannel().getIdLong() == channel.getIdLong() && event.getAuthor().getIdLong() == challenged.getIdLong(), event -> {
		final BlackUser answerUser = BlackUser.from(event.getAuthor());
		if (!answerUser.isBot() && answerUser.getId().equals(challenged.getId())) if (event.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
		    message.replyEmbeds(EmbedUtils.getSuccessEmbed(answerUser, guild).addField(getTranslation("challengeaccepted", answerUser, guild), getTranslation("playingagainst", answerUser, guild).replace("%challenger%", author.getName()), false).build()).queue();

		    //ANGENOMMEN
		    final ConnectFour game = ConnectFourGameManager.createGame(channel, author, challenged);
		    this.rerun(game, e.getTextChannel(), cmde);
		} else if (event.getMessage().getContentRaw().equalsIgnoreCase("no")) {
		    message.replyEmbeds(EmbedUtils.getErrorEmbed(answerUser, guild).setTitle(getTranslation("declined", answerUser, guild)).addField(getTranslation("challengedeclined", answerUser, guild), getTranslation("arentyoubraveenough", answerUser, guild), false).build()).queue();
		    return;
		} else {
		    message.replyEmbeds(EmbedUtils.getErrorEmbed(answerUser, guild).addField(getTranslation("challengedeclined", answerUser, guild), getTranslation("answerwithyes", answerUser, guild), false).build()).queue();
		    return;
		}
	    }, 1, TimeUnit.MINUTES, () -> message.replyEmbeds(EmbedUtils.getErrorEmbed(challenged, guild).addField(getTranslation("timeout", challenged, guild), getTranslation("tooktoolong", author, guild), false).build()).queue());
	} else {
	    message.replyEmbeds(EmbedUtils.getErrorEmbed(author, guild).addField(getTranslation("nousermentioned", author, guild), getTranslation("inputusertoplayagainst", author, guild), false).build()).queue();
	    return;
	}
    }

    public void rerun(final ConnectFour game, final TextChannel channel, final CommandEvent cmde) {
	Bot.waiter.waitForEvent(MessageReceivedEvent.class, answerEvent -> game.isPlayer(answerEvent.getAuthor().getId()), answerEvent -> {
	    final String msg = answerEvent.getMessage().getContentRaw();
	    final BlackUser author = BlackUser.from(answerEvent.getAuthor());
	    final BlackGuild guild = BlackGuild.from(answerEvent.getGuild());
	    answerEvent.getMessage().delete().queue();
	    if (Utils.equalsOneIgnoreCase(msg, "exit", "stop", "cancel", "leave")) {
		game.getMessage().editMessageEmbeds(EmbedUtils.getSuccessEmbed(author, guild).setTitle(cmde.getTranslation("gaveup")).addField(Utils.removeMarkdown(cmde.getTranslation("usergaveup").replace("%user%", author.getAsMention())), cmde.getTranslation("sadloose"), false).build()).queue();
		ConnectFourGameManager.deleteGame(game);
		return;
	    } else if (!author.getId().equals(game.currentUser == FieldType.X ? game.getPlayerX() : game.getPlayerY())) {
		game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getField(), false).setDescription(cmde.getTranslation("wrongturn")).build()).queue();
		this.rerun(game, channel, cmde);
		return;
	    } else if (game.isValidInput(msg)) {
		final Map.Entry<Integer, Integer> coords = game.getCoordinatesFromString(msg);
		final FieldType[][] temp = new FieldType[ConnectFourGameManager.Y][ConnectFourGameManager.X];
		System.arraycopy(game.getfield(), 0, temp, 0, game.getfield().length);
		if (temp[coords.getKey()][coords.getValue()] != FieldType.EMPTY) {
		    game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getField(), false).setDescription(cmde.getTranslation("fieldoccopied")).build()).queue();
		    game.nextUser();
		    this.rerun(game, channel, cmde);
		    return;
		}

		temp[coords.getKey()][coords.getValue()] = game.currentUser;
		game.setfield(temp);
		game.nextUser();
		game.getMessage().editMessageEmbeds(EmbedUtils.getSuccessEmbed(author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getField(), false).build()).queue();

		if (game.getWinner() != FieldType.EMPTY) {
		    game.getMessage().editMessageEmbeds(EmbedUtils.getSuccessEmbed(author, guild).addField("WE HAVE A WINNER!", "And the winner is....\n" + Utils.removeMarkdown((game.getWinner() == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName())) + "!", false).build()).queue();
		    ConnectFourGameManager.deleteGame(game);
		    return;
		}
	    } else {
		game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(author, guild).setTitle(cmde.getTranslation("connectfour") + " | " + cmde.getTranslation("currentplayer") + " " + Utils.removeMarkdown((game.currentUser == FieldType.X ? game.getPlayerX().getName() : game.getPlayerY().getName()))).addField(cmde.getTranslation("currentstate"), game.getField(), false).setDescription(cmde.getTranslation("wronginput")).build()).queue();
	    }
	    this.rerun(game, channel, cmde);
	    return;
	}, 1, TimeUnit.MINUTES, () -> {
	    game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(null, cmde.getGuild()).addField(cmde.getGuild().getLanguage().getTranslation("timeout"), cmde.getGuild().getLanguage().getTranslation("tooktoolong"), false).build()).queue();
	    ConnectFourGameManager.deleteGame(game);
	    return;
	});
    }
}