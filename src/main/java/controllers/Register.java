package controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.lang.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import beans.User;
import dao.UserDAO;
import utility.ConnectionHandler;

@WebServlet("/Register")

public class Register extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public Register() {
		super();
	}
	
	Connection connection;
	private TemplateEngine templateEngine;
	
	public void init() throws ServletException{
		ServletContext context = getServletContext();
		this.templateEngine = ConnectionHandler.instance.startTemplate(context);
		connection = ConnectionHandler.instance.ConnectDb(context);
	}
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "/index.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}
	
	

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = null;
		String email = null;
		String password = null;
		String repeatedPassword = null;
	
		
		ServletContext servletContext = getServletContext();
		
		
		// Get and escape request parameters
		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		repeatedPassword = StringEscapeUtils.escapeJava(request.getParameter("repeatedpassword"));
		
		if (username == null || password == null || username.isEmpty() || password.isEmpty()
		|| email == null || repeatedPassword == null || email.isEmpty() || repeatedPassword.isEmpty()) {
			
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		//check if password and repeatedPassword match
		boolean passwordNotMatching = false;
		if(!password.equals(repeatedPassword)) {
			passwordNotMatching = true;
			ctx.setVariable("passwordErrorMsg", "passwords don not match");
		}
		
		//email format validation
		boolean wrongEmailFormat = false;
	    String regex = "^(.+)@(.+)$";  
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(email); 
	    if(!matcher.matches()) {
	    	wrongEmailFormat = true;
	    	ctx.setVariable("emailErrorMsg", "wrong email format");
	    }
	    
	    //check if username is already taken
		UserDAO userDao = new UserDAO(connection);
		boolean alreadyTakenUsername = false;
		try {
			alreadyTakenUsername = userDao.alreadyTakenUserName(username);
		} catch (SQLException e) {
			
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
			return;
		}
		if(alreadyTakenUsername) ctx.setVariable("usernameErrorMsg", "username is already taken");
		
		
		
		//check if there's an existing account with the email
		boolean alreadyTakenEmail = false;
		try {
			alreadyTakenEmail = userDao.alreadyTakenEmail(email);
		} catch (SQLException e) {
			
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
			return;
		}
		if(alreadyTakenEmail) ctx.setVariable("existingAccountErrorMsg", "there is an existing account with this email ");
		
		if(wrongEmailFormat || alreadyTakenUsername || alreadyTakenEmail || passwordNotMatching) {
			String path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		//insert the user in the database
		User user = null;
		try {
			user = userDao.registerUser(username, email, password);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in database registration process");
			return;
		}

		// If the user exists, add info to the session and go to home page, otherwise
		// return an error message
		if (user == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in database registration process");
			return;
		} else {
			request.getSession().setAttribute("user", user);
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}

	}
	
	
	
}

