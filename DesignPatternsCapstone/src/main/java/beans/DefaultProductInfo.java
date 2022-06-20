package beans;

import java.util.Random;

/**
 * Enums used for generating the random products which are assigned to a new user. 
 * 
 * TODO: Move this data to its own table in the database and make the 
 * database perform the generation of products via a stored procedure.
 */
public enum DefaultProductInfo {
	BURGER("burger.png", "Deluxe Burger"),
	BURGER_DOUBLE("burgerDouble.png", "Double Deluxe Burger"),
	BIRTHDAY_CAKE("cakeBirthday.png", "Birthday Cake"),
	CHERRIES("cherries.png", "Cherries"),
	DONUT("donutSprinkles.png", "Donut"),
	FRIES("fries.png", "French Fries"),
	SUSHI("sushiEgg.png", "Sushi"),
	TURKEY("turkey.png", "Turkey"),
	WINE("wineWhite.png", "Wine"),;
	
	public final String path;
	public final String description;
	
	DefaultProductInfo(String path, String description){
		this.path = path;
		this.description = description;
	}
	
	public static DefaultProductInfo getRandomItem() {
		Random r = new Random();
		return DefaultProductInfo.values()[r.nextInt(DefaultProductInfo.values().length)];
	}
}
