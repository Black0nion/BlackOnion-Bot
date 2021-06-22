/**
 *
 */
package com.github.black0nion.blackonionbot.systems;

import java.awt.Color;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.github.black0nion.blackonionbot.blackobjects.BlackEmbed;
import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.utils.EmbedUtils;

import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * @author _SIM_
 *
 */
public class CustomCommand {

    private final BlackGuild guild;
    private String command;
    private BlackEmbed embed;
    private String answer;
    private boolean reply;

    public CustomCommand(final BlackGuild guild, final String command, final String answer) {
	this.guild = guild;
	this.command = command.toLowerCase();
	this.answer = answer;
    }

    public CustomCommand(final BlackGuild guild, final String command, final BlackEmbed answer) {
	this.guild = guild;
	this.command = command.toLowerCase();
	this.embed = answer;
    }

    public CustomCommand(final BlackGuild guild, final Document doc) {
	this.guild = guild;
	if (doc.containsKey("command")) {
	    this.command = doc.getString("command").toLowerCase();
	} else {
	    this.command = "";
	}

	if (doc.containsKey("embed")) {
	    final Document embedDoc = doc.get("embed", new Document());
	    final BlackEmbed embed = new BlackEmbed();
	    if (embedDoc.containsKey("title")) {
		if (embedDoc.containsKey("url")) {
		    embed.setTitle(embedDoc.getString("title"), embedDoc.getString("url"));
		} else {
		    embed.setTitle(embedDoc.getString("title"));
		}
	    }
	    if (embedDoc.containsKey("fields")) {
		for (final Document field : embedDoc.getList("fields", Document.class)) {
		    embed.addField(field.getString("name"), field.getString("value"), false);
		}
	    }
	    if (embedDoc.containsKey("color")) {
		final int integer = embedDoc.getInteger("color", EmbedUtils.blackOnionColor.getRGB());
		embed.setColor(new Color(integer));
	    }
	    this.embed = embed;
	} else if (doc.containsKey("answer")) {
	    this.answer = doc.getString("answer");
	} else {
	    // TODO: error handling
	}
	this.reply = doc.getBoolean("reply", true);
    }

    public CustomCommand setReply(final boolean reply) {
	this.reply = reply;
	return this;
    }

    /**
     * @return the command
     */
    public String getCommand() {
	return this.command;
    }

    /**
     * @return the guild
     */
    public BlackGuild getGuild() {
	return this.guild;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(final String command) {
	this.command = command;
    }

    public void handle(final GuildMessageReceivedEvent event) {
	if (this.embed != null) {
	    this.embed.setTimestamp(Instant.now());
	    final User author = event.getAuthor();
	    this.embed.setFooter(author.getName() + "#" + author.getDiscriminator(), author.getEffectiveAvatarUrl());

	    if (this.reply) {
		event.getMessage().replyEmbeds(this.embed.build()).queue();
	    } else {
		event.getChannel().sendMessageEmbeds(this.embed.build()).queue();
	    }
	} else if (this.answer != null) {
	    if (this.reply) {
		event.getMessage().reply(this.answer).queue();
	    } else {
		event.getChannel().sendMessage(this.answer).queue();
	    }
	} else throw new NullPointerException("Both embed and Answer is null!");
    }

    public Document toDocument() {
	final Document doc = new Document();
	doc.put("command", this.command);
	doc.put("reply", this.reply);
	if (this.embed != null) {
	    final Document embedDoc = new Document();
	    if (this.embed.getTitle() != null) {
		embedDoc.put("title", this.embed.getTitle());
	    }
	    if (this.embed.getUrl() != null) {
		embedDoc.put("url", this.embed.getUrl());
	    }
	    final List<Field> fields = this.embed.getFields();
	    if (fields.size() != 0) {
		final List<Document> fieldsDoc = new ArrayList<>();
		fields.forEach(field -> fieldsDoc.add(new Document().append("name", field.getName()).append("value", field.getValue())));
		embedDoc.put("fields", fieldsDoc);
	    }
	    if (this.embed.getColor() != null) {
		embedDoc.put("color", this.embed.getColor().getRGB());
	    }
	    doc.put("embed", embedDoc);
	} else if (this.answer != null) {
	    doc.put("answer", this.answer);
	} else {
	    // TODO: error handling
	}
	return doc;
    }

    @Override
    public boolean equals(final Object obj) {
	final CustomCommand cmd = (CustomCommand) obj;
	return cmd.getCommand().equalsIgnoreCase(this.getCommand()) && cmd.getGuild().getIdLong() == this.getGuild().getIdLong();
    }
}