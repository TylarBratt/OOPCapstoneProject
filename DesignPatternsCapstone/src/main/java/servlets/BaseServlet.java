package servlets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import beans.Database;

/**
 * 
 * A common interface for our servlets to inherit from. 
 * Simplifies common tasks such as database initialization and the loading of html files.
 *
 */
public class BaseServlet extends HttpServlet{
	public final boolean isUsingDatabase;
	Database database = null; //Only initialized if isUsingDatabase is true.
	
	@Override
	public void destroy() {
		//Shut down the database connection, if enabled..
		if (isUsingDatabase)
			database.shutdown();
		
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		//Initialize the database connection, if requested.
		if (isUsingDatabase) 
			database = new Database(this);
		
		super.init();
	}
	
	public BaseServlet(boolean isUsingDatabase) {
		this.isUsingDatabase = isUsingDatabase;
	}

	
	public String readFileText(String path, Object ...arguments) throws IOException {
		//Create a new buffered reader to load the file with.
		BufferedReader reader = new BufferedReader(new FileReader(getServletContext().getRealPath(path)));
		StringBuffer out = new StringBuffer();
		String line = null;
		while ((line=reader.readLine())!=null) 
			out.append(line);
		
		reader.close();
		
		//If there are additional arguments passed in, include them. Otherwise return the raw string.
		if (arguments.length > 0)
			return MessageFormat.format(out.toString(), arguments);
		else
			return out.toString();
	}
	
	public String generateCSS() throws IOException {
		return "<style>"+readFileText("/css/style.css")+"</style>";
	}
	
}
