package CinemaBooking.Group2.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;

import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.movie.MovieWithShowtimesDtos;
import CinemaBooking.Group2.dtos.seat.SeatDTO;
import CinemaBooking.Group2.dtos.seat.SeatRowDTO;
import CinemaBooking.Group2.dtos.showtime.MovieShowtimeGroupDtos;
import CinemaBooking.Group2.dtos.showtime.ShowtimePublicDtos;
import CinemaBooking.Group2.dtos.showtime.ShowtimeSeatResponseDTO;
import CinemaBooking.Group2.models.Seat;
import CinemaBooking.Group2.repositories.SeatRepository;
import CinemaBooking.Group2.repositories.ShowtimeRepository;
import CinemaBooking.Group2.repositories.SeatHoldRepository;

@Service
public class ShowtimeService {
    private final ShowtimeRepository stRepo;
    private final SeatRepository seatRepo;
    private final SeatHoldRepository seatHoldRepo;

    public ShowtimeService(ShowtimeRepository stRepo,
            SeatRepository seatRepo,
            SeatHoldRepository seatHoldRepo) {
        this.stRepo = stRepo;
        this.seatRepo = seatRepo;
        this.seatHoldRepo = seatHoldRepo;
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

    public List<MovieWithShowtimesDtos> getCinemasWithShowtimesByMovieId(int movieId) {
        // delegate to new overload without cinema filter
        return getCinemasWithShowtimesByMovieId(movieId, null);
    }

    public List<MovieWithShowtimesDtos> getCinemasWithShowtimesByMovieId(int movieId, Integer cinemaId) {
        if (movieId <= 0) {
            throw new IllegalArgumentException("movieId must be > 0");
        }
        return stRepo.getCinemasWithShowtimesByMovieId(movieId, cinemaId);
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

    public ShowtimeSeatResponseDTO getSeatMap(int showtimeId) {

        // 1. Lấy cinema name
        String cinemaName = stRepo.getCinemaNameByShowtime(showtimeId);

        if (cinemaName == null) {
            throw new IllegalArgumentException("Showtime not found");
        }

        // 2. Lấy thông tin showtime để có basePrice
        Map<String, Object> showtimeInfo = stRepo.getShowtimeDetails(showtimeId);
        if (showtimeInfo == null) {
            throw new IllegalArgumentException("Showtime details not found");
        }

        // Lấy basePrice từ showtime
        java.math.BigDecimal basePrice = java.math.BigDecimal.ZERO;
        Object basePriceObj = showtimeInfo.get("base_price");
        if (basePriceObj != null) {
            if (basePriceObj instanceof java.math.BigDecimal) {
                basePrice = (java.math.BigDecimal) basePriceObj;
            } else {
                try {
                    basePrice = new java.math.BigDecimal(basePriceObj.toString());
                } catch (NumberFormatException e) {
                    // Fallback to 0 if parsing fails
                }
            }
        }

        // 3. Lấy roomId
        int roomId = seatRepo.getRoomIdByShowtime(showtimeId);
        // 4. Lấy ghế
        List<Seat> seats = seatRepo.getSeatsByRoomId(roomId);

        // 5. Lấy ghế đã đặt
        Map<Integer, String> booked = seatRepo.getBookedSeats(showtimeId);

        // 5.1 Lấy ghế đang được hold (active holds)
        List<Integer> heldSeatIds = new ArrayList<>();
        try {
            heldSeatIds = seatHoldRepo.findActiveSeatIdsByShowtime(showtimeId);
        } catch (Exception ex) {
            // If hold fetch fails, ignore and proceed (seats will be shown as available)
        }

        // 6. Build response
        ShowtimeSeatResponseDTO res = new ShowtimeSeatResponseDTO();

        res.setShowtimeId(showtimeId);

        // SET CINEMA NAME
        res.setCinemaName(cinemaName);

        // --- SET ADDITIONAL MOVIE / SHOWTIME DATA ---
        try {
            // showtimeInfo đã được lấy ở trên
            if (showtimeInfo != null) {
                res.setMovieTitle((String) showtimeInfo.get("movie_title"));

                // Prefix "/media/" before poster url (handle null and avoid duplicate prefix)
                Object posterObj = showtimeInfo.get("poster_url");
                if (posterObj != null) {
                    String poster = posterObj.toString();
                    if (!poster.startsWith("/media/")) {
                        // remove leading slash to avoid double slashes
                        if (poster.startsWith("/")) {
                            poster = poster.substring(1);
                        }
                        poster = "/media/" + poster;
                    }
                    res.setMoviePosterUrl(poster);
                } else {
                    res.setMoviePosterUrl(null);
                }

                res.setGenre((String) showtimeInfo.get("genre"));

                Object durObj = showtimeInfo.get("duration_minutes");
                if (durObj != null) {
                    if (durObj instanceof Number) {
                        res.setDuration(((Number) durObj).intValue());
                    } else {
                        try {
                            res.setDuration(Integer.parseInt(durObj.toString()));
                        } catch (NumberFormatException ex) {
                            // ignore -> leave default 0
                        }
                    }
                }

                res.setRoomName((String) showtimeInfo.get("room_name"));
                res.setFullAddress((String) showtimeInfo.get("address"));

                Object stObj = showtimeInfo.get("start_time");
                if (stObj != null) {
                    if (stObj instanceof java.sql.Timestamp) {
                        res.setStartTime(((java.sql.Timestamp) stObj).toLocalDateTime());
                    } else if (stObj instanceof LocalDateTime) {
                        res.setStartTime((LocalDateTime) stObj);
                    }
                }
            }
        } catch (Exception ex) {
            // If details fetch fails, continue returning seat map but log? For now, rethrow
            // to surface error
            throw new RuntimeException("Failed to fetch showtime details for id: " + showtimeId, ex);
        }

        // Pass heldSeatIds và basePrice so held seats are shown as HELD instead of
        // AVAILABLE
        res.setRows(
                buildSeatRows(seats, booked, heldSeatIds, basePrice));

        return res;
    }

    /**
     * Group ghế theo hàng (A,B,C...)
     */
    private List<SeatRowDTO> buildSeatRows(
            List<Seat> seats,
            Map<Integer, String> booked,
            List<Integer> heldSeatIds,
            java.math.BigDecimal basePrice) {

        // TreeMap → auto sort theo A,B,C
        Map<String, List<SeatDTO>> map = new TreeMap<>();

        // ========== Convert Seat → SeatDTO ==========
        for (Seat s : seats) {

            String code = s.getSeatCode(); // VD: A12

            String row = code.substring(0, 1); // A
            String num = code.substring(1); // 12

            SeatDTO dto = new SeatDTO();

            dto.setId(s.getId());
            dto.setCode(code);
            dto.setRow(row);
            dto.setNumber(num);
            dto.setType(s.getSeatType().name());

            // ===== Status =====
            if (booked.containsKey(s.getId())) {

                String st = booked.get(s.getId());

                if ("PAID".equals(st)) {
                    dto.setStatus("SOLD");
                } else {
                    dto.setStatus("BOOKED");
                }

            } else if (heldSeatIds != null && !heldSeatIds.isEmpty() && heldSeatIds.contains(s.getId())) {
                // Seat is currently held (not booked)
                dto.setStatus("HELD");
            } else {
                dto.setStatus("AVAILABLE");
            }

            // ===== Price =====
            // Tính giá = basePrice + extraPrice (giống như logic trong BookingService)
            java.math.BigDecimal totalPrice = basePrice;
            if (s.getExtraPrice() != null) {
                totalPrice = totalPrice.add(s.getExtraPrice());
            }

            long price = totalPrice.longValue();
            dto.setPrice(price);

            // ===== Group theo row =====
            map.computeIfAbsent(row, k -> new ArrayList<>())
                    .add(dto);
        }

        // ========== Sort từng hàng ==========
        for (List<SeatDTO> list : map.values()) {

            list.sort((a, b) -> {

                int n1 = Integer.parseInt(a.getNumber());
                int n2 = Integer.parseInt(b.getNumber());

                return Integer.compare(n1, n2);
            });
        }

        // ========== Convert Map → List ==========
        List<SeatRowDTO> rows = new ArrayList<>();

        for (String row : map.keySet()) {

            SeatRowDTO r = new SeatRowDTO();

            r.setRowLabel(row);
            r.setSeats(map.get(row));

            rows.add(r);
        }

        return rows;
    }

}