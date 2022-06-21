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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Album;
import beans.Image;
import beans.User;
import dao.AlbumDAO;
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
		
		try {
			albumId = Integer.parseInt(request.getParameter("album"));
			currentPage = Integer.parseInt(request.getParameter("page"));
			//int selectedImg = Integer.parseInt(request.getParameter("img"));
		} catch (NumberFormatException | NullPointerException e){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		if(albumId == null || currentPage == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
		}
		
		AlbumDAO albumDao = new AlbumDAO(connection);
		ImageDAO imageDao = new ImageDAO(connection);
		//TODO: add CommentDAO, handle comments
		
		
		try {
			Album album = albumDao.getById(albumId);
			if(album == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Selected album does not exist");
			}
			int pageCount = albumDao.getPageCount(album.getId());
			if(currentPage < 1 || currentPage > pageCount) currentPage = 1;
			
			
			List<Image> images = albumDao.getFiveImages(album.getId(), currentPage);
			
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());	
			
			ctx.setVariable("images", images);
			ctx.setVariable("currentPage", currentPage);
			ctx.setVariable("pageCount", pageCount);
			ctx.setVariable("album", album);
			
			String path = "/WEB-INF/Album.html"; 
			
			templateEngine.process(path, ctx, response.getWriter());
			
		}
		catch(SQLException e){
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error, resources not found");
		}
		
		
		
	}
	
	
}	
	

