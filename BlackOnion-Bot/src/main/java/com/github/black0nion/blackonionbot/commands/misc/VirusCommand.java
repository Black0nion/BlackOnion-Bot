package com.github.black0nion.blackonionbot.commands.misc;

import com.github.black0nion.blackonionbot.blackobjects.BlackGuild;
import com.github.black0nion.blackonionbot.blackobjects.BlackMember;
import com.github.black0nion.blackonionbot.blackobjects.BlackMessage;
import com.github.black0nion.blackonionbot.blackobjects.BlackUser;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.commands.Command;
import com.github.black0nion.blackonionbot.commands.CommandEvent;
import com.github.black0nion.blackonionbot.misc.Progress;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class VirusCommand extends Command {

    public VirusCommand() {
	this.setCommand("viruscheck", "virus").setSyntax("<url / attachement>").setProgress(Progress.PAUSED);
    }

//    private static final UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });

    @Override
    public void execute(final String[] args, final CommandEvent cmde, final GuildMessageReceivedEvent e, final BlackMessage message, final BlackMember member, final BlackUser author, final BlackGuild guild, final TextChannel channel) {
	cmde.reply(cmde.success().setTitle("VirusTotal", "https://www.virustotal.com/gui/home/upload").addField("virustotalfieldtitle", "virustotalinfo", false));
//	Bot.executor.submit(() -> {
//	    final List<Attachment> attachments = message.getAttachments();
//
//	    String url = "";
//	    if (attachments.size() != 0) {
//		if (attachments.get(0).getSize() >= 32000000) {
//		    url = new JSONObject(getUploadUrl()).getString("data");
//		} else {
//		    url = attachments.get(0).getUrl();
//		}
//	    } else if (args.length >= 2) {
//		if (!urlValidator.isValid(args[1])) {
//		    cmde.error("invalidurl", "pleaseinputvalidurl");
//		    return;
//		}
//		url = args[1];
//	    } else {
//		cmde.sendPleaseUse();
//		return;
//	    }
//
//	    final String finalUrl = url;
//	    final HttpResponse<String> checkUrl = checkUrl(finalUrl);
//	    final String urlResult = checkUrl.getBody();
//	    if (!new JSONObject(urlResult).has("data")) {
//		cmde.sendPleaseUse();
//		return;
//	    }
//
//	    System.out.println(urlResult);
//	    cmde.success("fileprocessed", "waitforresult", new Placeholder("url", "non"));
//	});
    }

    public static HttpResponse<String> checkUrl(final String url) {
	try {
	    final String key = Bot.getCredentialsManager().getString("virustotal_key");
	    if (key == null) return null;
	    Unirest.setTimeouts(0, 0);
	    return Unirest.post("https://www.virustotal.com/api/v3/urls").header("x-apikey", key).field("url", url).asString();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    public static HttpResponse<String> getUploadUrl() {
	try {
	    final String key = Bot.getCredentialsManager().getString("virustotal_key");
	    if (key == null) return null;
	    Unirest.setTimeouts(0, 0);
	    return Unirest.get("https://www.virustotal.com/api/v3/files/upload_url").header("x-apikey", key).asString();
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }
}