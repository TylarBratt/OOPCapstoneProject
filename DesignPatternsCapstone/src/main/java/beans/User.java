package beans;

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
	public User(long id, String userName, String password, long credits, Role role){
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.credits = credits;
		this.role = role;
	}
	
}
