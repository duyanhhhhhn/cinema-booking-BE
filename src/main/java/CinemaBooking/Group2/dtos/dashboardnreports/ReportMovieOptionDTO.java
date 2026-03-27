package CinemaBooking.Group2.dtos.dashboardnreports;

public class ReportMovieOptionDTO {

    private Integer movieId;
    private String movieTitle;
    private String posterUrl;

    public ReportMovieOptionDTO() {
    }

    public ReportMovieOptionDTO(Integer movieId, String movieTitle, String posterUrl) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.posterUrl = posterUrl;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
}