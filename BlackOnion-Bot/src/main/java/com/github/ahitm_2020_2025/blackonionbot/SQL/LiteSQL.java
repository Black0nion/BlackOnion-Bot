package com.github.ahitm_2020_2025.blackonionbot.SQL;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
			
			System.out.println("[SQLite] connected");
			
			statement = connection.createStatement();
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void disconnect() {
		try {
			if (connection != null) {
				connection.close();
				System.out.println("[SQLite] disconnected");
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
