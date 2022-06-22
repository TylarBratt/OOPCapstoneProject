package beans;

public class NavbarGenerator {
	public enum Gravity {
		LEFT,
		RIGHT;
	}
	public static class NavbarItem {
		public final String title;
		public final String path;
		public final Gravity gravity;
		public NavbarItem(String title, String path, Gravity gravity) {
			this.title = title;
			this.path = path;
			this.gravity = gravity;
		}
		public String getHTML(String currentPath) {
			StringBuilder html = new StringBuilder();
			html.append("<li style=\"float:");
			//Float to the left/right depending on gravity..
			
			html.append(gravity);
			
			html.append("\"><a ");
			//If the item has a path matching currentPath, then highlight it.
			if (path.equals(currentPath)) 
				html.append("class=\"active\" ");
			
			html.append("href=\"");
			html.append(path);
			html.append("\">");
			html.append(title);
			html.append("</a></li>");
			return html.toString();
		}
	}
	
	public String getNavbarHTML(String navbarType, String currentPath) {
		StringBuilder html = new StringBuilder();
		//Start an unordered list
		html.append("<ul class=\"navbar\">");
		
		//Add a list item for each navbar item. We do it in reverse so items get floated in correct order.
		NavbarItem[] items = getNavbarItems(navbarType);
		
		//Add left floating items first..
		for (int i = 0; i < items.length; i++) 
			if (items[i].gravity.equals(Gravity.LEFT))
			html.append(items[i].getHTML(currentPath));
		
		//Add right floating items in reverse (so they appear on screen in the order they were listed)
		for (int i = items.length-1; i >= 0; i--) 
			if (items[i].gravity.equals(Gravity.RIGHT))
			html.append(items[i].getHTML(currentPath));
			
		
		//Close the unordered list.
		html.append("</ul>");
		
		return html.toString();
	}
	
	private NavbarItem[] getNavbarItems(String navbarType) {
		switch (navbarType) {
		case "loggedIn":
			return new NavbarItem[] {new NavbarItem("Auctions", "home", Gravity.LEFT), 
									new NavbarItem("Account", "account", Gravity.RIGHT),
									new NavbarItem("Logout", "logout", Gravity.RIGHT)};
		case "default":
			return new NavbarItem[] {new NavbarItem("Login", "login", Gravity.RIGHT)};
		default:
			throw new RuntimeException("Navbar generator: no navbar template exists for \""+navbarType+"\"");
		}
		
	}
	
	
	
}
