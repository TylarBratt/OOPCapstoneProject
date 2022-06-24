package beans.navbar;


public class LoggedInNavbar extends Navbar {

	public LoggedInNavbar() {
		super(new Item[] {new Item("Auctions", "home", Gravity.LEFT), 
						  new Item("Account", "account", Gravity.LEFT),
						  new Item("Logout", "logout", Gravity.RIGHT)});
	}

}
