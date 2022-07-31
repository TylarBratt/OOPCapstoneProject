package servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

@WebServlet("/jsp")

public class JSPTestServlet extends JSPServletBase {

	public JSPTestServlet() {
		super("JSP Test", "/jsp/test.jsp", true, false);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getActiveNavbarItem() {
		return "";
	}

}
