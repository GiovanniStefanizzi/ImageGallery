package controllers;
import java.io.*;
import java.sql.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.*;
import utility.ConnectionHandler;

@WebServlet("/Servlet1")

public class Servlet1 extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	public Servlet1() {
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
		 
	}

}
