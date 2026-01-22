package CinemaBooking.Group2.dtos.showtime;

import java.util.List;

public class MovieShowtimeGroupDtos {
	  private int movieId;
	    private String movieTitle;
	    private String coverUrl;
	    private List<ShowtimePublicDtos> showtimes;

	    public int getMovieId() { return movieId; }
	    public void setMovieId(int movieId) { this.movieId = movieId; }

	    public String getMovieTitle() { return movieTitle; }
	    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

	    public String getCoverUrl() { return coverUrl; }
	    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

	    public List<ShowtimePublicDtos> getShowtimes() { return showtimes; }
	    public void setShowtimes(List<ShowtimePublicDtos> showtimes) { this.showtimes = showtimes; }
}
