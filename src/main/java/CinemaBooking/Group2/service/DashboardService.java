package CinemaBooking.Group2.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.dashboardnreports.ReportMovieOptionDTO;
import CinemaBooking.Group2.dtos.dashboardnreports.RevenueReportResponseDTO;
import CinemaBooking.Group2.repositories.DashboardRepository;
import CinemaBooking.Group2.repositories.DashboardRepository.CinemaRevenueRankingRow;
import CinemaBooking.Group2.repositories.DashboardRepository.DailyRevenueRow;
import CinemaBooking.Group2.repositories.DashboardRepository.MovieOptionRow;
import CinemaBooking.Group2.repositories.DashboardRepository.MovieRevenueRankingRow;
import CinemaBooking.Group2.repositories.DashboardRepository.RevenueSummaryRow;

@Service
public class DashboardService {

    private final DashboardRepository repository;

    public DashboardService(DashboardRepository repository) {
        this.repository = repository;
    }

    public RevenueReportResponseDTO getRevenueReport(
        Authentication authentication,
        LocalDate startDate,
        LocalDate endDate,
        Integer cinemaId,
        Integer movieId
    ) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be less than or equal to end date.");
        }

        UserScope scope = resolveScope(authentication, cinemaId);

        RevenueSummaryRow summaryRow = repository.findRevenueSummary(
            startDate, endDate, scope.cinemaId(), movieId
        );

        List<DailyRevenueRow> dailyRows = repository.findDailyRevenue(
            startDate, endDate, scope.cinemaId(), movieId
        );

        List<MovieRevenueRankingRow> movieRows = repository.findMovieRevenueRanking(
            startDate, endDate, scope.cinemaId(), movieId
        );

        List<CinemaRevenueRankingRow> cinemaRows = repository.findCinemaRevenueRanking(
            startDate, endDate, scope.cinemaId(), movieId
        );

        RevenueReportResponseDTO.Filter filter = new RevenueReportResponseDTO.Filter(
            startDate, endDate, scope.cinemaId(), movieId
        );

        List<RevenueReportResponseDTO.DailyRevenue> dailyRevenue = mapDailyRevenue(dailyRows);

        List<RevenueReportResponseDTO.MovieRevenueRanking> movieRanking = mapMovieRanking(
            movieRows, summaryRow.getTotalRevenue()
        );

        List<RevenueReportResponseDTO.CinemaRevenueRanking> cinemaRanking = mapCinemaRanking(
            cinemaRows, summaryRow.getTotalRevenue()
        );

        RevenueReportResponseDTO.MovieRevenueRanking lowestMovie =
            movieRanking.isEmpty() ? null : movieRanking.get(0);

        RevenueReportResponseDTO.MovieRevenueRanking topMovie =
            movieRanking.isEmpty() ? null : movieRanking.get(movieRanking.size() - 1);

        RevenueReportResponseDTO.CinemaRevenueRanking lowestCinema =
            cinemaRanking.isEmpty() ? null : cinemaRanking.get(0);

        RevenueReportResponseDTO.CinemaRevenueRanking topCinema =
            cinemaRanking.isEmpty() ? null : cinemaRanking.get(cinemaRanking.size() - 1);

        RevenueReportResponseDTO.Summary summary = new RevenueReportResponseDTO.Summary(
            summaryRow.getTotalRevenue(),
            summaryRow.getTotalTicketsSold(),
            summaryRow.getTotalPaidBookings(),
            summaryRow.getTotalMovies(),
            summaryRow.getTotalCinemas(),
            topMovie,
            lowestMovie,
            topCinema,
            lowestCinema
        );

        RevenueReportResponseDTO response = new RevenueReportResponseDTO();
        response.setFilter(filter);
        response.setSummary(summary);
        response.setDailyRevenue(dailyRevenue);
        response.setMovieRevenueRanking(movieRanking);
        response.setCinemaRevenueRanking(cinemaRanking);

        return response;
    }

    public List<ReportMovieOptionDTO> getReportMovieOptions(
        Authentication authentication,
        Integer cinemaId
    ) {
        UserScope scope = resolveScope(authentication, cinemaId);

        List<MovieOptionRow> rows = repository.findReportMovieOptions(scope.cinemaId());
        List<ReportMovieOptionDTO> result = new ArrayList<>();

        for (MovieOptionRow row : rows) {
            result.add(new ReportMovieOptionDTO(
                row.getMovieId(),
                row.getMovieTitle(),
                row.getPosterUrl()
            ));
        }

        return result;
    }

    private UserScope resolveScope(Authentication authentication, Integer requestedCinemaId) {
        if (authentication == null) {
            throw new IllegalArgumentException("Unauthorized access.");
        }

        boolean isAdmin = hasRole(authentication, "ADMIN");
        boolean isManager = hasRole(authentication, "MANAGER");

        if (isAdmin) {
            return new UserScope("ADMIN", requestedCinemaId);
        }

        if (isManager) {
            if (requestedCinemaId != null) {
                return new UserScope("MANAGER", requestedCinemaId);
            }

            String email = authentication.getName();
            Integer assignedCinemaId = repository.findCinemaIdByEmail(email);

            if (assignedCinemaId == null) {
                throw new IllegalArgumentException("Manager is not assigned to any cinema.");
            }

            return new UserScope("MANAGER", assignedCinemaId);
        }

        throw new IllegalArgumentException("You do not have permission to access revenue reports.");
    }

    private boolean hasRole(Authentication authentication, String roleName) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
            .anyMatch(authority ->
                roleName.equalsIgnoreCase(authority.getAuthority())
                    || ("ROLE_" + roleName).equalsIgnoreCase(authority.getAuthority())
            );
    }

    private List<RevenueReportResponseDTO.DailyRevenue> mapDailyRevenue(List<DailyRevenueRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }

        List<RevenueReportResponseDTO.DailyRevenue> result = new ArrayList<>();
        for (DailyRevenueRow row : rows) {
            result.add(new RevenueReportResponseDTO.DailyRevenue(
                row.getDate(),
                row.getTotalRevenue(),
                row.getTotalTicketsSold(),
                row.getTotalPaidBookings()
            ));
        }

        return result;
    }

    private List<RevenueReportResponseDTO.MovieRevenueRanking> mapMovieRanking(
        List<MovieRevenueRankingRow> rows,
        BigDecimal totalRevenue
    ) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }

        List<RevenueReportResponseDTO.MovieRevenueRanking> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            MovieRevenueRankingRow row = rows.get(i);

            result.add(new RevenueReportResponseDTO.MovieRevenueRanking(
                i + 1,
                row.getMovieId(),
                row.getMovieTitle(),
                row.getPosterUrl(),
                row.getTotalRevenue(),
                row.getTotalTicketsSold(),
                row.getTotalPaidBookings(),
                DashboardRepository.calcPercent(row.getTotalRevenue(), totalRevenue)
            ));
        }

        return result;
    }

    private List<RevenueReportResponseDTO.CinemaRevenueRanking> mapCinemaRanking(
        List<CinemaRevenueRankingRow> rows,
        BigDecimal totalRevenue
    ) {
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }

        List<RevenueReportResponseDTO.CinemaRevenueRanking> result = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            CinemaRevenueRankingRow row = rows.get(i);

            result.add(new RevenueReportResponseDTO.CinemaRevenueRanking(
                i + 1,
                row.getCinemaId(),
                row.getCinemaName(),
                row.getCinemaImageUrl(),
                row.getTotalRevenue(),
                row.getTotalTicketsSold(),
                row.getTotalPaidBookings(),
                DashboardRepository.calcPercent(row.getTotalRevenue(), totalRevenue)
            ));
        }

        return result;
    }

    private record UserScope(String role, Integer cinemaId) {
    }
}