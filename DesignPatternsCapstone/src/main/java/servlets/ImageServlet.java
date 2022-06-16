package servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/images")
public class ImageServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//Image source file is specified in src parameter.
		String src = req.getParameter("src");
		
		ServletContext cntx= req.getServletContext();
		  //SOLUTION OBTAINED FROM https://stackoverflow.com/questions/8623709/output-an-image-file-from-a-servlet
	      // Get the absolute path of the image
	      String filename = cntx.getRealPath("images/"+src);
	      // retrieve mimeType dynamically
	      String mime = cntx.getMimeType(filename);
	      if (mime == null) {
	        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        return;
	      }

	      resp.setContentType(mime);
	      File file = new File(filename);
	      resp.setContentLength((int)file.length());

	      FileInputStream in = new FileInputStream(file);
	      OutputStream out = resp.getOutputStream();

	      // Copy the contents of the file to the output stream
	       byte[] buf = new byte[1024];
	       int count = 0;
	       while ((count = in.read(buf)) >= 0) {
	         out.write(buf, 0, count);
	      }
	    out.close();
	    in.close();
	}
	
}
