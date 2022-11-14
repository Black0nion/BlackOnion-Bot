package com.github.black0nion.blackonionbot.database;

import com.github.black0nion.blackonionbot.utils.ThrowableSupplier;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSupplier extends ThrowableSupplier<Connection> {
	@Override
	Connection get() throws SQLException;
}
