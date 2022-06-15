package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import beans.Common;
import beans.Log;
import beans.User;
import beans.UserRole;

public class Database {
	public Connection connection = null;
	
	public Database() {
		//Get connection to database.
		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("MySQL JDBC Driver Registered!");
			
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+Common.databaseSchema, Common.databaseUser, Common.databasePassword);
			
		} catch (ClassNotFoundException e) {
			System.out.print("MySQL JDBC driver not found!");
			e.printStackTrace();
		}
		catch (SQLException e) {
			System.out.print("Connection failed! Check output console");
			e.printStackTrace();
		}
		
		if (connection != null) {
			System.out.println("Connection made to DB!");
		}
	}
	
	public void shutdown() {
		System.out.println("Shutting down MySQL connection...");

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public User login(String userName, String password) {
		ResultSet results = null;
		try {
			String loginQuery = "SELECT * FROM user WHERE username = ? AND password = ?";
			PreparedStatement statement = connection.prepareStatement(loginQuery);
			statement.setString(1, userName);
			statement.setString(2, password);
			results = statement.executeQuery();
			
			// If a user was returned as a result, then login was successful. 
			// Store the userID to the current session to indicate the user has logged in.
			if (results.next()) {
				return new User(results.getLong("id"),
						results.getString("username"),
						results.getString("password"),
						results.getLong("credits"),
						UserRole.valueOf(results.getString("role")));
				
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Log> getLogs() {

		List<Log> output = new ArrayList<>();
		
		try {
			String sqlRequest = "SELECT * FROM log;";
			Statement statement = connection.createStatement();
			ResultSet set = statement.executeQuery(sqlRequest);

			while (set.next()) {
				output.add(new Log(set.getLong("id"),set.getString("title"), set.getString("content"), set.getString("timestamp")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		return output;
	}
	
	public void removeLog(long index) {
		try {
			//DELETE FROM table_name WHERE condition;
			String insertQuery = "DELETE FROM log WHERE id=?;";
			
			PreparedStatement statement = connection.prepareStatement(insertQuery);
			statement.setLong(1, index);
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " rows deleted.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean addLog(String title, String content) {
		//Create a new log object. We can set ID and timestamp to null because these will be automatically set by the database.
		Log log = new Log(null, title, content, null);
		int rowsAffected = 0;
		try {
			String insertQuery = "INSERT INTO log (title,content,timestamp) VALUES (?,?,CURRENT_TIMESTAMP())";
			
			PreparedStatement statement = connection.prepareStatement(insertQuery);
			statement.setString(1, log.title);
			statement.setString(2, log.content);
			rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " rows updated.");
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void updateLog(long index, String title, String content) {
		
		try {
			String insertQuery = "UPDATE log SET title=?, content=? WHERE id=?;";
			
			PreparedStatement statement = connection.prepareStatement(insertQuery);
			statement.setString(1, title);
			statement.setString(2, content);
			statement.setLong(3, index);
			int rowsAffected = statement.executeUpdate();
			System.out.println(rowsAffected + " rows updated.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	

}
