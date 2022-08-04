package beans;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.ServletContext;

public class HTMLReader {
	ServletContext context;
	public HTMLReader(ServletContext context) {
		this.context = context;
	}
	public String readFile(String path, Object ...arguments) {
		try {
			//Create a new buffered reader to load the file with.
			BufferedReader reader = new BufferedReader(new FileReader(context.getRealPath(path)));
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
		} catch (IOException e) {
			e.printStackTrace();
			//Stop the program immediately an error occurs.
			throw new RuntimeException("Error reading file text!");
		}
	}
	
	public String readFile(HTMLAdapter htmlAdapter) {
		return readFile(htmlAdapter.getFilePath(), htmlAdapter.getArguments().toArray());
	}
}
