package controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import javax.print.attribute.IntegerSyntax;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.Album;
import beans.User;
import dao.AlbumDAO;
import dao.ImageDAO;
import utility.ConnectionHandler;


@WebServlet("/Upload")
@MultipartConfig(
		  fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
		  maxFileSize = 1024 * 1024 * 10,      // 10 MB
		  maxRequestSize = 1024 * 1024 * 100   // 100 MB
		)
public class ImageUpload extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	
	public ImageUpload() {
		super();
	}
	
	Connection connection;
	private TemplateEngine templateEngine;
	
	public void init() throws ServletException{
		ServletContext context = getServletContext();
		this.templateEngine = ConnectionHandler.instance.startTemplate(context);
		connection = ConnectionHandler.instance.ConnectDb(context);
	}	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Integer albumId = null;
		try {
			albumId = Integer.parseInt(request.getParameter("albumId"));
		}
		catch(NumberFormatException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error getting albumId");
			
		}
		Part filePart = request.getPart("image");
		String title = StringEscapeUtils.escapeJava(request.getParameter("title"));
		String description = StringEscapeUtils.escapeJava(request.getParameter("description"));
		String path = System.getProperty("resources.images");
		String imagePath = path + "/WEB-INF/resources/";
		
		
		
		
		if (filePart == null || title == null || title.isEmpty() || description == null || description.isEmpty() || albumId == null){
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		 User user = (User) request.getSession().getAttribute("user");
		 String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		 File f = new File(imagePath + fileName);
		 try (OutputStream output = new FileOutputStream(f);  InputStream file = filePart.getInputStream()){
			 file.transferTo(output);
		 }
		 
		 AlbumDAO albumDAO = new AlbumDAO(connection);
		 ImageDAO imageDAO = new ImageDAO(connection);
		 
		 try {
			 Album album = albumDAO.getById(albumId);
			 if(album == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Album not found");
				return;
				
				//TODO: handle error
			 }

			 imageDAO.insertImage(title, description, albumId, imagePath);
			 
			path = getServletContext().getContextPath() + "Album?albumId="+album.getId()+"&page=1";
			response.sendRedirect(path);
			 
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "error in image creation");
			return;
		}
		 
		 
		
	}	
}
