package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Album;

public class AlbumDAO {
	private Connection connection;
	
	public AlbumDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Album> getUserAlbums(int ownerId) throws SQLException{
		List<Album> albums = new ArrayList<Album>();
		
		String query = "SELECT * FROM imagegallery.album"+
					   "WHERE ownerId = ?"+
						"ORDER BY date DESC";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
			
			preparedStatement.setInt(1, ownerId);
			
			try (ResultSet result = preparedStatement.executeQuery()) {
			
				while (result.next()){
					Album album = new Album();
					album.setId(result.getInt("idAlbum"));
					album.setDate(result.getDate("date"));
					album.setId(result.getInt("idAlbum"));
					album.setTitle(result.getString("title"));
					
					albums.add(album);
				}	
			}
            catch (SQLException e){
                connection.rollback();
                return null;
            }		
		}
		return albums;
	}
	
	
	public List<Album> getOtherAlbums(int ownerId) throws SQLException{
		List<Album> albums = new ArrayList();
		
		String query = "SELECT * FROM imagegallery.album"+
					   "WHERE ownerId != ?"+
						"ORDER BY date DESC";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
			
			preparedStatement.setInt(1, ownerId);
			
			try (ResultSet result = preparedStatement.executeQuery()) {
			
				while (result.next()){
					Album album = new Album();
					album.setId(result.getInt("idAlbum"));
					album.setDate(result.getDate("date"));
					album.setId(result.getInt("idAlbum"));
					album.setTitle(result.getString("title"));
					
					albums.add(album);
				}	
			}
            catch (SQLException e){
                connection.rollback();
                return null;
            }		
		}
		return albums;
	}
	
	
	
	
}
