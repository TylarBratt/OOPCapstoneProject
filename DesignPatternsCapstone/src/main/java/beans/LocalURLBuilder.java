package beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
/**
 * 
 * Simplifies the creation of URLs for files which are local to the server.
 */
public class LocalURLBuilder {
	private final String path;
	private List<Pair<String,String>> params = new ArrayList<>();
	private final HttpServletRequest request;
	public LocalURLBuilder(String path, HttpServletRequest req) {
		this.path = path;
		this.request = req;
	}
	public LocalURLBuilder addParam(String key, String value) {
		if (key == null)
			throw new RuntimeException("URL Param Key cannot be null");
		params.add(new Pair<>(key,value));
		
		return this;
	}
	public LocalURLBuilder addParam(String key) {
		return addParam(key, null);
	}
	
	
	@Override
	public String toString() {
		StringBuilder paramText = new StringBuilder();
		
		for (Pair<String, String> param : params) {
			//Add leading question mark if this is the first param, otherwise include an & separator.
			if (paramText.length() == 0)
				paramText.append("?");
			else
				paramText.append("&");
			
			//Replace spaces with the appropriate value
			String formattedKey = param.a.trim().replace(" ", "%20");
			paramText.append(formattedKey);
			
			if (param.b != null) {
				String formattedValue = param.b.trim().replace(" ", "%20");
				paramText.append("=");
				paramText.append(formattedValue);
			}
		}
		System.out.println("Param url: "+paramText.toString());
		return request.getContextPath()+"/"+path+paramText.toString();
	}
}