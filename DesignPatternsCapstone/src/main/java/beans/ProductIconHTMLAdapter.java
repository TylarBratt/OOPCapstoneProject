package beans;

import java.util.ArrayList;
import java.util.List;

public class ProductIconHTMLAdapter implements HTMLAdapter {

	final Product product;
	public ProductIconHTMLAdapter(Product product) {
		this.product = product;
	}
	@Override
	public String getFilePath() {
		return "html/product.html";
	}

	@Override
	public List<Object> getArguments() {
		List<Object> args = new ArrayList<>();
		args.add(product.imagePath);
		args.add(product.name);
		args.add(getExtraHTML());
		return args;
	}

	public String getExtraHTML() {
		return "";
	}
}
