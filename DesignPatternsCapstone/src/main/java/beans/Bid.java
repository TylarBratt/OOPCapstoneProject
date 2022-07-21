package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Bid {
	public final long id;
	public final long amount;
	public final long userID;
	public final Timestamp time;
	public final long auctionID;
	
	public Bid(ResultSet queryResults) throws SQLException {
		id = queryResults.getLong("id");
		amount = queryResults.getLong("ammount");
		userID = queryResults.getLong("user_id");
		time = queryResults.getTimestamp("date");
		auctionID = queryResults.getLong("auction_id");
	}
}
