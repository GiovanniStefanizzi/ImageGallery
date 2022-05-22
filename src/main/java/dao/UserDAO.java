package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;

public class UserDAO {

	private  Connection connection;
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	

	public User checkCredentials(String username, String password) throws SQLException {
		String query = "SELECT id, username, name, surname FROM users WHERE username = ? AND password = ?";
		try (PreparedStatement pStatement = connection.prepareStatement(query);) {
			pStatement.setString(1, username);
			pStatement.setString(2, password);
			try (ResultSet res = pStatement.executeQuery();) {
				if(!res.isBeforeFirst()) // no result
					return null;
				else {
					res.next();
					User user = new User();
					user.setId(res.getInt("id"));
					user.setUserName(res.getString("username"));
					user.setEmail(res.getString("email"));
					return user;
				}
			}
		}
	}

}
