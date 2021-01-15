package com.github.black0nion.blackonionbot.SQL;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.github.black0nion.blackonionbot.Logger;
import com.github.black0nion.blackonionbot.enums.LogOrigin;

public class LiteSQL {
	private static Connection connection;
	
	private static Statement statement;
	
	public static void connect() {
		connection = null;
		
		try {
			File file = new File("files/database.db");
			file.createNewFile();
			
			String url = "jdbc:sqlite:" + file.getPath();
			connection = DriverManager.getConnection(url);
			
			Logger.logInfo("Connected.", LogOrigin.SQLITE);
			
			statement = connection.createStatement();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void disconnect() {
		try {
			if (connection != null) {
				connection.close();
				Logger.logWarning("Disconnected.", LogOrigin.SQLITE);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void onUpdate(String sql) {
		try {
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static ResultSet onQuery(String sql) {
		try {
			return statement.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("null");
		return null;
	}
}
