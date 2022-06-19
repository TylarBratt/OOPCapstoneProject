package beans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


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
			String query = "SELECT * FROM user WHERE username = ? AND password = ?";
			PreparedStatement statement = connection.prepareStatement(query);
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
						User.Role.valueOf(results.getString("role")));
				
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public User createAccount(String userName, String password) {
		try {
			PreparedStatement statement = connection.prepareStatement("CALL insert_user(?,?,?,?);");
			statement.setString(1, userName);
			statement.setString(2, password);
			statement.setLong(3, Common.newUserCredits);
			statement.setString(4, User.Role.USER.name());
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				User user = new User(results.getLong("id"),results.getString("username"), results.getString("password"),results.getLong("credits"), User.Role.valueOf(results.getString("role")));
				
				//TODO: Generate a series of test products for this user..
				for (int i = 0; i < Common.newUserProductCount; i++) {
					DefaultProductInfo productInfo = DefaultProductInfo.getRandomItem();
					this.createProduct(productInfo.description , productInfo.path, user.id);
				}
			}
				
			return login(userName, password);			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}
	
	public List<Log> getLogs() {

		List<Log> output = new ArrayList<>();
		
		try {
			String query = "SELECT * FROM log;";
			ResultSet set = connection.createStatement().executeQuery(query);

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
	
	Product createProduct(String name, String imagePath, long ownerID) {
		try {
			PreparedStatement statement = connection.prepareStatement("CALL insert_product(?,?,?);"); //TODO: Make static?
			statement.setString(1, name);
			statement.setLong(2, ownerID);
			statement.setString(3, imagePath);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				System.out.println("Product created successfully!");
				return new Product(results);
				}
						
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<Product> getProductsOwnedByUser(long userID){
		List<Product> output = new ArrayList<>();
		
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM product WHERE owner_id = ?;");
			statement.setLong(1, userID);
			ResultSet set = statement.executeQuery();

			while (set.next()) {
				output.add(new Product(set));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		return output;
	}
	
	public Product getProductWithID(long productID) {
		
		try {
			PreparedStatement statement = connection.prepareStatement("SELECT * FROM product WHERE id = ?;");
			statement.setLong(1, productID);
			ResultSet set = statement.executeQuery();

			if (set.next()) 
				return new Product(set);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		
		return null;
	}

}
