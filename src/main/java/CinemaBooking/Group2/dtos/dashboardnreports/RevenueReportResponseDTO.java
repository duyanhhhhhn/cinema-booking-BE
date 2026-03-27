package CinemaBooking.Group2.dtos.dashboardnreports;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RevenueReportResponseDTO {

    private Filter filter;
    private Summary summary;
    private List<DailyRevenue> dailyRevenue;
    private List<MovieRevenueRanking> movieRevenueRanking;
    private List<CinemaRevenueRanking> cinemaRevenueRanking;

    public RevenueReportResponseDTO() {
    }

    public RevenueReportResponseDTO(
        Filter filter,
        Summary summary,
        List<DailyRevenue> dailyRevenue,
        List<MovieRevenueRanking> movieRevenueRanking,
        List<CinemaRevenueRanking> cinemaRevenueRanking
    ) {
        this.filter = filter;
        this.summary = summary;
        this.dailyRevenue = dailyRevenue;
        this.movieRevenueRanking = movieRevenueRanking;
        this.cinemaRevenueRanking = cinemaRevenueRanking;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public List<DailyRevenue> getDailyRevenue() {
        return dailyRevenue;
    }

    public void setDailyRevenue(List<DailyRevenue> dailyRevenue) {
        this.dailyRevenue = dailyRevenue;
    }

    public List<MovieRevenueRanking> getMovieRevenueRanking() {
        return movieRevenueRanking;
    }

    public void setMovieRevenueRanking(List<MovieRevenueRanking> movieRevenueRanking) {
        this.movieRevenueRanking = movieRevenueRanking;
    }

    public List<CinemaRevenueRanking> getCinemaRevenueRanking() {
        return cinemaRevenueRanking;
    }

    public void setCinemaRevenueRanking(List<CinemaRevenueRanking> cinemaRevenueRanking) {
        this.cinemaRevenueRanking = cinemaRevenueRanking;
    }

    public static class Filter {
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer cinemaId;
        private Integer movieId;

        public Filter() {
        }

        public Filter(LocalDate startDate, LocalDate endDate, Integer cinemaId, Integer movieId) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.cinemaId = cinemaId;
            this.movieId = movieId;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public Integer getCinemaId() {
            return cinemaId;
        }

        public void setCinemaId(Integer cinemaId) {
            this.cinemaId = cinemaId;
        }

        public Integer getMovieId() {
            return movieId;
        }

        public void setMovieId(Integer movieId) {
            this.movieId = movieId;
        }
    }

    public static class Summary {
        private BigDecimal totalRevenue;
        private long totalTicketsSold;
        private long totalPaidBookings;
        private long totalMovies;
        private long totalCinemas;
        private MovieRevenueRanking topMovie;
        private MovieRevenueRanking lowestMovie;
        private CinemaRevenueRanking topCinema;
        private CinemaRevenueRanking lowestCinema;

        public Summary() {
        }

        public Summary(
            BigDecimal totalRevenue,
            long totalTicketsSold,
            long totalPaidBookings,
            long totalMovies,
            long totalCinemas,
            MovieRevenueRanking topMovie,
            MovieRevenueRanking lowestMovie,
            CinemaRevenueRanking topCinema,
            CinemaRevenueRanking lowestCinema
        ) {
            this.totalRevenue = totalRevenue;
            this.totalTicketsSold = totalTicketsSold;
            this.totalPaidBookings = totalPaidBookings;
            this.totalMovies = totalMovies;
            this.totalCinemas = totalCinemas;
            this.topMovie = topMovie;
            this.lowestMovie = lowestMovie;
            this.topCinema = topCinema;
            this.lowestCinema = lowestCinema;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public long getTotalTicketsSold() {
            return totalTicketsSold;
        }

        public void setTotalTicketsSold(long totalTicketsSold) {
            this.totalTicketsSold = totalTicketsSold;
        }

        public long getTotalPaidBookings() {
            return totalPaidBookings;
        }

        public void setTotalPaidBookings(long totalPaidBookings) {
            this.totalPaidBookings = totalPaidBookings;
        }

        public long getTotalMovies() {
            return totalMovies;
        }

        public void setTotalMovies(long totalMovies) {
            this.totalMovies = totalMovies;
        }

        public long getTotalCinemas() {
            return totalCinemas;
        }

        public void setTotalCinemas(long totalCinemas) {
            this.totalCinemas = totalCinemas;
        }

        public MovieRevenueRanking getTopMovie() {
            return topMovie;
        }

        public void setTopMovie(MovieRevenueRanking topMovie) {
            this.topMovie = topMovie;
        }

        public MovieRevenueRanking getLowestMovie() {
            return lowestMovie;
        }

        public void setLowestMovie(MovieRevenueRanking lowestMovie) {
            this.lowestMovie = lowestMovie;
        }

        public CinemaRevenueRanking getTopCinema() {
            return topCinema;
        }

        public void setTopCinema(CinemaRevenueRanking topCinema) {
            this.topCinema = topCinema;
        }

        public CinemaRevenueRanking getLowestCinema() {
            return lowestCinema;
        }

        public void setLowestCinema(CinemaRevenueRanking lowestCinema) {
            this.lowestCinema = lowestCinema;
        }
    }

    public static class DailyRevenue {
        private LocalDate date;
        private BigDecimal totalRevenue;
        private long totalTicketsSold;
        private long totalPaidBookings;

        public DailyRevenue() {
        }

        public DailyRevenue(
            LocalDate date,
            BigDecimal totalRevenue,
            long totalTicketsSold,
            long totalPaidBookings
        ) {
            this.date = date;
            this.totalRevenue = totalRevenue;
            this.totalTicketsSold = totalTicketsSold;
            this.totalPaidBookings = totalPaidBookings;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public long getTotalTicketsSold() {
            return totalTicketsSold;
        }

        public void setTotalTicketsSold(long totalTicketsSold) {
            this.totalTicketsSold = totalTicketsSold;
        }

        public long getTotalPaidBookings() {
            return totalPaidBookings;
        }

        public void setTotalPaidBookings(long totalPaidBookings) {
            this.totalPaidBookings = totalPaidBookings;
        }
    }

    public static class MovieRevenueRanking {
        private Integer rank;
        private Integer movieId;
        private String movieTitle;
        private String posterUrl;
        private BigDecimal totalRevenue;
        private long totalTicketsSold;
        private long totalPaidBookings;
        private BigDecimal revenueSharePercent;

        public MovieRevenueRanking() {
        }

        public MovieRevenueRanking(
            Integer rank,
            Integer movieId,
            String movieTitle,
            String posterUrl,
            BigDecimal totalRevenue,
            long totalTicketsSold,
            long totalPaidBookings,
            BigDecimal revenueSharePercent
        ) {
            this.rank = rank;
            this.movieId = movieId;
            this.movieTitle = movieTitle;
            this.posterUrl = posterUrl;
            this.totalRevenue = totalRevenue;
            this.totalTicketsSold = totalTicketsSold;
            this.totalPaidBookings = totalPaidBookings;
            this.revenueSharePercent = revenueSharePercent;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
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

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public long getTotalTicketsSold() {
            return totalTicketsSold;
        }

        public void setTotalTicketsSold(long totalTicketsSold) {
            this.totalTicketsSold = totalTicketsSold;
        }

        public long getTotalPaidBookings() {
            return totalPaidBookings;
        }

        public void setTotalPaidBookings(long totalPaidBookings) {
            this.totalPaidBookings = totalPaidBookings;
        }

        public BigDecimal getRevenueSharePercent() {
            return revenueSharePercent;
        }

        public void setRevenueSharePercent(BigDecimal revenueSharePercent) {
            this.revenueSharePercent = revenueSharePercent;
        }
    }

    public static class CinemaRevenueRanking {
        private Integer rank;
        private Integer cinemaId;
        private String cinemaName;
        private String cinemaImageUrl;
        private BigDecimal totalRevenue;
        private long totalTicketsSold;
        private long totalPaidBookings;
        private BigDecimal revenueSharePercent;

        public CinemaRevenueRanking() {
        }

        public CinemaRevenueRanking(
            Integer rank,
            Integer cinemaId,
            String cinemaName,
            String cinemaImageUrl,
            BigDecimal totalRevenue,
            long totalTicketsSold,
            long totalPaidBookings,
            BigDecimal revenueSharePercent
        ) {
            this.rank = rank;
            this.cinemaId = cinemaId;
            this.cinemaName = cinemaName;
            this.cinemaImageUrl = cinemaImageUrl;
            this.totalRevenue = totalRevenue;
            this.totalTicketsSold = totalTicketsSold;
            this.totalPaidBookings = totalPaidBookings;
            this.revenueSharePercent = revenueSharePercent;
        }

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public Integer getCinemaId() {
            return cinemaId;
        }

        public void setCinemaId(Integer cinemaId) {
            this.cinemaId = cinemaId;
        }

        public String getCinemaName() {
            return cinemaName;
        }

        public void setCinemaName(String cinemaName) {
            this.cinemaName = cinemaName;
        }

        public String getCinemaImageUrl() {
            return cinemaImageUrl;
        }

        public void setCinemaImageUrl(String cinemaImageUrl) {
            this.cinemaImageUrl = cinemaImageUrl;
        }

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public long getTotalTicketsSold() {
            return totalTicketsSold;
        }

        public void setTotalTicketsSold(long totalTicketsSold) {
            this.totalTicketsSold = totalTicketsSold;
        }

        public long getTotalPaidBookings() {
            return totalPaidBookings;
        }

        public void setTotalPaidBookings(long totalPaidBookings) {
            this.totalPaidBookings = totalPaidBookings;
        }

        public BigDecimal getRevenueSharePercent() {
            return revenueSharePercent;
        }

        public void setRevenueSharePercent(BigDecimal revenueSharePercent) {
            this.revenueSharePercent = revenueSharePercent;
        }
    }
}