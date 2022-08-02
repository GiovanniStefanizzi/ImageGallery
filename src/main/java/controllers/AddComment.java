package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.catalina.startup.Catalina;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Album;
import beans.Image;
import beans.User;
import dao.AlbumDAO;
import dao.CommentDAO;
import dao.ImageDAO;
import dao.UserDAO;
import utility.ConnectionHandler;


@WebServlet("/Comments")
public class AddComment extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;
	
	public AddComment() {
		// TODO Auto-generated constructor stub
	}
	
	public void init() throws ServletException {
		ServletContext context = getServletContext();
		this.templateEngine = ConnectionHandler.instance.startTemplate(context);
		connection = ConnectionHandler.instance.ConnectDb(context);
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer imageId = null;
		Integer albumId = null;
		User user = null;
		try {
			imageId = Integer.parseInt(request.getParameter("image"));
			albumId = Integer.parseInt(request.getParameter("album"));
			user = .parseInt(request.getParameter("user"));
			//int selectedImg = Integer.parseInt(request.getParameter("img"));
		} catch (NumberFormatException | NullPointerException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		if(imageId == null || albumId == null || userId == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
		}
		
		CommentDAO commentDAO = new CommentDAO(connection);
		
		
		commentDAO.addComment(userDAO., getServletInfo());;
	}
	

}
