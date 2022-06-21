package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Auction {
	public final long id;
	public final long productID;
	public final long userID;
	public final Date startDate;
	public final long startPrice;
	public final long durationMins;
	
	public Auction(ResultSet data) throws SQLException {
		id = data.getLong("auction.id");
		productID = data.getLong("product.id");
		userID = data.getLong("user.id");
		startDate = data.getDate("start_date");
		startPrice = data.getLong("start_price");
		durationMins = data.getLong("duration_mins");
		
	}
}
