package servlets;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServlet;

public class BaseServlet extends HttpServlet{
	
	String getFileText(String path) throws IOException {
		//Create a new buffered reader to load the file with.
		BufferedReader reader = new BufferedReader(new FileReader(path));
		StringBuffer out = new StringBuffer();
		String line = null;
		while ((line=reader.readLine())!=null) 
			out.append(line);
		
		reader.close();
		return out.toString();
	}
	
}
