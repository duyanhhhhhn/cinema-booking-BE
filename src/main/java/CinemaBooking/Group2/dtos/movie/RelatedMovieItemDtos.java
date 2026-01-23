package CinemaBooking.Group2.dtos.movie;

public class RelatedMovieItemDtos {
	 	private int id;
	    private String title;
	    private String posterUrl;
	    private Integer durationMinutes;
	    private String genre;
	    private String status;

	    public RelatedMovieItemDtos() {}

	    public RelatedMovieItemDtos(int id, String title, String posterUrl, Integer durationMinutes, String genre, String status) {
	        this.id = id;
	        this.title = title;
	        this.posterUrl = posterUrl;
	        this.durationMinutes = durationMinutes;
	        this.genre = genre;
	        this.status = status;
	    }

	    public int getId() { return id; }
	    public void setId(int id) { this.id = id; }

	    public String getTitle() { return title; }
	    public void setTitle(String title) { this.title = title; }

	    public String getPosterUrl() { return posterUrl; }
	    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

	    public Integer getDurationMinutes() { return durationMinutes; }
	    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

	    public String getGenre() { return genre; }
	    public void setGenre(String genre) { this.genre = genre; }

	    public String getStatus() { return status; }
	    public void setStatus(String status) { this.status = status; }
}
