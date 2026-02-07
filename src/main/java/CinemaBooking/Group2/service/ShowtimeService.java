package CinemaBooking.Group2.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

@Service
public class ShowtimeService {
	private final ShowtimeRepository stRepo;
	private final SeatRepository seatRepo;
	
		public ShowtimeService(ShowtimeRepository stRepo,
			SeatRepository seatRepo) {
		this.stRepo = stRepo;
		this.seatRepo = seatRepo;
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
        if (movieId <= 0) {
            throw new IllegalArgumentException("movieId must be > 0");
        }
        return stRepo.getCinemasWithShowtimesByMovieId(movieId);
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
    String cinemaName =
            stRepo.getCinemaNameByShowtime(showtimeId);

    if (cinemaName == null) {
        throw new IllegalArgumentException("Showtime not found");
    }
    // 2. Lấy roomId
    int roomId =
            seatRepo.getRoomIdByShowtime(showtimeId);
    // 3. Lấy ghế
    List<Seat> seats =
            seatRepo.getSeatsByRoomId(roomId);

    // 4. Lấy ghế đã đặt
    Map<Integer,String> booked =
            seatRepo.getBookedSeats(showtimeId);

    // 5. Build response
    ShowtimeSeatResponseDTO res =
            new ShowtimeSeatResponseDTO();

    res.setShowtimeId(showtimeId);

    //  SET CINEMA NAME
    res.setCinemaName(cinemaName);

    res.setRows(
            buildSeatRows(seats, booked)
    );

    return res;
}


    /**
     * Group ghế theo hàng (A,B,C...)
     */
    private List<SeatRowDTO> buildSeatRows(
            List<Seat> seats,
            Map<Integer,String> booked){

        // TreeMap → auto sort theo A,B,C
        Map<String,List<SeatDTO>> map =
                new TreeMap<>();


        // ========== Convert Seat → SeatDTO ==========
        for(Seat s : seats){

            String code = s.getSeatCode(); // VD: A12

            String row = code.substring(0,1); // A
            String num = code.substring(1);  // 12


            SeatDTO dto = new SeatDTO();

            dto.setId(s.getId());
            dto.setCode(code);
            dto.setRow(row);
            dto.setNumber(num);
            dto.setType(s.getSeatType().name());


            // ===== Status =====
            if(booked.containsKey(s.getId())){

                String st = booked.get(s.getId());

                if("PAID".equals(st)){
                    dto.setStatus("SOLD");
                }else{
                    dto.setStatus("BOOKED");
                }

            }else{
                dto.setStatus("AVAILABLE");
            }


            // ===== Price =====
            long price = 0;

            if(s.getExtraPrice()!=null){
                price = s.getExtraPrice().longValue();
            }

            dto.setPrice(price);


            // ===== Group theo row =====
            map.computeIfAbsent(row,k->new ArrayList<>())
               .add(dto);
        }


        // ========== Sort từng hàng ==========
        for(List<SeatDTO> list : map.values()){

            list.sort((a,b)->{

                int n1 = Integer.parseInt(a.getNumber());
                int n2 = Integer.parseInt(b.getNumber());

                return Integer.compare(n1, n2);
            });
        }


        // ========== Convert Map → List ==========
        List<SeatRowDTO> rows = new ArrayList<>();

        for(String row : map.keySet()){

            SeatRowDTO r = new SeatRowDTO();

            r.setRowLabel(row);
            r.setSeats(map.get(row));

            rows.add(r);
        }

        return rows;
    }

}
