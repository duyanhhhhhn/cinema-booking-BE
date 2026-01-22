package CinemaBooking.Group2.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.showtime.MovieShowtimeGroupDtos;
import CinemaBooking.Group2.dtos.showtime.ShowtimePublicDtos;
import CinemaBooking.Group2.repositories.ShowtimeRepository;

@Service
public class ShowtimeService {
	private final ShowtimeRepository stRepo;
	public ShowtimeService(ShowtimeRepository stRepo) {
		this.stRepo = stRepo;
	}
	
	public List<MovieShowtimeGroupDtos> getShowtimesGroupedByMovie(int cinemaId, Integer movieId, String dateStr) {
        LocalDate date = parseAndValidate(cinemaId, movieId, dateStr);
        return stRepo.getShowtimesPublicGrouped(cinemaId, movieId, date);
    }
	
    public List<MovieShowtimeGroupDtos> getShowtimesGroupedByMovie(int cinemaId, Integer movieId, LocalDate date) {
        validate(cinemaId, movieId, date);
        return stRepo.getShowtimesPublicGrouped(cinemaId, movieId, date);
    }

    private LocalDate parseAndValidate(int cinemaId, Integer movieId, String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            throw new IllegalArgumentException("date is required (yyyy-MM-dd).");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("date invalid. Expected format: yyyy-MM-dd.");
        }

        validate(cinemaId, movieId, date);
        return date;
    }

    private void validate(int cinemaId, Integer movieId, LocalDate date) {
        if (cinemaId <= 0) {
            throw new IllegalArgumentException("cinemaId invalid.");
        }
        if (date == null) {
            throw new IllegalArgumentException("date is required (yyyy-MM-dd).");
        }
        if (movieId != null && movieId <= 0) {
            throw new IllegalArgumentException("movieId invalid.");
        }
    }

}
