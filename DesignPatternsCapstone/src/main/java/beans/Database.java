package beans;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import beans.exception.BidTooLowException;
import beans.exception.InsufficientFundsException;
import beans.exception.InvalidBidderException;
import beans.exception.InvalidInputException;

public class Database {
	public Connection connection = null;

	public Database(ServletContext servletContext) {

		// Get connection to database.
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			// System.out.println("MySQL JDBC Driver Registered!");

			String databaseSchema = servletContext.getInitParameter("databaseSchema");
			String databaseUser = servletContext.getInitParameter("databaseUser");
			String databasePassword = servletContext.getInitParameter("databasePassword");
			
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/" + databaseSchema/* +"?allowMultiQueries=true" */,
					databaseUser, databasePassword);

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
			} else
				return false;

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Create auction failed.");
		}

	}

	public Auction getActiveAuctionForProduct(long productID) {
		for (Auction auction : getActiveAuctions())
			if (auction.productID == productID)
				return auction;

		// No matching auction found.
		return null;
	}

	/*
	 * Returns all auctions.
	 */
	public List<Auction> getAuctions() {

		List<Auction> auctions = new ArrayList<Auction>();

		try {
			PreparedStatement statement = connection.prepareStatement("CALL get_auctions;"); // TODO: Make

			ResultSet results = statement.executeQuery();

			// For each row in the result, create a new auction object and add it to the
			// list of auctions..
			while (results.next())
				auctions.add(new Auction(results));

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error getting auctions.");
		}

		return auctions;

	}

	/*
	 * Returns all active auctions.
	 */
	public List<Auction> getActiveAuctions() {
		List<Auction> auctions = new ArrayList<Auction>();

		try {
			ResultSet results = connection.prepareStatement("CALL get_active_auctions;").executeQuery();
			// For each row in the result, create a new auction object and add it to the
			// list of auctions..
			while (results.next())
				auctions.add(new Auction(results));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error getting active auctions.");
		}

		return auctions;
	}

	public int getProductWithName(String name) throws SQLException {
		PreparedStatement stat = connection.prepareStatement("SELECT id FROM product WHERE name LIKE ?");
		stat.setString(1, name);
		ResultSet srs = stat.executeQuery();
		srs.next();
		int id = srs.getInt("id");
		return id;
	}

	public void makeBid(Auction auction, Long userID, Integer amount)
			throws BidTooLowException, InvalidInputException, InvalidBidderException, InsufficientFundsException {
		// Don't allow users to bid on an auction they created.
		if (auction.ownerID == userID)
			throw new InvalidBidderException();

		if (userID == null)
			throw new RuntimeException("Error making bid. UserID is null.");

		if (amount == null)
			throw new InvalidInputException();

		// Make sure bid is at least 1 credit higher than the highest bid...
		boolean isReserveMet = amount >= auction.startPrice;
		boolean isHighestBid = auction.highBid == null || amount > auction.highBid;
		if (!isReserveMet || !isHighestBid)
			throw new BidTooLowException();

		int availableCredits = getAvailableCredits(userID, auction.id);
		System.out.println("Available credits for bid: " + availableCredits);
		if ((availableCredits - amount) < 0)
			throw new InsufficientFundsException();

		try {
			PreparedStatement statement = connection.prepareStatement("CALL make_bid(?,?,?);");
			statement.setLong(1, auction.id);
			statement.setLong(2, userID);
			statement.setLong(3, amount);
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
		throw new RuntimeException("Error getting auction with ID " + id + ". Auction not found.");
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
	public User getUser(Long userID) {
		if (userID == null)
			return null;
		
		ResultSet results = null;
		try {
			PreparedStatement statement = connection.prepareStatement("CALL get_user_with_id(?)");
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

	public List<Bid> getBidsForAuction(long auctionID){
		List<Bid> bids = new ArrayList<>();
		try {
			String query = "SELECT * FROM bid WHERE auction_id = ? ORDER BY date DESC";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setLong(1, auctionID);
			ResultSet results = statement.executeQuery();
			
			while (results.next())
				bids.add(new Bid(results));
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Error getting bids for auction "+auctionID);
		}
		return bids;
	}
	public ResultSet getBidswithPID(long id) {
		ResultSet results;
		try {
			// "SELECT bid.id, bid.ammount, bid.user_id, 'name', 'date', auction_id,
			// auction.id, auction.product_id, product.id, product.name FROM bid LEFT JOIN
			// auction on auction_id = auction.id LEFT JOIN user ON bid.user_id = user.id
			// LEFT JOIN product ON auction.product_id = product.id WHERE product.id = ?"
			// this might be the better query
			String query = "SELECT * FROM bid LEFT JOIN auction on auction_id = auction.id LEFT JOIN user ON bid.user_id = user.id LEFT JOIN product ON auction.product_id = product.id WHERE product.id = ?";
			PreparedStatement statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			results = statement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Error getting bids with product id");
		}
		return results;
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

	public int getAvailableCredits(long userID, Long auctionID) {
		ResultSet results = null;
		try {
			CallableStatement statement = connection.prepareCall("CALL get_user_available_credits(?,?,?)");
			statement.setLong(1, userID);
			if (auctionID != null)
				statement.setLong(2, auctionID);
			else 
				statement.setNull(2,  Types.INTEGER);
			statement.registerOutParameter(3, java.sql.Types.INTEGER);
			statement.execute();

			return statement.getInt(3);

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error gettings active bid totals.");
		}
	}
	
	/*
	 * Returns all auctions which the user has bid on..
	 */
	public List<Auction> getParticipatingAuctions(long userID){
		List<Auction> auctions = new ArrayList<Auction>();
		
		try {
			PreparedStatement statement = connection.prepareStatement("CALL get_participating_auctions(?)");
			statement.setLong(1,userID);
			ResultSet results = statement.executeQuery();
			//For each row in the result, create a new auction object and add it to the list of auctions..
			while (results.next())
				auctions.add(new Auction(results));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error getting participating auctions.");
		}
		
		return auctions;
	}
	
	/*
	 * Returns all auctions which the user has started.
	 */
	public List<Auction> getStartedAuctions(long userID){
		List<Auction> auctions = new ArrayList<Auction>();
		
		try {
			PreparedStatement statement = connection.prepareStatement("CALL get_started_auctions(?)");
			statement.setLong(1,userID);
			ResultSet results = statement.executeQuery();
			//For each row in the result, create a new auction object and add it to the list of auctions..
			while (results.next())
				auctions.add(new Auction(results));
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error getting started auctions.");
		}
		
		return auctions;
	}
	
	public void cancelAuction(long auctionID) {
	
		try {
			CallableStatement statement = connection.prepareCall("CALL cancel_auction(?)");
			statement.setLong(1, auctionID);
			statement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Error cancelling auction.");
		}
	}
	
	public Timestamp getCurrentTimestamp() {
		PreparedStatement stat;
		try {
			stat = connection.prepareStatement("SELECT NOW();");
			ResultSet result = stat.executeQuery();
			if (!result.next())
				throw new RuntimeException("Error getting current date");
			return result.getTimestamp(1);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Error getting current date");
		}
	}
}

