package com.github.black0nion.blackonionbot.utils;

import club.minnced.discord.webhook.WebhookClient;
import com.github.black0nion.blackonionbot.bot.Bot;
import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import com.github.black0nion.blackonionbot.tests.SameObject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UtilsTest {

	@Test
	void test_getCountryFromCode() {
		assertEquals("United Kingdom", Utils.getCountryFromCode("GB"));
		assertEquals("Austria", Utils.getCountryFromCode("AT"));
		assertEquals("Germany", Utils.getCountryFromCode("DE"));
		assertEquals("Australia", Utils.getCountryFromCode("AU"));
		assertEquals("United States", Utils.getCountryFromCode("US"));
	}

	@SuppressWarnings({ "UnnecessaryBoxing", "removal", "CachedNumberConstructorCall" })
	@Test
	void test_isLong_object() {
		assertTrue(Utils.isLong(69L));
		assertTrue(Utils.isLong(420L));
		assertTrue(Utils.isLong(Long.MAX_VALUE));
		assertTrue(Utils.isLong(Long.MIN_VALUE));

		assertTrue(Utils.isLong(new Long(69L)));
		assertTrue(Utils.isLong(new Long(420L)));
		assertTrue(Utils.isLong(new Long(Long.MAX_VALUE)));
		assertTrue(Utils.isLong(new Long(Long.MIN_VALUE)));

		assertTrue(Utils.isLong(69D));
		assertTrue(Utils.isLong(420D));

		assertFalse(Utils.isLong((Object) "nan"));
		assertFalse(Utils.isLong((Object) "hi"));
	}

	@Test
	void test_isLong_string() {
		assertTrue(Utils.isLong("69"));
		assertTrue(Utils.isLong("420"));
		assertTrue(Utils.isLong(Long.toString(Long.MAX_VALUE)));
		assertTrue(Utils.isLong(Long.toString(Long.MIN_VALUE)));

		assertFalse(Utils.isLong("nan"));
		assertFalse(Utils.isLong("hi"));
	}

	@Test
	void test_isInteger() {
		assertTrue(Utils.isInteger("69"));
		assertTrue(Utils.isInteger("420"));
		assertTrue(Utils.isInteger(Integer.toString(Integer.MAX_VALUE)));
		assertTrue(Utils.isInteger(Integer.toString(Integer.MIN_VALUE)));

		assertFalse(Utils.isInteger("nan"));
		assertFalse(Utils.isInteger("hi"));
	}

	@Test
	void test_isBoolean_string() {
		assertTrue(Utils.isBoolean("true"));
		assertTrue(Utils.isBoolean("false"));

		assertFalse(Utils.isBoolean("nan"));
		assertFalse(Utils.isBoolean("hi"));
	}

	@Test
	void test_isBoolean_object() {
		assertTrue(Utils.isBoolean(true));
		assertTrue(Utils.isBoolean(false));

		assertTrue(Utils.isBoolean(Boolean.TRUE));
		assertTrue(Utils.isBoolean(Boolean.FALSE));

		assertTrue(Utils.isBoolean((Object) "true"));
		assertTrue(Utils.isBoolean((Object) "false"));

		assertFalse(Utils.isBoolean((Object) "nan"));
		assertFalse(Utils.isBoolean((Object) "hi"));
	}

	@Test
	void test_equalsOne() {
		assertTrue(Utils.equalsOne("test", "test", "test2"));
		assertTrue(Utils.equalsOne("test", "test2", "test"));

		assertFalse(Utils.equalsOne("test", "test2", "test3"));

		assertTrue(Utils.equalsOne(69, 69, 420));
		assertTrue(Utils.equalsOne(69, 420, 69));

		assertFalse(Utils.equalsOne(1337, 420, 69));

		assertTrue(Utils.equalsOne(new SameObject("test"), new SameObject("test"), new SameObject("test2")));
		assertTrue(Utils.equalsOne(new SameObject("test"), new SameObject("test2"), new SameObject("test")));

		assertFalse(Utils.equalsOne(new SameObject("test"), new SameObject("test2"), new SameObject("test3")));
	}

	@Test
	void test_equalsOneIgnoreCase() {
		assertTrue(Utils.equalsOneIgnoreCase("test", "test", "test2"));
		assertTrue(Utils.equalsOneIgnoreCase("test", "test2", "test"));
		assertTrue(Utils.equalsOneIgnoreCase("test", "TEST", "test2"));
		assertTrue(Utils.equalsOneIgnoreCase("test", "test2", "TEST"));

		assertFalse(Utils.equalsOneIgnoreCase("test", "test2", "test3"));
	}

	@Test
	void test_getPermissionString_Permission() {
		assertEquals("```\n" + "- " + Permission.ADMINISTRATOR.getName() + "\n- " + Permission.MESSAGE_MANAGE.getName() + "\n```",
			Utils.getPermissionString(Permission.ADMINISTRATOR, Permission.MESSAGE_MANAGE));

		assertEquals("```\n" + "- " + Permission.ADMINISTRATOR.getName() + "\n- " + Permission.MESSAGE_MANAGE.getName() + "\n```",
			Utils.getPermissionString(Permission.MESSAGE_MANAGE, Permission.ADMINISTRATOR));

		assertEquals("```\n" + "- " + Permission.ADMINISTRATOR.getName() + "\n- " + Permission.MESSAGE_MANAGE.getName() + "\n```",
			Utils.getPermissionString(Permission.MESSAGE_MANAGE, Permission.ADMINISTRATOR, Permission.MESSAGE_MANAGE));

		assertEquals("```\n```", Utils.getPermissionString(new Permission[0]));
	}

	@Test
	void test_getPermissionString_CustomPermission() {
		assertEquals("```\n" + "- " + CustomPermission.ADMIN.getName() + "\n- " + CustomPermission.BAN_USAGE.getName() + "\n```",
			Utils.getCustomPermissionString(CustomPermission.ADMIN, CustomPermission.BAN_USAGE));

		assertEquals("```\n" + "- " + CustomPermission.ADMIN.getName() + "\n- " + CustomPermission.BAN_USAGE.getName() + "\n```",
			Utils.getCustomPermissionString(CustomPermission.BAN_USAGE, CustomPermission.ADMIN));

		assertEquals("```\n" + "- " + CustomPermission.ADMIN.getName() + "\n- " + CustomPermission.BAN_USAGE.getName() + "\n```",
			Utils.getCustomPermissionString(CustomPermission.BAN_USAGE, CustomPermission.ADMIN, CustomPermission.BAN_USAGE));

		assertEquals("```\n```", Utils.getCustomPermissionString(new CustomPermission[0]));
	}

	@Test
	void test_firstLetterUppercase() {
		assertEquals("Test", Utils.firstLetterUppercase("test"));
		assertEquals("Test", Utils.firstLetterUppercase("Test"));
		assertEquals("Test", Utils.firstLetterUppercase("TEST"));
		assertEquals("Test", Utils.firstLetterUppercase("tEST"));
		assertEquals("Test", Utils.firstLetterUppercase("tEsT"));
		assertEquals("Test", Utils.firstLetterUppercase("tESt"));
		assertEquals("Test", Utils.firstLetterUppercase("tEST"));
		assertEquals("", assertDoesNotThrow(() -> Utils.firstLetterUppercase("")));
		assertEquals("T", assertDoesNotThrow(() -> Utils.firstLetterUppercase("t")));
		assertEquals("Te", assertDoesNotThrow(() -> Utils.firstLetterUppercase("te")));
		assertNull(assertDoesNotThrow(() -> Utils.firstLetterUppercase(null)));
	}

	@Test
	void test_add() {
		@SuppressWarnings("unchecked")
		List<Object> mock = mock(List.class);
		Object obj = new Object() {
			@Override
			public boolean equals(Object obj) {
				return obj == this;
			}
		};

		assertSame(mock, Utils.add(mock, obj));
		verify(mock, times(1)).add(obj);
	}

	@Test
	void test_parse() {
		assertEquals(CustomPermission.ADMIN, Utils.parse(CustomPermission.class, CustomPermission.ADMIN.getName()));
		assertEquals(CustomPermission.ADMIN, Utils.parse(CustomPermission.class, CustomPermission.ADMIN.getName().toLowerCase(Locale.ROOT)));
		assertNull(Utils.parse(CustomPermission.class, "thissurelyisn'tapermissionright?"));
	}

	@Test
	void test_makeWebhookClient() {
		Webhook mock = mock(Webhook.class);
		when(mock.getUrl()).thenReturn("https://discord.com/api/webhooks/1234567890/abcdefghijklmnopqrstuvwxyz");
		WebhookClient client = Utils.makeWebhookClient(mock);
		assertNotNull(client);
		client.close();
	}

	@Test
	void test_getWebhook_inList() {
		List<Webhook> webhooks = new ArrayList<>();
		Webhook webhook1 = mock(Webhook.class);
		Webhook webhook2 = mock(Webhook.class);
		webhooks.add(webhook1);
		webhooks.add(webhook2);

		when(webhook1.getName()).thenReturn("BlackOnion-Bot ContentModerator");
		when(webhook1.getOwner()).thenReturn(mock(Member.class));

		when(webhook2.getName()).thenReturn("Captain Hook");

		TextChannel mock = mock(TextChannel.class);
		Webhook webhook = Utils.getWebhook(mock, webhooks, w -> true);
		verify(mock, never()).createWebhook(any());
		assertNotNull(webhook);
		assertSame(webhook1, webhook);
	}

	@Test
	void test_getWebhook_notInList() {
		List<Webhook> webhooks = new ArrayList<>();
		Webhook webhook1 = mock(Webhook.class);
		Webhook webhook2 = mock(Webhook.class);
		webhooks.add(webhook1);
		webhooks.add(webhook2);

		when(webhook1.getName()).thenReturn("My WebHook");
		when(webhook2.getName()).thenReturn("Captain Hook");

		when(webhook1.getOwner()).thenReturn(mock(Member.class));
		when(webhook2.getOwner()).thenReturn(mock(Member.class));

		WebhookAction webhookAction = mock(WebhookAction.class, RETURNS_SELF);
		Webhook newWebhook = mock(Webhook.class);
		when(webhookAction.submit()).thenReturn(CompletableFuture.completedFuture(newWebhook));

		TextChannel channel = mock(TextChannel.class);
		when(channel.createWebhook(any())).thenReturn(webhookAction);

		Webhook webhook = Utils.getWebhook(channel, webhooks, w -> true);
		verify(channel, times(1)).createWebhook("BlackOnion-Bot ContentModerator");
		verify(webhookAction, times(1)).setAvatar(same(Bot.BLACKONION_ICON));
		verify(webhookAction, times(1)).submit();

		assertNotNull(webhook);
		assertSame(newWebhook, webhook);
		assertNotSame(webhook1, webhook);
		assertNotSame(webhook2, webhook);
	}

	@Test
	void test_optionsToJson() {
		List<OptionData> data = new ArrayList<>();

		OptionData o1 = new OptionData(OptionType.STRING, "name1", "Desc1", true);
		OptionData o2 = new OptionData(OptionType.SUB_COMMAND, "name2", "Desc2", false);

		data.add(o1);
		data.add(o2);

		JSONArray result = Utils.optionsToJson(data);
		assertEquals(2, result.length());

		for (int i = 0; i < result.length(); i++) {
			assertInstanceOf(JSONObject.class, result.get(i));
		}

		assertEquals(new JSONObject()
			.put("name", o1.getName())
			.put("description", o1.getDescription())
			.put("type", o1.getType().name())
			.put("required", o1.isRequired()).toString(), result.getJSONObject(0).toString());
		assertEquals(new JSONObject()
			.put("name", o2.getName())
			.put("description", o2.getDescription())
			.put("type", o2.getType().name())
			.put("required", o2.isRequired()).toString(), result.getJSONObject(1).toString());
	}

	@Test
	void test_optionsToJson_empty() {
		List<OptionData> data = new ArrayList<>();
		JSONArray result = Utils.optionsToJson(data);
		assertEquals(0, result.length());
	}

	@Test
	void test_optionsToJson_nullArgument() {
		JSONArray result = Utils.optionsToJson(null);

		assertNotNull(result);
		assertEquals(0, result.length());
	}

	@Test
	void test_formatDuration() {
		Duration duration = Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4);
		assertEquals("1 day 2 hours 3 minutes 4 seconds", Utils.formatDuration(duration));

		duration = Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4).plusMillis(5);
		assertEquals("1 day 2 hours 3 minutes 4 seconds", Utils.formatDuration(duration));

		duration = Duration.ofDays(1).plusHours(2).plusMinutes(3).plusSeconds(4).plusNanos(5);
		assertEquals("1 day 2 hours 3 minutes 4 seconds", Utils.formatDuration(duration));

		duration = Duration.ofDays(1);
		assertEquals("1 day", Utils.formatDuration(duration));

		duration = Duration.ofDays(2);
		assertEquals("2 days", Utils.formatDuration(duration));

		duration = Duration.ofDays(1).plusHours(1);
		assertEquals("1 day 1 hour", Utils.formatDuration(duration));

		duration = Duration.ofDays(1).plusHours(2);
		assertEquals("1 day 2 hours", Utils.formatDuration(duration));

		duration = Duration.ofDays(1).plusHours(2).plusMinutes(1);
		assertEquals("1 day 2 hours 1 minute", Utils.formatDuration(duration));

		duration = Duration.ofDays(1).plusHours(2).plusMinutes(2);
		assertEquals("1 day 2 hours 2 minutes", Utils.formatDuration(duration));

		duration = Duration.ofDays(1).plusHours(2).plusMinutes(2).plusSeconds(1);
		assertEquals("1 day 2 hours 2 minutes 1 second", Utils.formatDuration(duration));

		duration = Duration.ofDays(1).plusHours(2).plusMinutes(2).plusSeconds(2);
		assertEquals("1 day 2 hours 2 minutes 2 seconds", Utils.formatDuration(duration));
	}

	@Test
	void test_getDurationOptions() {
		OptionData[] options = Utils.getDurationOptions("ban");
		assertEquals(4, options.length);

		OptionData days = options[0];
		assertEquals("minutes", days.getName());
		assertEquals("The time of the ban in minutes", days.getDescription());
		assertEquals(OptionType.INTEGER, days.getType());
		assertFalse(days.isRequired());

		OptionData hours = options[1];
		assertEquals("hours", hours.getName());
		assertEquals("The time of the ban in hours", hours.getDescription());
		assertEquals(OptionType.INTEGER, hours.getType());
		assertFalse(hours.isRequired());

		OptionData minutes = options[2];
		assertEquals("days", minutes.getName());
		assertEquals("The time of the ban in days", minutes.getDescription());
		assertEquals(OptionType.INTEGER, minutes.getType());
		assertFalse(minutes.isRequired());

		OptionData seconds = options[3];
		assertEquals("weeks", seconds.getName());
		assertEquals("The time of the ban in weeks", seconds.getDescription());
		assertEquals(OptionType.INTEGER, seconds.getType());
		assertFalse(seconds.isRequired());
	}

	@Test
	void test_tryGet() {
		String result = assertDoesNotThrow(() -> Utils.tryGet(() -> {throw new IllegalArgumentException("Test");}));
		assertNull(result);

		result = assertDoesNotThrow(() -> Utils.tryGet(() -> "test"));
		assertEquals("test", result);

		SameObject obj = new SameObject();
		SameObject result2 = assertDoesNotThrow(() -> Utils.tryGet(() -> obj));
		assertSame(obj, result2);
	}

	private enum TestEnum {
		VALUE1,
		VALUE2,
		VALUE3
	}

	@Test
	void test_parseToT() {
		String value = "test";
		String result = Utils.parseToT(value, String.class);
		assertEquals(value, result);

		value = "1";
		int result2 = Utils.parseToT(value, int.class);
		assertEquals(1, result2);

		value = "1";
		Integer result3 = Utils.parseToT(value, Integer.class);
		assertEquals(1, result3);

		value = "1";
		long result4 = Utils.parseToT(value, long.class);
		assertEquals(1, result4);

		value = "1";
		Long result5 = Utils.parseToT(value, Long.class);
		assertEquals(1, result5);

		value = "1";
		float result6 = Utils.parseToT(value, float.class);
		assertEquals(1, result6);

		value = "1";
		Float result7 = Utils.parseToT(value, Float.class);
		assertEquals(1, result7);

		value = "1";
		double result8 = Utils.parseToT(value, double.class);
		assertEquals(1, result8);

		value = "1";
		Double result9 = Utils.parseToT(value, Double.class);
		assertEquals(1, result9);

		value = "true";
		boolean result10 = Utils.parseToT(value, boolean.class);
		assertTrue(result10);

		value = "TRUE";
		boolean result10_upper = Utils.parseToT(value, boolean.class);
		assertTrue(result10_upper);

		value = "true";
		Boolean result11 = Utils.parseToT(value, Boolean.class);
		assertTrue(result11);

		value = "TRUE";
		Boolean result11_upper = Utils.parseToT(value, Boolean.class);
		assertTrue(result11_upper);

		value = "false";
		boolean result12 = Utils.parseToT(value, boolean.class);
		assertFalse(result12);

		value = "FALSE";
		boolean result12_upper = Utils.parseToT(value, boolean.class);
		assertFalse(result12_upper);

		value = "false";
		Boolean result13 = Utils.parseToT(value, Boolean.class);
		assertFalse(result13);

		value = "FALSE";
		Boolean result13_upper = Utils.parseToT(value, Boolean.class);
		assertFalse(result13_upper);

		value = TestEnum.VALUE1.name();
		TestEnum result14 = Utils.parseToT(value, TestEnum.class);
		assertEquals(TestEnum.VALUE1, result14);

		value = TestEnum.VALUE2.name();
		TestEnum result15 = Utils.parseToT(value, TestEnum.class);
		assertEquals(TestEnum.VALUE2, result15);

		value = TestEnum.VALUE3.name();
		TestEnum result16 = Utils.parseToT(value, TestEnum.class);
		assertEquals(TestEnum.VALUE3, result16);
	}

	@Test
	void test_parseToT_invalid() {
		String value = "test";
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, int.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, Integer.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, long.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, Long.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, float.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, Float.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, double.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, Double.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, boolean.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, Boolean.class));
		assertThrows(IllegalArgumentException.class, () -> Utils.parseToT(value, TestEnum.class));
	}

	@Test
	void test_parseToT_recursion() {
		Object result = assertTimeout(Duration.ofMillis(10), () -> Utils.parseToT("", UtilsTest.class));
		assertNull(result);
	}

	@Test
	void testReplaceException_nullArgumentsThrow() {
		assertThrows(NullPointerException.class, () -> Utils.replaceException(null, Exception.class, RuntimeException.class));
		assertThrows(NullPointerException.class, () -> Utils.replaceException(() -> null, null, RuntimeException.class));
		assertThrows(NullPointerException.class, () -> Utils.replaceException(() -> null, Exception.class, null));
		assertThrows(NullPointerException.class, () -> Utils.replaceException(() -> null, null, RuntimeException.class));
		assertThrows(NullPointerException.class, () -> Utils.replaceException(null, null, null));
	}

	@Test
	void testReplaceException_sameException() {
		NullPointerException exception = new NullPointerException();
		Throwable thrown = assertThrows(Exception.class, () -> Utils.replaceException(() -> {
			throw exception;
		}, NullPointerException.class, NullPointerException.class));
		assertSame(exception, thrown);
	}
}
