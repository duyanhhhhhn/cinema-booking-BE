package CinemaBooking.Group2.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.movie_review.admin.MovieReviewDtos;
import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.MovieReview;
import CinemaBooking.Group2.models.User;

@Repository
public class MovieReviewRepository { 
    private final DataSource dataSource;

	public MovieReviewRepository(DataSource dataSouce) {
		this.dataSource = dataSouce;
	}
	
	// This function help get all movies review by id movie (movie detail) -> role admin 
    public List<MovieReviewDtos> getAllReview() {
        String sql = "SELECT " +
		             "  u.email, u.full_name, " +
		             "  m.title, m.short_description, m.duration_minutes, m.genre, " +
		             "  mr.id AS review_id, mr.rating, mr.comment, mr.created_at " +
		             "FROM movie_review AS mr " +
		             "JOIN movie AS m ON m.id = mr.movie_id " +
		             "JOIN `user` AS u ON u.id = mr.user_id " +
		             "ORDER BY mr.created_at DESC, mr.id DESC";

        List<MovieReviewDtos> movieReviews = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MovieReviewDtos dto = new MovieReviewDtos();
                dto.setEmail(rs.getString("email"));
                dto.setFull_name(rs.getString("full_name"));
                dto.setTitle(rs.getString("title"));
                dto.setShort_description(rs.getString("short_description"));
                dto.setDuration_minutes(rs.getInt("duration_minutes"));
                dto.setGenre(rs.getString("genre"));
                dto.setId(rs.getInt("review_id"));
                dto.setRating(rs.getString("rating"));
                dto.setComment(rs.getString("comment"));
                dto.setCreated_at(rs.getTimestamp("created_at"));
                movieReviews.add(dto);
            }
        } 
        catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách movie review từ database role -> admin", e);
        }

        return movieReviews;
    }
    
    public long countByRoleId() {
        String sql =
            "SELECT COUNT(*) " +
            "FROM movie_review mr " +
            "JOIN `user` u ON u.id = mr.user_id " +
            "WHERE u.role_id = 2";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong(1) : 0L;

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi count movie_review theo role_id=2", e);
        }
    }


    
    // This function get all review show on frontend -> role: client
    public List<MovieReviewClientDtos> getAllRating(int limit, int offset) {
    	String sql =
    		    "SELECT mr.*, u.full_name, u.role_id " +
    		    "FROM movie_review AS mr " +
    		    "JOIN `user` AS u ON u.id = mr.user_id " +
    		    "WHERE u.role_id = 2 " +
    		    "ORDER BY mr.created_at DESC, mr.id DESC " +
    		    "LIMIT ? OFFSET ?";

    	List<MovieReviewClientDtos> list_mr = new ArrayList<>();
    	try(Connection conn = dataSource.getConnection();
    		PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
    		try(ResultSet rs = ps.executeQuery()) {
	    		while(rs.next()) {
	    			MovieReviewClientDtos dtos = new MovieReviewClientDtos();
	    			dtos.setId(rs.getInt("id"));
	    			dtos.setUserId(rs.getInt("user_id"));
	    			dtos.setMovieId(rs.getInt("movie_id"));
	    			dtos.setRating(rs.getInt("rating"));
	    			dtos.setComment(rs.getString("comment"));
	    			dtos.setCreatedAt(rs.getDate("created_at"));
	    			dtos.setFull_name(rs.getString("full_name"));
	    			dtos.setRole_id(rs.getInt("role_id"));
	    			list_mr.add(dtos);
	    		}
    		}
    	} 
    	catch(SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách movie review từ database role -> users", e);
    	}
    	return list_mr;
    }

}
