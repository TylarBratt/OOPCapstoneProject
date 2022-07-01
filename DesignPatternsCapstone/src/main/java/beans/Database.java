package beans;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.exception.BidTooLowException;
import beans.exception.InvalidInputException;

public class Database {
	public Connection connection = null;

	public Database() {
		
		// Get connection to database.
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			//System.out.println("MySQL JDBC Driver Registered!");

			// Create database connection. Append allowMultiQueries flag if you need to
			// process multiple queries at a time.
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/" + Common.databaseSchema/* +"?allowMultiQueries=true" */,
					Common.databaseUser, Common.databasePassword);

		} catch (ClassNotFoundException e) {
			System.out.print("MySQL JDBC driver not found!");
			e.printStackTrace();
		} catch (SQLException e) {
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

	/*
	 * Logs in a user if the provided credentials match an entry in the user
	 * database.
	 */
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
			if (results.next())
				return new User(results);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Creates a new user account in the database and logs in with that user
	 * account.
	 * We must do this via a stored procedure because we need to create AND return
	 * an entry at the same time.
	 */
	public User createAccount(String userName, String password) {
		try {
			PreparedStatement statement = connection.prepareStatement("CALL insert_user(?,?,?,?);");
			statement.setString(1, userName);
			statement.setString(2, password);
			statement.setLong(3, Common.newUserCredits);
			statement.setString(4, User.Role.USER.name());
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				User user = new User(results);

				// Generate a series of test products for this user..
				for (int i = 0; i < Common.newUserProductCount; i++) {
					DefaultProductInfo productInfo = DefaultProductInfo.getRandomItem();
					this.createProduct(productInfo.description, productInfo.path, user.id);
				}
			}

			return login(userName, password);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	Product createProduct(String name, String imagePath, long ownerID) {
		try {
			PreparedStatement statement = connection.prepareStatement("CALL insert_product(?,?,?);"); // TODO: Make
																										// static?
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

	public List<Product> getProductsOwnedByUser(long userID) {
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

	public boolean createAuction(long userID, long productID, long startPrice, long durationMins) {
		// TODO: Make sure userID matches product owner ID.
		try {
			PreparedStatement statement = connection.prepareStatement("CALL make_auction(?,?,?);"); // TODO: Make
																									// static?
			statement.setLong(1, productID);
			statement.setLong(2, durationMins);
			statement.setLong(3, startPrice);
			ResultSet results = statement.executeQuery();

			if (results.next()) {
				System.out.println("Auction created successfully!");
				return true;
			}
			else return false;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Create auction failed.");
		}
		
		

	}

	public Auction getActiveAuctionForProduct(long productID) {
		for (Auction auction : getAuctions(false)) 
			if (auction.productID == productID)
				return auction;
		
		
		// No matching auction found.
		return null;
	}

	/*
	 * Returns a list of auctions from the database. If includeInactive is FALSE then
	 * only active auctions will be returned.
	 */
	public List<Auction> getAuctions(boolean includeInactive) {
		List<Auction> auctions = new ArrayList<Auction>();
		
		try {
			StringBuilder sql = new StringBuilder();
				sql.append("SELECT * FROM auction"
						+ "	LEFT JOIN product ON auction.product_id = product.id"
						+ " LEFT JOIN user AS owner ON product.owner_id = owner.id"
						+ " LEFT JOIN bid ON bid.auction_id = auction.id"
						+ "	LEFT JOIN bid AS higher_bid ON higher_bid.auction_id = bid.auction_id AND bid.ammount < higher_bid.ammount"
						+ " WHERE higher_bid.id IS NULL");
			if (!includeInactive)
				sql.append(" AND auction.is_active = 1");
			
			PreparedStatement st = connection.prepareStatement(sql.toString());
			ResultSet results = st.executeQuery(sql.toString());
			
			//For each row in the result, create a new auction object and add it to the list of auctions..
			while (results.next())
				auctions.add(new Auction(results));
			
			return auctions;
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error getting auctions.");
		}
		
	}
	/*
	 * Returns all auctions.
	 */
	public List<Auction> getAuctions(){
		return getAuctions(true);
	}
	/*
	 * Returns all active auctions.
	 */
	public List<Auction> getActiveAuctions(){
		return getAuctions(false);
	}
	
	

	public int getProductWithName(String name) throws SQLException {
		PreparedStatement stat = connection.prepareStatement("SELECT id FROM product WHERE name LIKE ?");
		stat.setString(1, name);
		ResultSet srs = stat.executeQuery();
		srs.next();
		int id = srs.getInt("id");
		return id;
	}

	public void makeBid(Auction auction, Long userID, Integer amount) throws BidTooLowException, InvalidInputException {
		if (userID == null)
			throw new RuntimeException("Error making bid. UserID is null.");
		
		if (amount == null)
			throw new InvalidInputException();
		
		//Make sure bid is at least 1 credit higher than the highest bid...
		boolean isReserveMet = amount >= auction.startPrice;
		boolean isHighestBid = auction.highBid == null || amount > auction.highBid;
		if (!isReserveMet || !isHighestBid) 
			throw new BidTooLowException();
		
		
		//Calculate the ID for the for the new bid by adding 1 to the highest bid id in the database.
		//TODO: Combine this all into a single SQL stored procedure, since a race condition for the ID can occur if two people try creating a bid at the same time.
		int bidID = getnewID()+1;
		
		//Obtain the current date in a valid SQL format
		String date = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now());
		
		PreparedStatement statement;
		try {
			
			statement = connection.prepareStatement("INSERT INTO bid VALUES(?, ?, ?, ?, ?)");
			statement.setInt(1, bidID);
			statement.setInt(2, amount);
			statement.setLong(3, userID);
			statement.setString(4, date);
			statement.setLong(5, auction.id);

			statement.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Error making bid!");
		}
		

	}
	
	public Auction getAuctionWithID(long id) {
		for (Auction auction : getAuctions()) {
			if (auction.id == id)
				return auction;
		}
		throw new RuntimeException("Error getting auction with ID "+id+". Auction not found.");
	}

	public int getnewID() {
		PreparedStatement stat;
		try {
			stat = connection.prepareStatement("SELECT MAX(id) AS id FROM bid");
			ResultSet result = stat.executeQuery();
			result.next();
			int id = result.getInt("id");
			return id;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Error getting new bid id");
		}
		
	}
	
	/*
	 * Returns the current user info from the database for the user matching userID.
	 */
	public User getUser(long userID) {
		ResultSet results = null;
		try {
			String query = "SELECT * FROM user WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setLong(1, userID);
			results = statement.executeQuery();

			// If a user was returned as a result, then login was successful.
			// Store the userID to the current session to indicate the user has logged in.
			if (results.next())
				return new User(results);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	void processFinishedAuctions() {
		System.out.print("Processing finished auctions..");
		try {
			PreparedStatement statement = connection.prepareStatement("CALL process_finished_auctions();"); 
			
			statement.execute();
			System.out.println(" DONE!");

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Process finished auctions failed!");
		}
	}

}
