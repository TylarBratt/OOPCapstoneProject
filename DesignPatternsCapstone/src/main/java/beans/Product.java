package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Product {
	public final long id;
	public final String name;
	public final long ownerID;
	public final String imagePath;
	
	public Product(ResultSet results) throws SQLException {
		id = results.getLong("id");
		name = results.getString("name");
		ownerID = results.getLong("owner_id");
		imagePath = results.getString("image_path");
	}
}
