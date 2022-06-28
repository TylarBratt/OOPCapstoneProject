package servlets;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import beans.Product;
import beans.User;

/**
 * Servlet implementation class BidServlet
 */
@WebServlet("/BidServlet")
public class BidServlet extends BaseServlet {

    public BidServlet() {
        super(true);
        // TODO Auto-generated constructor stub
    }

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long userID = (Long) request.getSession().getAttribute("user");

        if (userID == null)
            response.sendRedirect(request.getContextPath() + "/login");
        else
            try {
                response.getWriter().write(getHTML(request));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    private String getHTML(HttpServletRequest request) throws IOException, SQLException {
        int bidID = database.getnewID();

        bidID++;
        User user = database.getUser((Long) request.getSession().getAttribute("user"));
        
        long userid2 = user.getid();
        int userid = (int) userid2;
        String amount = request.getParameter("newestBid");
        int total = Integer.valueOf(amount);

        String auction_id = request.getParameter("id");

        int auctionid = Integer.valueOf(auction_id);

        // auction id for some reason has a weird / after it.

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String date = dtf.format(now);

        try {
            database.setBid(bidID, total, userid, date, auctionid);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return readFileText("html/bid.html", generateCSS(), "<h2>Bid Created</h2>");
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }

}
