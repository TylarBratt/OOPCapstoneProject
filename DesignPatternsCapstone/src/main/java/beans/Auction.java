package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Auction {
	public final long id;
	public final long productID;
	public final String productName;
	public final long userID;
	public final Date startDate;
	public final long startPrice;
	public final long durationMins;
	public final long maxBid;
	
	public Auction(ResultSet data) throws SQLException {
		id = data.getLong("auction.id");
		productID = data.getLong("product.id");
		userID = data.getLong("user.id");
		startDate = data.getDate("start_date");
		startPrice = data.getLong("start_price");
		durationMins = data.getLong("duration_mins");
		maxBid = data.getLong("max_bid");
		productName = data.getString("product.name");
	}
	
	public long getCurrentPrice() {
		//Return value of the max bid, if there is one, otherwise return the starting price.
		if (hasBid()) 
			return maxBid;
		else
			return startPrice;
	}
	
	public boolean hasBid() {
		return maxBid > 0;
	}
}
