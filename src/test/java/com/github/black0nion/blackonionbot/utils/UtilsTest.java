package com.github.black0nion.blackonionbot.utils;

import com.github.black0nion.blackonionbot.misc.enums.CustomPermission;
import net.dv8tion.jda.api.Permission;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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

	private static final class TestClass {
		private final String test;

		public TestClass(String test) {
			this.test = test;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			TestClass testClass = (TestClass) o;
			return Objects.equals(test, testClass.test);
		}

		@Override
		public int hashCode() {
			return Objects.hash(test);
		}
	}

	@Test
	void test_equalsOne() {
		assertTrue(Utils.equalsOne("test", "test", "test2"));
		assertTrue(Utils.equalsOne("test", "test2", "test"));

		assertFalse(Utils.equalsOne("test", "test2", "test3"));

		assertTrue(Utils.equalsOne(69, 69, 420));
		assertTrue(Utils.equalsOne(69, 420, 69));

		assertFalse(Utils.equalsOne(1337, 420, 69));

		assertTrue(Utils.equalsOne(new TestClass("test"), new TestClass("test"), new TestClass("test2")));
		assertTrue(Utils.equalsOne(new TestClass("test"), new TestClass("test2"), new TestClass("test")));

		assertFalse(Utils.equalsOne(new TestClass("test"), new TestClass("test2"), new TestClass("test3")));
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
			Utils.getPermissionString(CustomPermission.ADMIN, CustomPermission.BAN_USAGE));

		assertEquals("```\n" + "- " + CustomPermission.ADMIN.getName() + "\n- " + CustomPermission.BAN_USAGE.getName() + "\n```",
			Utils.getPermissionString(CustomPermission.BAN_USAGE, CustomPermission.ADMIN));

		assertEquals("```\n" + "- " + CustomPermission.ADMIN.getName() + "\n- " + CustomPermission.BAN_USAGE.getName() + "\n```",
			Utils.getPermissionString(CustomPermission.BAN_USAGE, CustomPermission.ADMIN, CustomPermission.BAN_USAGE));

		assertEquals("```\n```", Utils.getPermissionString(new CustomPermission[0]));
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
