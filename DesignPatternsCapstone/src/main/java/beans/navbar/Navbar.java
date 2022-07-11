package beans.navbar;

public class Navbar {
	
	public enum Gravity {
		LEFT,
		RIGHT;
	}
	public static class Item {
		public final String title;
		public final String path;
		public final Gravity gravity;
		public Item(String title, String path, Gravity gravity) {
			this.title = title;
			this.path = path;
			this.gravity = gravity;
		}
		public String getHTML(String activePath) {
			StringBuilder html = new StringBuilder();
			html.append("<li style=\"float:");
			//Float to the left/right depending on gravity..
			
			html.append(gravity);
			
			html.append("\"><a ");
			//If the item has a path matching currentPath, then highlight it.
			if (activePath != null && path.equals(activePath)) 
				html.append("class=\"active\" ");
			
			html.append("href=\"");
			html.append(path);
			html.append("\">");
			html.append(title);
			html.append("</a></li>");
			return html.toString();
		}
	}
	
	private final Item[] items;
	
	public Navbar(Item[] items) {
		this.items = items;
	}
	
	public String getHTML(String currentPath) {
		StringBuilder html = new StringBuilder();
		//Start an unordered list
		html.append("<ul class=\"navbar\">");
			
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
	
	
}
