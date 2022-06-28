package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Auction {
	public final long id;
	public final long productID;
	public final String productName;
	public final long ownerID;
	public final Date startDate;
	public final long startPrice;
	public final long durationMins;
	public final Long highBid;
	public final Long highBidderID;
	public final boolean isActive;
	
	public Auction(ResultSet data) throws SQLException {
		id = data.getLong("auction.id");
		productID = data.getLong("product.id");
		ownerID = data.getLong("owner.id");
		startDate = data.getDate("start_date");
		startPrice = data.getLong("start_price");
		durationMins = data.getLong("duration_mins");
		
		//Try to obtain the high bid amount, since there may not yet be any bids. 
		Long highBid;
		try {
			highBid = Long.parseLong(data.getString("bid.ammount"));
		} catch (NumberFormatException e) {
			highBid = null;
		}
		this.highBid = highBid;
		
		
		//Try to obtain the user ID of the high bidder, since there may not yet be any bids..
		Long highBidderID;
		try {
			highBidderID = Long.parseLong(data.getString("bid.user_id"));
		} catch (NumberFormatException e) {
			highBidderID = null;
		}
		this.highBidderID = highBidderID;
		
		productName = data.getString("product.name");
		isActive = data.getInt("is_active") == 1;
	}
	
	public boolean hasBid() {
		return highBid != null;
	}
}
