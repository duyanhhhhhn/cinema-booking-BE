package CinemaBooking.Group2.dtos.movie;

public class MovieMediaDtos {
    private final String posterUrl;
    private final String bannerUrl;

    public MovieMediaDtos(String posterUrl, String bannerUrl) {
        this.posterUrl = posterUrl;
        this.bannerUrl = bannerUrl;
    }
    public String getPosterUrl() { return posterUrl; }
    public String getBannerUrl() { return bannerUrl; }
}
