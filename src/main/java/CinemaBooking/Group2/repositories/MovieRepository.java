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

import CinemaBooking.Group2.models.Movie;

@Repository
public class MovieRepository {	
    private final DataSource dataSource;
    
    @Autowired
	public MovieRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public List<Movie> getAllMovie()  {
		String sql = "SELECT * FROM movie";
		List<Movie> movies = new ArrayList<>();
		try(Connection conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()) {
			while(rs.next()) {
				Movie movie = new Movie();
                movie.setId(rs.getInt("id"));
                movie.setTitle(rs.getString("title"));
                movie.setShortDescription(rs.getString("short_description"));
                movie.setDescription(rs.getString("description"));
                movie.setDurationMinutes(rs.getInt("duration_minutes"));
                movie.setGenre(rs.getString("genre"));
                movie.setLanguage(rs.getString("language"));
                movie.setFormat(rs.getString("format"));
                movie.setDirector(rs.getString("director"));
                movie.setCast(rs.getString("cast"));
                movie.setPosterUrl(rs.getString("poster_url"));
                movie.setBannerUrl(rs.getString("banner_url"));
                movie.setTrailerUrl(rs.getString("trailer_url"));
                movie.setReleaseDate(rs.getDate("release_date"));
                movie.setEndDate(rs.getDate("end_date"));
                // tuỳ kiểu cột status trong DB mà map:
                // movie.setStatus(Movie.MovieStatus.valueOf(rs.getString("status")));
                movie.setCreatedAt(rs.getTimestamp("created_at"));

                movies.add(movie);
			}
		}
		catch(SQLException e) {
	        throw new RuntimeException("Lỗi khi lấy danh sách movie từ database", e);
		}
		return movies;
	}
	
	// function get all data movie if status = comming_soon
	public List<Movie> getAllMovieCommingSoon(int page, int perPage) {
	    if (page < 1) page = 1;
	    if (perPage < 1) perPage = 10;

	    int offset = (page - 1) * perPage;

	    String sql =
	        "SELECT id, title, short_description, duration_minutes, status " +
	        "FROM movie " +
	        "WHERE status IN ('COMING_SOON', 'NOW_SHOWING') " +
	        "ORDER BY id DESC " +
	        "LIMIT ? OFFSET ?;";

	    List<Movie> movies = new ArrayList<>();

	    try (Connection conn = dataSource.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        // set param trước khi executeQuery
	        ps.setInt(1, perPage);
	        ps.setInt(2, offset);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Movie movie = new Movie();
	                movie.setId(rs.getInt("id"));
	                movie.setTitle(rs.getString("title"));
	                movie.setShortDescription(rs.getString("short_description"));
	                movie.setDurationMinutes(rs.getInt("duration_minutes"));
	                movie.setStatus(Movie.MovieStatus.valueOf(rs.getString("status")));
	                movies.add(movie);
	            }
	        }

	    } catch (SQLException e) {
	        throw new RuntimeException("Lỗi khi lấy danh sách movie theo status", e);
	    }

	    return movies;
	}
	
	// total page movie 
	public int countMovieComingSoonNowShowing() {
	    String sql = "SELECT COUNT(*) FROM movie WHERE status IN ('COMING_SOON', 'NOW_SHOWING')";
	    try (Connection conn = dataSource.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        rs.next();
	        return rs.getInt(1);

	    } catch (SQLException e) {
	        throw new RuntimeException("Lỗi đếm movie", e);
	    }
	}

	 
	// function get movie by id and show detail
	public Movie getMovieDetailById(int id) {
	    String sql = "SELECT * FROM movie WHERE id = ?";

	    try (Connection conn = dataSource.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, id);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                Movie movie = new Movie();
	                movie.setId(rs.getInt("id"));
	                movie.setTitle(rs.getString("title"));
	                movie.setShortDescription(rs.getString("short_description"));
	                movie.setDescription(rs.getString("description"));
	                movie.setDurationMinutes(rs.getInt("duration_minutes"));
	                movie.setGenre(rs.getString("genre"));
	                movie.setLanguage(rs.getString("language"));
	                movie.setFormat(rs.getString("format"));
	                movie.setDirector(rs.getString("director"));
	                movie.setCast(rs.getString("cast"));
	                movie.setPosterUrl(rs.getString("poster_url"));
	                movie.setBannerUrl(rs.getString("banner_url"));
	                movie.setTrailerUrl(rs.getString("trailer_url"));
	                movie.setReleaseDate(rs.getDate("release_date"));
	                movie.setEndDate(rs.getDate("end_date"));
	                movie.setCreatedAt(rs.getTimestamp("created_at"));

	                String statusStr = rs.getString("status");
	                if (statusStr != null) {
	                    movie.setStatus(Movie.MovieStatus.valueOf(statusStr));
	                }

	                return movie;
	            }
	            return null;
	        }

	    } catch (SQLException e) {
	        throw new RuntimeException("Error while fetching movie detail by id=" + id, e);
	    }
	}

}
