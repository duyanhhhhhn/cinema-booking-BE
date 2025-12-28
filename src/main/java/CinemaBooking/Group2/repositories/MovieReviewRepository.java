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

import CinemaBooking.Group2.dtos.movie_review.MovieReviewDtos;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.MovieReview;
import CinemaBooking.Group2.models.User;

@Repository
public class MovieReviewRepository { 
    private final DataSource dataSource;

	public MovieReviewRepository(DataSource dataSouce) {
		this.dataSource = dataSouce;
	}
	
	// This function help get all movies review by id movie (movie detail)-> role admin 
    public List<MovieReviewDtos> getAllReview() {
        String sql =
            "SELECT " +
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
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách movie review từ database", e);
        }

        return movieReviews;
    }
    
    // This function get all review show on frontend -> role: client
    public List<MovieReview> getAllRating() {
    	try {
    		
    	}
    }

}
