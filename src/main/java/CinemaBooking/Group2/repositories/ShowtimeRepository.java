package CinemaBooking.Group2.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import CinemaBooking.Group2.dtos.movie.MovieWithShowtimesDtos;
import CinemaBooking.Group2.dtos.showtime.MovieShowtimeGroupDtos;
import CinemaBooking.Group2.dtos.showtime.ShowtimePublicDtos;
import CinemaBooking.Group2.models.Showtime;

@Repository
public class ShowtimeRepository {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private JdbcTemplate jdbc;

	public Showtime findById(int showtimeId) {
		String sql = "SELECT * FROM showtime WHERE id = ?";
		try {
			return jdbc.queryForObject(sql, new BeanPropertyRowMapper<>(Showtime.class), showtimeId);
		} catch (Exception e) {
			return null;
		}
	}

	public boolean exists(int showtimeId) {
		String sql = "SELECT COUNT(*) FROM showtime WHERE id = ?";
		try {
			Integer count = jdbc.queryForObject(sql, Integer.class, showtimeId);
			return count != null && count > 0;
		} catch (Exception e) {
			return false;
		}
	}

	// (codeser) -> HÀM TRUY VẤN LẤY LỊCH CHIẾU CỦA CÁC BỘ PHIM (RẠP + THỜI GIAN...)
	public List<MovieShowtimeGroupDtos> getShowtimesPublicGrouped(int cinemaId, Integer movieId, LocalDate date) {

		if (cinemaId <= 0)
			throw new IllegalArgumentException("cinemaId invalid.");
		if (date == null)
			throw new IllegalArgumentException("date is required (yyyy-MM-dd).");
		if (movieId != null && movieId <= 0)
			throw new IllegalArgumentException("movieId invalid.");

		boolean hasMovie = (movieId != null && movieId > 0);

		LocalDateTime startOfDay = date.atStartOfDay();
		LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

		String sql = "SELECT " + "  m.id    AS movie_id, " + "  m.title AS movie_title, "
				+ "  COALESCE(m.poster_url, m.banner_url) AS cover_url, " + "  s.id    AS showtime_id, "
				+ "  TIME_FORMAT(s.start_time, '%H:%i') AS start_time, " + "  s.base_price AS price, "
				+ "  r.name AS room_name " + "FROM showtime s " + "JOIN movie  m ON m.id = s.movie_id "
				+ "JOIN room   r ON r.id = s.room_id " + "JOIN cinema c ON c.id = r.cinema_id "
				+ "WHERE c.is_active = 1 " + "  AND s.status = 'SCHEDULED' "
				+ "  AND m.status IN ('COMING_SOON','NOW_SHOWING') " + "  AND r.cinema_id = ? "
				+ (hasMovie ? "  AND s.movie_id = ? " : "") + "  AND s.start_time >= ? " + "  AND s.start_time <  ? "
				+ "ORDER BY m.id ASC, s.start_time ASC, r.name ASC, s.id ASC;";

		Map<Integer, MovieShowtimeGroupDtos> map = new LinkedHashMap<>();

		try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			conn.setReadOnly(true);

			int idx = 1;
			ps.setInt(idx++, cinemaId);

			if (hasMovie) {
				ps.setInt(idx++, movieId);
			}

			ps.setTimestamp(idx++, Timestamp.valueOf(startOfDay));
			ps.setTimestamp(idx++, Timestamp.valueOf(endOfDay));

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int mid = rs.getInt("movie_id");

					MovieShowtimeGroupDtos group = map.get(mid);
					if (group == null) {
						group = new MovieShowtimeGroupDtos();
						group.setMovieId(mid);
						group.setMovieTitle(rs.getString("movie_title"));
						group.setCoverUrl(rs.getString("cover_url"));
						group.setShowtimes(new ArrayList<>());
						map.put(mid, group);
					}

					ShowtimePublicDtos item = new ShowtimePublicDtos();
					item.setId(rs.getInt("showtime_id"));
					item.setStartTime(rs.getString("start_time"));
					item.setPrice(rs.getBigDecimal("price"));
					item.setRoomName(rs.getString("room_name"));

					group.getShowtimes().add(item);
				}
			}

			return new ArrayList<>(map.values());

		} catch (SQLException e) {
			throw new RuntimeException("Failed to fetch showtimes (PUBLIC GROUPED) with filters.", e);
		}
	}

	public List<Showtime> getShowtimesModel(Integer cinemaId, Integer movieId, LocalDate date) {

		boolean hasCinema = (cinemaId != null && cinemaId > 0);
		boolean hasMovie = (movieId != null && movieId > 0);
		boolean hasDate = (date != null);

		LocalDateTime startOfDay = null;
		LocalDateTime endOfDay = null;
		if (hasDate) {
			startOfDay = date.atStartOfDay();
			endOfDay = date.plusDays(1).atStartOfDay();
		}

		String sql = "SELECT "
				+ "  s.id, s.movie_id, s.room_id, s.start_time, s.end_time, s.base_price, s.status, s.created_at "
				+ "FROM showtime s " + "JOIN movie  m ON m.id = s.movie_id " + "JOIN room   r ON r.id = s.room_id "
				+ "JOIN cinema c ON c.id = r.cinema_id " + "WHERE 1=1 " + "  AND c.is_active = 1 "
				+ "  AND s.status = 'SCHEDULED' " + "  AND m.status IN ('COMING_SOON', 'NOW_SHOWING') "
				+ (hasCinema ? " AND r.cinema_id = ? " : "") + (hasMovie ? " AND s.movie_id  = ? " : "")
				+ (hasDate ? " AND s.start_time >= ? AND s.start_time < ? " : "")
				+ "ORDER BY s.start_time ASC, s.id ASC;";

		List<Showtime> items = new ArrayList<>();

		try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			int idx = 1;

			if (hasCinema) {
				ps.setInt(idx++, cinemaId);
			}

			if (hasMovie) {
				ps.setInt(idx++, movieId);
			}

			if (hasDate) {
				ps.setTimestamp(idx++, Timestamp.valueOf(startOfDay));
				ps.setTimestamp(idx++, Timestamp.valueOf(endOfDay));
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					items.add(mapShowtimeItem(rs));
				}
			}

			return items;

		} catch (SQLException e) {
			throw new RuntimeException("Failed to fetch showtimes (MODEL) with filters.", e);
		}
	}

	private Showtime mapShowtimeItem(ResultSet rs) throws SQLException {
		Showtime s = new Showtime();

		s.setId(rs.getInt("id"));
		s.setMovieId(rs.getInt("movie_id"));
		s.setRoomId(rs.getInt("room_id"));

		Timestamp st = rs.getTimestamp("start_time");
		Timestamp et = rs.getTimestamp("end_time");
		Timestamp ca = rs.getTimestamp("created_at");

		s.setStartTime(st != null ? st.toLocalDateTime() : null);
		s.setEndTime(et != null ? et.toLocalDateTime() : null);

		s.setBasePrice(rs.getBigDecimal("base_price"));
		s.setStatus(parseShowtimeStatus(rs.getString("status")));

		s.setCreatedAt(ca != null ? ca.toLocalDateTime() : null);

		return s;
	}

	private Showtime.ShowtimeStatus parseShowtimeStatus(String dbValue) {
		if (dbValue == null)
			return null;
		try {
			return Showtime.ShowtimeStatus.valueOf(dbValue.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException("Invalid showtime.status value in DB: " + dbValue, ex);
		}
	}

	// NOTE: paste this method into your repository class that currently contains it.
	// Only edits: add c.image_url select + setCinemaImageUrl(...) inside dto init.

	public List<MovieWithShowtimesDtos> getCinemasWithShowtimesByMovieId(int movieId, Integer cinemaId) {
	    String baseSql = "SELECT " +
	            "  c.id AS cinema_id, " +
	            "  c.name AS cinema_name, " +
	            "  c.address AS address, " +
	            "  c.image_url AS cinema_image_url, " +     // ✅ ADD
	            "  m.poster_url AS poster_url, " +
	            "  m.duration_minutes AS duration_minutes, " +
	            "  s.id AS showtime_id, " +
	            "  s.start_time AS start_time, " +
	            "  s.end_time AS end_time, " +
	            "  r.type AS room_type " +
	            "FROM showtime s " +
	            "JOIN room r ON r.id = s.room_id " +
	            "JOIN cinema c ON c.id = r.cinema_id " +
	            "JOIN movie m ON m.id = s.movie_id " +
	            "WHERE s.movie_id = ? " +
	            "  AND (s.status = 'SCHEDULED' OR s.status IS NULL) " +
	            "  AND s.start_time >= NOW() " +
	            "  AND s.end_time > s.start_time " +
	            "  AND (m.duration_minutes IS NULL OR " +
	            "       TIMESTAMPDIFF(MINUTE, s.start_time, s.end_time) >= m.duration_minutes) " +
	            "  AND (c.is_active = 1 OR c.is_active IS NULL) ";

	    boolean hasCinemaFilter = (cinemaId != null && cinemaId > 0);
	    if (hasCinemaFilter) {
	        baseSql += "  AND c.id = ? ";
	    }

	    baseSql += " ORDER BY c.id ASC, s.start_time ASC";

	    java.time.ZoneId VN_ZONE = java.time.ZoneId.of("Asia/Ho_Chi_Minh");
	    java.time.format.DateTimeFormatter ISO_OFFSET = java.time.format.DateTimeFormatter
	            .ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

	    java.util.Map<Integer, MovieWithShowtimesDtos> grouped = new java.util.LinkedHashMap<>();

	    try (java.sql.Connection con = dataSource.getConnection();
	         java.sql.PreparedStatement ps = con.prepareStatement(baseSql)) {

	        int idx = 1;
	        ps.setInt(idx++, movieId);

	        if (hasCinemaFilter) {
	            ps.setInt(idx++, cinemaId);
	        }

	        try (java.sql.ResultSet rs = ps.executeQuery()) {
	            java.time.LocalDateTime now = java.time.LocalDateTime.now();

	            while (rs.next()) {
	                java.sql.Timestamp st = rs.getTimestamp("start_time");
	                java.sql.Timestamp et = rs.getTimestamp("end_time");

	                if (st == null || et == null) {
	                    continue;
	                }

	                java.time.LocalDateTime startTime = st.toLocalDateTime();
	                java.time.LocalDateTime endTime = et.toLocalDateTime();

	                if (startTime.isBefore(now)) continue;
	                if (!endTime.isAfter(startTime)) continue;

	                int cId = rs.getInt("cinema_id");
	                MovieWithShowtimesDtos dto = grouped.get(cId);

	                if (dto == null) {
	                    dto = new MovieWithShowtimesDtos();
	                    dto.setCinemaId(cId);
	                    dto.setCinemaName(rs.getString("cinema_name"));
	                    dto.setAddress(rs.getString("address"));

	                    // ✅ cinema image (DB path: "cinema/xxx.jpg")
	                    dto.setCinemaImageUrl(rs.getString("cinema_image_url"));

	                    // movie poster (kept)
	                    dto.setPosterUrl(rs.getString("poster_url"));

	                    Object durObj = rs.getObject("duration_minutes");
	                    Integer durationMinutes = (durObj == null) ? null : ((Number) durObj).intValue();
	                    dto.setDurationMinutes(durationMinutes);

	                    grouped.put(cId, dto);
	                }

	                Integer dur = dto.getDurationMinutes();
	                if (dur != null) {
	                    long diffMin = java.time.Duration.between(startTime, endTime).toMinutes();
	                    if (diffMin < dur) continue;
	                }

	                String roomType = rs.getString("room_type");
	                String type = (roomType == null || roomType.isBlank()) ? "2D" : roomType.trim();

	                CinemaBooking.Group2.dtos.showtime.ShowtimeItemDtos stDto =
	                        new CinemaBooking.Group2.dtos.showtime.ShowtimeItemDtos();
	                stDto.setId(rs.getInt("showtime_id"));

	                String startIso = startTime.atZone(VN_ZONE).format(ISO_OFFSET);
	                stDto.setStartTime(startIso);

	                stDto.setType(type);

	                dto.getShowtimes().add(stDto);
	            }
	        }

	        return new java.util.ArrayList<>(grouped.values());

	    } catch (java.sql.SQLException e) {
	        throw new RuntimeException("FAILED TO FETCH CINEMAS & SHOWTIMES BY MOVIE ID: " + movieId, e);
	    }
	}

	// New method: fetch cinema name for a given showtime id
	public String getCinemaNameByShowtime(int showtimeId) {
		String sql = "SELECT c.name AS cinema_name " +
				 "FROM showtime s " +
				 "JOIN room r ON r.id = s.room_id " +
				 "JOIN cinema c ON c.id = r.cinema_id " +
				 "WHERE s.id = ? LIMIT 1";

		try (java.sql.Connection con = dataSource.getConnection();
			 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, showtimeId);

			try (java.sql.ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getString("cinema_name");
				}
			}

			return null;

		} catch (java.sql.SQLException e) {
			throw new RuntimeException("Failed to fetch cinema name for showtime: " + showtimeId, e);
		}
	}

	// New method: fetch showtime details used by ShowtimeService
	public Map<String, Object> getShowtimeDetails(int showtimeId) {
		String sql = "SELECT " +
				 "  s.base_price AS base_price, " +
				 "  m.title AS movie_title, " +
				 "  COALESCE(m.poster_url, m.banner_url) AS poster_url, " +
				 "  m.genre AS genre, " +
				 "  m.duration_minutes AS duration_minutes, " +
				 "  r.name AS room_name, " +
				 "  c.address AS address, " +
				 "  s.start_time AS start_time " +
				 "FROM showtime s " +
				 "JOIN room r ON r.id = s.room_id " +
				 "JOIN cinema c ON c.id = r.cinema_id " +
				 "JOIN movie m ON m.id = s.movie_id " +
				 "WHERE s.id = ? LIMIT 1";

		try (java.sql.Connection con = dataSource.getConnection();
			 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, showtimeId);

			try (java.sql.ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) return null;

				Map<String, Object> map = new LinkedHashMap<>();

				map.put("base_price", rs.getBigDecimal("base_price"));
				map.put("movie_title", rs.getString("movie_title"));
				map.put("poster_url", rs.getString("poster_url"));
				map.put("genre", rs.getString("genre"));

				Object durObj = rs.getObject("duration_minutes");
				map.put("duration_minutes", durObj);

				map.put("room_name", rs.getString("room_name"));
				map.put("address", rs.getString("address"));
				map.put("start_time", rs.getTimestamp("start_time"));

				return map;
			}

		} catch (java.sql.SQLException e) {
			throw new RuntimeException("Failed to fetch showtime details for id: " + showtimeId, e);
		}
	}

}