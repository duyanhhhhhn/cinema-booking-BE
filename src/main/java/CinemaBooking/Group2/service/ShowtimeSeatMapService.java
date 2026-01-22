package CinemaBooking.Group2.service;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.showtime.ShowtimeDetailDtos;
import CinemaBooking.Group2.repositories.ShowtimeSheetMapRepositories;

@Service
public class ShowtimeSeatMapService {

    private final ShowtimeSheetMapRepositories repo;

    public ShowtimeSeatMapService(ShowtimeSheetMapRepositories repo) {
        this.repo = repo;
    }

    public ShowtimeDetailDtos getShowtimeDetailWithSeatMap(int showtimeId) {
        if (showtimeId <= 0) {
            throw new IllegalArgumentException("showtimeId invalid.");
        }

        ShowtimeDetailDtos dto = repo.getShowtimeDetailWithSeatMap(showtimeId);

        if (dto == null) {
            throw new IllegalArgumentException("showtime not found.");
        }

        return dto;
    }
}
