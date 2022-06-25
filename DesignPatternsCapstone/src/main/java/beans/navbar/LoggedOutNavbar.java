package beans.navbar;


public class LoggedOutNavbar extends Navbar {

	public LoggedOutNavbar() {
		super(new Item[] {new Item("Login", "login", Gravity.RIGHT)});
	}

}
