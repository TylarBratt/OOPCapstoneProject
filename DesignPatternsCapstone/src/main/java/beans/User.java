package beans;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
	public enum Role {
		USER,
		ADMIN
	}

	public final long id;
	public final String userName;
	public final String password;
	public final long credits;
	public final Role role;

	public User(ResultSet data) throws SQLException {
		this.id = data.getLong("id");
		this.userName = data.getString("username");
		this.password = data.getString("password");
		this.credits = data.getLong("credits");
		this.role = User.Role.valueOf(data.getString("role"));

	}

	public long getid() {
		return id;
	}
	
	public String getMaskedUserName() {
		char first = userName.charAt(0);
		char last = userName.charAt(userName.length()-1);
		return first + "******" + last;
	}
}
