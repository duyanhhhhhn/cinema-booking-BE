package CinemaBooking.Group2.dtos.scheduler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminSchedulerEventDto {
    private int id;
    private int resource;
    private String text;
    private LocalDateTime start;
    private LocalDateTime end;

    private int movieId;
    private String posterUrl;
    private BigDecimal basePrice;
    private String status;

    private boolean conflict;
    private List<Integer> conflictWithIds = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getResource() { return resource; }
    public void setResource(int resource) { this.resource = resource; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isConflict() { return conflict; }
    public void setConflict(boolean conflict) { this.conflict = conflict; }

    public List<Integer> getConflictWithIds() { return conflictWithIds; }
    public void setConflictWithIds(List<Integer> conflictWithIds) { this.conflictWithIds = conflictWithIds; }
}