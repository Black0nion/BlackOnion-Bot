package com.github.ahitm_2020_2025.blackonionbot.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;

public class WebhookFactory {
	public static WebhookClient createWebhook(String url) {
		WebhookClientBuilder builder = new WebhookClientBuilder(url);
		builder.setThreadFactory((job) -> {
		    Thread thread = new Thread(job);
		    thread.setName("WebhookFactory");
		    thread.setDaemon(true);
		    return thread;
		});
		builder.setWait(true);
		return builder.build();
	}
}
