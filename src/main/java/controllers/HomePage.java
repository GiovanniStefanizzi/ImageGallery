package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Album;
import beans.User;
import dao.AlbumDAO;
import utility.ConnectionHandler;



@WebServlet("/Home")
public class HomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	public HomePage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		ServletContext context = getServletContext();
		this.templateEngine = ConnectionHandler.instance.startTemplate(context);
		connection = ConnectionHandler.instance.ConnectDb(context);
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		AlbumDAO albumDao = new AlbumDAO(connection);
		List <Album> userAlbums = null;
		List <Album> otherAlbums = null;

		
		User user = (User)request.getSession().getAttribute("user");		
		int userId = user.getId();

		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());	
		
		//get the user's albums
		try {
			userAlbums = albumDao.getUserAlbums(userId);
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover albums");
			return;
		}
		ctx.setVariable("useralbums", userAlbums);
		
		//get the other users' albums
		try {
			otherAlbums = albumDao.getOtherAlbums(user.getId());
		}catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover albums");
			return;
		}
		ctx.setVariable("otheralbums", otherAlbums);
		String path = "/WEB-INF/Home.html";
		templateEngine.process(path, ctx, response.getWriter());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		User user = (User)request.getSession().getAttribute("user");		
		int userId = user.getId();
		
		String title;
		// Get and escape request parameters
		title = StringEscapeUtils.escapeJava(request.getParameter("title"));
		
		if(title.isEmpty()||title == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}	
		
		// create the album in the database
		AlbumDAO albumDao = new AlbumDAO(connection);
		try {
			albumDao.createAlbum(userId, title);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in album creation process");
			return;
		}
		String path = getServletContext().getContextPath() + "/Album";
		response.sendRedirect(path);
		
	}

}
