package com.github.black0nion.blackonionbot.commands.slash.impl.fun;

import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.common.utils.UserRespondUtilsImpl;
import com.github.black0nion.blackonionbot.commands.common.utils.event.UserRespondUtils;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommand;
import com.github.black0nion.blackonionbot.commands.slash.SlashCommandEvent;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettings;
import com.github.black0nion.blackonionbot.config.discord.guild.GuildSettingsRepo;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettings;
import com.github.black0nion.blackonionbot.config.discord.user.UserSettingsRepo;
import com.github.black0nion.blackonionbot.systems.games.FieldType;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToe;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeBot;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToeGameManager;
import com.github.black0nion.blackonionbot.systems.games.tictactoe.TicTacToePlayer;
import com.github.black0nion.blackonionbot.systems.language.Language;
import com.github.black0nion.blackonionbot.systems.language.LanguageSystem;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;
import com.github.black0nion.blackonionbot.utils.Pair;
import com.github.black0nion.blackonionbot.utils.Placeholder;
import com.github.black0nion.blackonionbot.wrappers.jda.BlackGuild;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TicTacToeCommand extends SlashCommand {
	private static final String USER = "user";
	private final LanguageSystem languageSystem;
	private final GuildSettingsRepo guildSettingsRepo;
	private final UserSettingsRepo userSettingsRepo;

	public TicTacToeCommand(LanguageSystem languageSystem, GuildSettingsRepo guildSettingsRepo, UserSettingsRepo userSettingsRepo) {
		super(builder(Commands.slash("tictactoe", "Used to play TicTacToe with the someone.")
			.addOption(OptionType.USER, USER, "The user to play with.")));
		this.languageSystem = languageSystem;
		this.guildSettingsRepo = guildSettingsRepo;
		this.userSettingsRepo = userSettingsRepo;
	}

	public void rerun(final UserRespondUtils cmde, final TicTacToe game, final TextChannel channel, GuildSettings guildSettings) {
		final BlackGuild guild = BlackGuild.from(channel.getGuild());
		Bot.getInstance().getEventWaiter().waitForEvent(ButtonInteractionEvent.class, answerEvent -> answerEvent.getGuild() != null && answerEvent.getGuild().getIdLong() == channel.getGuild().getIdLong() && answerEvent.getMessage().getIdLong() == game.getMessage().getIdLong() && game.isPlayer(answerEvent.getUser().getId()), answerEvent -> {
			answerEvent.deferEdit().queue();
			final User author = answerEvent.getUser();
			final String id = answerEvent.getButton().getId();
			assert id != null;

			final Message message = game.getMessage();
			if (id.equalsIgnoreCase("leave")) {
				message.editMessage(cmde.getTranslation("usergaveup").replace("%user%", author.getAsMention()))
					.setComponents(game.getRows().stream().map(row -> row.getButtons().stream().map(Button::asDisabled).toList()).map(ActionRow::of).toList()).queue();
				TicTacToeGameManager.deleteGame(game);
				return;
			} else if (!author.getId().equals(game.currentPlayer == FieldType.X ? game.getPlayerX().getId() : game.getPlayerY().getId())) {
				this.rerun(cmde, game, channel, guildSettings);
				return;
			}

			Pair<Integer, Integer> coords = new Pair<>(Integer.valueOf(id.substring(0, 1)), Integer.valueOf(id.substring(1, 2)));
			final FieldType[][] temp = new FieldType[TicTacToeGameManager.SIZE][TicTacToeGameManager.SIZE];
			System.arraycopy(game.getField(), 0, temp, 0, game.getField().length);

			final TicTacToePlayer player = game.currentPlayer == FieldType.X ? game.getPlayerX() : game.getPlayerY();

			if (temp[coords.getFirst()][coords.getSecond()] != FieldType.EMPTY) {
				message.editMessage(cmde.getTranslation("tictactoe") + " | " + cmde.getTranslation("currentplayer") + " " + player.getAsMention() + " | " + cmde.getTranslation("fieldoccupied")).queue();
				this.rerun(cmde, game, channel, guildSettings);
				return;
			}

			temp[coords.getFirst()][coords.getSecond()] = game.currentPlayer;
			game.setField(temp);

			if (this.handleWin(game, coords, answerEvent.getJDA().getSelfUser().getId())) return;

			List<ActionRow> placeAt = placeAt(game.getRows(), coords, game.currentPlayer);
			if (game.getPlayerY().isBot()) {
				placeAt = placeAt.stream().map(row -> ActionRow.of(row.getButtons().stream().map(b -> {
					if (!Objects.equals(b.getId(), "leave")) return b.asDisabled();
					else return b;
				}).toList())).toList();
			}
			game.setRows(placeAt);
			message.editMessage(cmde.getTranslation("tictactoe") + " | " + cmde.getTranslation("currentplayer") + " " + (game.currentPlayer == FieldType.O ? game.getPlayerX().getAsMention() : game.getPlayerY().getAsMention())).setComponents(placeAt).queue();
			game.nextUser();

			if (game.getPlayerY().isBot()) {
				try {
					// fake delay to make it look like the bot is thinking
					Thread.sleep(ThreadLocalRandom.current().nextInt(1000) + 1000 * (game.getMoves() / 2L + 1));
				} catch (final InterruptedException ex) {
					ex.printStackTrace();
				}

				coords = TicTacToeBot.move(game);
				temp[coords.getFirst()][coords.getSecond()] = game.currentPlayer;
				game.setField(temp);

				if (this.handleWin(game, coords, answerEvent.getJDA().getSelfUser().getId())) return;

				final List<ActionRow> placeAtBot = placeAt(game.getRows(), coords, game.currentPlayer).stream().map(row -> ActionRow.of(row.getButtons().stream().map(b -> {
					if (b.getLabel().equals(" ")) return b.asEnabled();
					else return b;
				}).toList())).toList();
				game.setRows(placeAtBot);
				message.editMessage(cmde.getTranslation("tictactoe") + " | " + cmde.getTranslation("currentplayer") + " " + (game.currentPlayer == FieldType.O ? game.getPlayerX().getAsMention() : game.getPlayerY().getAsMention())).setComponents(placeAtBot).queue();
				game.nextUser();
				this.rerun(cmde, game, channel, guildSettings);
			} else {
				this.rerun(cmde, game, channel, guildSettings);
			}
		}, 1, TimeUnit.MINUTES, () -> {
			Language language = guildSettings.getLanguage().getOrDefault();
			game.getMessage().editMessageEmbeds(EmbedUtils.getErrorEmbed(language).addField(language.getTranslationNonNull("timeout"), language.getTranslationNonNull("tooktoolong"), false).build()).setComponents().queue();
			TicTacToeGameManager.deleteGame(game);
		});
	}

	private static List<ActionRow> placeAt(final List<ActionRow> actionRowsRaw, final Pair<Integer, Integer> coords, final FieldType currentPlayer) {
		final List<ActionRow> actionRows = new ArrayList<>(actionRowsRaw);
		final List<Button> buttons = new ArrayList<>(actionRows.get(coords.getFirst()).getButtons());
		buttons.set(coords.getSecond(), Button.of(currentPlayer == FieldType.X ? ButtonStyle.SUCCESS : ButtonStyle.PRIMARY, coords.getFirst() + "" + coords.getSecond(), currentPlayer.name()).asDisabled());
		final ActionRow of = ActionRow.of(buttons);
		actionRows.set(coords.getFirst(), of);
		return actionRows;
	}

	private boolean handleWin(final TicTacToe game, final Pair<Integer, Integer> coords, final String botId) {
		final FieldType firstWinner = game.getWinner(coords.getFirst(), coords.getSecond());
		if (firstWinner == null) return false;
		else {
			final List<ActionRow> placeAt = placeAt(game.getRows(), coords, game.currentPlayer);
			placeAt.remove(3);
			if (firstWinner == FieldType.EMPTY) {
				game.getMessage().editMessage("WE HAVE NO WINNER!\nu both succ, nobody won, lul").setComponents(placeAt).queue();
			} else {
				game.getMessage().editMessage("WE HAVE A WINNER!\nAnd the winner is....\n" + (firstWinner == FieldType.X ? game.getPlayerX().getAsMention() : game.getPlayerY().getAsMention(botId)) + "!").setComponents(placeAt).queue();
			}
			TicTacToeGameManager.deleteGame(game);
			return true;
		}
	}

	@Override
	public void execute(SlashCommandEvent cmde, SlashCommandInteractionEvent e, Member member, User author, BlackGuild guild, TextChannel channel, UserSettings userSettings, GuildSettings guildSettings) throws Exception {
		User challenged = e.getOption(USER, OptionMapping::getAsUser);

		if (TicTacToeGameManager.isIngame(author.getId()) || (challenged != null && TicTacToeGameManager.isIngame(challenged.getId()))) {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(cmde.getLanguage(), author, userSettings, guildSettings).addField(cmde.getTranslation("alreadyingame"), cmde.getTranslation("nomultitasking"), false).build()).queue();
			return;
		}

		if (challenged == null || challenged.getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
			TicTacToeGameManager.createGame(languageSystem, userSettingsRepo, guild, guildSettings, e, new TicTacToePlayer(author), new TicTacToePlayer(), game -> this.rerun(cmde, game, channel, guildSettings));
			return;
		} else if (challenged.isBot() || challenged.getIdLong() == author.getIdLong()) {
			e.replyEmbeds(EmbedUtils.getErrorEmbed(cmde.getLanguage(), author, userSettings, guildSettings).addField(cmde.getTranslation("errorcantplayagainst").replace("%enemy%", (challenged.isBot() ? cmde.getTranslation("bot") : cmde.getTranslation("yourself"))), cmde.getTranslation("nofriends"), false).build()).queue();
			return;
		}

		final Language language = languageSystem.getLanguage(userSettings, guildSettings);
		e.reply(language.getTranslation("ttt_askforaccept", new Placeholder("challenged", challenged.getAsMention()), new Placeholder("challenger", author.getAsMention())) + "\n" + language.getTranslation("answerwithyes"))
			.addActionRow(
				Button.of(ButtonStyle.SUCCESS, enrichId("yes", author.getId(), challenged.getId()), language.getTranslation("yes")),
				Button.of(ButtonStyle.DANGER, enrichId("no", author.getId(), challenged.getId()), language.getTranslation("no"))
			).queue();
	}

	@Override
	public void handleButtonPress(ButtonInteractionEvent event) {
		if (event.getUser().isBot()) return;

		// format: (yes|no):challenger:challenged
		String[] idParts = getIdParts(event.getComponentId());
		String message = idParts[0];
		String challengerId = idParts[1];
		String challengedId = idParts[2];

		final Guild guild = event.getGuild();
		final GuildSettings guildSettings = guildSettingsRepo.getSettings(guild.getIdLong());


		final User challenged = event.getUser();
		final UserSettings userSettings = userSettingsRepo.getSettings(challenged);
		final UserRespondUtils challengedResponse = new UserRespondUtilsImpl(event, guildSettings, challenged, userSettings, languageSystem.getDefaultLanguage());

		// we don't care if other trolls click the button
		// we'll acknowledge them anyway
		if (!challenged.getId().equals(challengedId)) {
			event.deferEdit().queue();
			return;
		}

		if (TicTacToeGameManager.isIngame(challenged.getId()) || TicTacToeGameManager.isIngame(challengerId)) {
			Language language = languageSystem.getLanguage(userSettings, guildSettings);
			event.replyEmbeds(EmbedUtils.getErrorEmbed(languageSystem.getDefaultLanguage(), challenged, userSettings, guildSettings)
				.addField(language.getTranslationNonNull("alreadyingame"), language.getTranslationNonNull("nomultitasking"), false)
				.build())
			.setEphemeral(true)
			.queue();
			return;
		}

		// disable all buttons
		event.getMessage().editMessageComponents(ActionRow.of(event.getMessage().getButtons().stream().map(Button::asDisabled).toList())).queue();

		event.getGuild().retrieveMemberById(challengerId).queue(member -> {
			final User challenger = member.getUser();

			final UserSettings challengerSettings = userSettingsRepo.getSettings(challenger);
			final UserRespondUtils challengerResponse = new UserRespondUtilsImpl(event, guildSettings, challenger, challengerSettings, languageSystem.getDefaultLanguage());

			if (message.equalsIgnoreCase("yes")) {
				TicTacToeGameManager.createGame(languageSystem, userSettingsRepo, guild, guildSettings, event, new TicTacToePlayer(challenger), new TicTacToePlayer(challenged), game -> this.rerun(challengedResponse, game, event.getChannel().asTextChannel(), guildSettings));
			} else {
				challengerResponse.send("challengedeclined");
			}
		});
	}
}
