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

public interface Database {


	public void shutdown();

	/*
	 * Logs in a user if the provided credentials match an entry in the user
	 * database.
	 */
	public User login(String userName, String password);

	/*
	 * Creates a new user account in the database and logs in with that user
	 * account.
	 * We must do this via a stored procedure because we need to create AND return
	 * an entry at the same time.
	 */
	public User createAccount(String userName, String password);

	Product createProduct(String name, String imagePath, long ownerID);
	public List<Product> getProductsOwnedByUser(long userID);

	public Product getProductWithID(long productID);

	public boolean createAuction(long userID, long productID, long startPrice, long durationMins);

	public Auction getActiveAuctionForProduct(long productID);

	/*
	 * Returns all auctions.
	 */
	public List<Auction> getAuctions();
	/*
	 * Returns all active auctions.
	 */
	public List<Auction> getActiveAuctions();

	public int getProductWithName(String name) throws SQLException;

	public void makeBid(Auction auction, Long userID, Integer amount)
			throws BidTooLowException, InvalidInputException, InvalidBidderException, InsufficientFundsException;
	
	public Auction getAuctionWithID(long id);

	public int getnewID();

	/*
	 * Returns the current user info from the database for the user matching userID.
	 */
	public User getUser(Long userID);

	public List<Bid> getBidsForAuction(long auctionID);
	public ResultSet getBidswithPID(long id);

	void processFinishedAuctions();

	public int getAvailableCredits(long userID, Long auctionID);
	
	/*
	 * Returns all auctions which the user has bid on..
	 */
	public List<Auction> getParticipatingAuctions(long userID);
	
	/*
	 * Returns all auctions which the user has started.
	 */
	public List<Auction> getStartedAuctions(long userID);
	
	public void cancelAuction(long auctionID);
	
	public Timestamp getCurrentTimestamp();
}

