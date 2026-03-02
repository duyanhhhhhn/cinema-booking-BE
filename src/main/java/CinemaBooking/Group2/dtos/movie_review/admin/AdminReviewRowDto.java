package CinemaBooking.Group2.dtos.movie_review.admin;

import java.time.LocalDateTime;

public class AdminReviewRowDto {
    private int id;
    private int userId;
    private String userFullName;
    private String userEmail;
    private int movieId;
    private String movieTitle;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private boolean hidden; 

    public AdminReviewRowDto() {}
    public AdminReviewRowDto(int id, int userId, String userFullName, String userEmail,
            int movieId, String movieTitle, int rating, String comment,
            LocalDateTime createdAt) {
this.id = id;
this.userId = userId;
this.userFullName = userFullName;
this.userEmail = userEmail;
this.movieId = movieId;
this.movieTitle = movieTitle;
this.rating = rating;
this.comment = comment;
this.createdAt = createdAt;
}

//✅ Constructor mới: có thêm hidden
public AdminReviewRowDto(int id, int userId, String userFullName, String userEmail,
            int movieId, String movieTitle, int rating, String comment,
            LocalDateTime createdAt, boolean hidden) {
this(id, userId, userFullName, userEmail, movieId, movieTitle, rating, comment, createdAt);
this.hidden  = hidden; 
}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isHidden() { return hidden; }      // JSON key: "hidden"
    public void setHidden(boolean hidden) { this.hidden = hidden; }
}