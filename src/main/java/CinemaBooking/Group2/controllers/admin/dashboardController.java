package CinemaBooking.Group2.controllers.admin;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import CinemaBooking.Group2.dtos.ApiResponse;
import CinemaBooking.Group2.dtos.dashboardnreports.ReportMovieOptionDTO;
import CinemaBooking.Group2.dtos.dashboardnreports.RevenueReportResponseDTO;
import CinemaBooking.Group2.service.DashboardService;

@RestController
@RequestMapping("/api/admin/dashboard")
public class dashboardController {

    private final DashboardService dashboardService;

    public dashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/reports/revenue")
    public ResponseEntity<ApiResponse<RevenueReportResponseDTO>> getRevenueReport(
        Authentication authentication,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,
        @RequestParam(required = false)
        Integer cinemaId,
        @RequestParam(required = false)
        Integer movieId
    ) {
        RevenueReportResponseDTO data = dashboardService.getRevenueReport(
            authentication,
            startDate,
            endDate,
            cinemaId,
            movieId
        );

        return ResponseEntity.ok(
            new ApiResponse<>("Revenue report fetched successfully for current scope.", data)
        );
    }

    @GetMapping("/reports/movie-options")
    public ResponseEntity<ApiResponse<List<ReportMovieOptionDTO>>> getReportMovieOptions(
        Authentication authentication,
        @RequestParam(required = false)
        Integer cinemaId
    ) {
        List<ReportMovieOptionDTO> data = dashboardService.getReportMovieOptions(authentication, cinemaId);

        return ResponseEntity.ok(
            new ApiResponse<>("Report movie options fetched successfully.", data)
        );
    }
}