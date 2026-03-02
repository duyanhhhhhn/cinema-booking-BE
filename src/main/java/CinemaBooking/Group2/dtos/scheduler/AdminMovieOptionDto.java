package CinemaBooking.Group2.dtos.scheduler;

public class AdminMovieOptionDto {
    private int id;
    private String title;
    private int durationMinutes;
    private String posterUrl;
    private String status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}