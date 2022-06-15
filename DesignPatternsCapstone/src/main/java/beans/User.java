package beans;

public class User {
	public final long id;
	public final String userName;
	public final String password;
	public final long credits;
	public final UserRole role;
	public User(long id, String userName, String password, long credits, UserRole role){
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.credits = credits;
		this.role = role;
	}
	
}
