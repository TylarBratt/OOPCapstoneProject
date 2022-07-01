package beans.navbar;


public class LoggedOutNavbar extends Navbar {

	public LoggedOutNavbar() {
		super(new Item[] {new Item("Create Account", "signup", Gravity.RIGHT),
				new Item("Login", "login", Gravity.RIGHT)});
	}

}
