package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
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
import beans.Comment;
import beans.Image;
import beans.User;
import dao.AlbumDAO;
import dao.CommentDAO;
import dao.ImageDAO;
import utility.ConnectionHandler;



@WebServlet("/Album")
public class AlbumPage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	public AlbumPage() {
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
		
		Integer albumId = null;
		Integer currentPage = null;
		Integer imageIndex = null;
		
		try {
			albumId = Integer.parseInt(request.getParameter("album"));
			currentPage = Integer.parseInt(request.getParameter("page"));
			imageIndex = Integer.parseInt(request.getParameter("img"));
		} catch (NumberFormatException | NullPointerException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		if(albumId == null || currentPage == null || imageIndex == null ) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
		}
		
		AlbumDAO albumDao = new AlbumDAO(connection);
		CommentDAO commentDao = new CommentDAO(connection);
		//TODO: add CommentDAO, handle comments
		
		
		try {
			Album album = albumDao.getById(albumId);
			if(album == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Selected album does not exist");
			}
			int pageCount = albumDao.getPageCount(album.getId());
			if(currentPage < 1 || currentPage > pageCount) currentPage = 1;
			
			List<Image> images = albumDao.getFiveImages(album.getId(), currentPage);
			if(imageIndex < 0 || imageIndex > 5 || (images.size())<=imageIndex) imageIndex = -1;
			if(imageIndex > 5) imageIndex = -1;
			
			
			if(imageIndex >= 0) {
				List<Comment> comments = new ArrayList<Comment>();
		
				comments = commentDao.getComments(images.get(imageIndex).getId());
				images.get(imageIndex).addComments(comments);
			}
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());	
			
			ctx.setVariable("images", images);
			ctx.setVariable("imageIndex", imageIndex);
			ctx.setVariable("currentPage", currentPage);
			ctx.setVariable("pageCount", pageCount);
			ctx.setVariable("album", album);
			ctx.setVariable("source", System.getProperty("resources.images"));
			
			String path = "/WEB-INF/album.html"; 
			
			templateEngine.process(path, ctx, response.getWriter());
			
		}
		catch(SQLException e){
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error, resources not found");
		}
	}
	
	
}	
	

