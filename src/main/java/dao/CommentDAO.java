package dao;

import java.sql.Connection;
import java.util.List;

import beans.Comment;

public class CommentDAO {
private Connection connection;
	
	public CommentDAO(Connection connection) {
		this.connection = connection;
	}
	
	
	//TODO
	public List<Comment> getComments(int idImage) {
		return null;
	}
	
	
	//TODO
	public void setComment() {
		
	}
	
	
	
}
