package CinemaBooking.Group2.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.dtos.movie.MovieCreateDtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieDtos;
import CinemaBooking.Group2.dtos.movie.MovieEditDtos;
import CinemaBooking.Group2.mappers.MovieMapper;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.repositories.MovieRepository;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;


    /** Get all movies and map them to MovieResponse DTOs. */
    public List<MovieDtos> getAllMovie() {
        try {
            List<Movie> movies = movieRepository.getAllMovie();
            return movies.stream()
                    .map(MovieMapper::toResponseDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch movie list. Please check repository/database.", e);
        }
    }


    /** Get public movies (COMING_SOON and NOW_SHOWING) with pagination. */
    public List<MovieDtos> getAllMovieStatus(int page, int perPage) {
        try {
            List<Movie> movies = movieRepository.getAllMovieCommingSoon(page, perPage);
            return movies.stream()
                    .map(MovieMapper::toResponseDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch movies by status with pagination.", e);
        }
    }


    /** Count public movies (COMING_SOON and NOW_SHOWING). */
    public int countMovieStatus() {
        try {
            return movieRepository.countMovieComingSoonNowShowing();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count movies by status.", e);
        }
    }


    /** Get movie details by id and map to MovieDetailDtos. */
    public MovieDetailDtos getMovieDetailById(int id) {
        try {
            Movie movie = movieRepository.getMovieDetailById(id);
            if (movie == null) return null;
            return MovieMapper.toDetailDto(movie);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get movie detail in service layer.", e);
        }
    }


    /** Create a movie using JSON only (no images) and return the generated movie id. */
    public int createMovieJson(MovieCreateDtos dto) {
        if (dto == null) throw new RuntimeException("Request body is empty.");

        String title = dto.getTitle();
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Title is required.");
        }

        int duration = dto.getDurationMinutes();
        if (duration <= 0) {
            throw new RuntimeException("DurationMinutes must be > 0.");
        }

        Movie movie = MovieMapper.toModel(dto);
        if (movie == null) {
            throw new RuntimeException("Failed to map request to Movie model.");
        }

        if (movie.getStatus() == null) {
            movie.setStatus(Movie.MovieStatus.COMING_SOON);
        }

        movie.setPosterUrl(null);
        movie.setBannerUrl(null);

        int movieId = movieRepository.createNewMovieReturnId(movie);
        if (movieId <= 0) {
            throw new RuntimeException("Failed to create movie.");
        }

        return movieId;
    }


    /** Upload and set the poster image for a movie; store only relative path in database. */
    public boolean updatePosterImage(int movieId, MultipartFile poster) {
        if (movieId <= 0) throw new RuntimeException("Invalid movie id.");
        if (!movieRepository.existsById(movieId)) throw new RuntimeException("Movie not found.");

        try {
            String posterPath = saveToUploads(poster, "movie/posters", "poster");
            if (posterPath == null) throw new RuntimeException("Poster file is required.");

            movieRepository.updateMovieImages(movieId, posterPath, null);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload poster image.", e);
        }
    }


    /** Upload and set the banner image for a movie; store only relative path in database. */
    public boolean updateBannerImage(int movieId, MultipartFile banner) {
        if (movieId <= 0) throw new RuntimeException("Invalid movie id.");
        if (!movieRepository.existsById(movieId)) throw new RuntimeException("Movie not found.");

        try {
            String bannerPath = saveToUploads(banner, "movie/banners", "banner");
            if (bannerPath == null) throw new RuntimeException("Banner file is required.");

            movieRepository.updateMovieImages(movieId, null, bannerPath);
            return true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload banner image.", e);
        }
    }


    /** Create a new movie without uploading images (keeps old endpoints working). */
    public boolean createNewMovie(MovieCreateDtos dto) {
        try {
            int id = createMovieJson(dto);
            return id > 0;
        } catch (Exception e) {
            return false;
        }
    }


    /** Delete a movie by id. */
    public boolean deleteMovie(int id) {
        try {
            if (id <= 0) return false;
            return movieRepository.deleteMovieById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete movie by id", e);
        }
    }


    /** Update movie fields by id and return the latest movie detail DTO. */
//    public MovieDetailDtos updateMovie(int id, MovieEditDtos dto) {
//        if (!movieRepository.existsById(id)) {
//            throw new RuntimeException("Movie not found with id = " + id);
//        }
//        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
//            throw new RuntimeException("Title is required.");
//        }
//        if (dto.getDurationMinutes() <= 0) {
//            throw new RuntimeException("Duration must be > 0.");
//        }
//        if (dto.getReleaseDate() != null && dto.getEndDate() != null
//                && dto.getReleaseDate().after(dto.getEndDate())) {
//            throw new RuntimeException("ReleaseDate must be before EndDate.");
//        }
//
////        Movie movieToUpdate = MovieMapper.toModelEdit(dto);
//
//        boolean ok = movieRepository.updateMovieById(id, movieToUpdate);
//        if (!ok) {
//            throw new RuntimeException("Update movie failed.");
//        }
//
//        Movie updated = movieRepository.getMovieDetailById(id);
//        if (updated == null) {
//            throw new RuntimeException("Updated but cannot load movie.");
//        }
//
//        return MovieMapper.toDetailDto(updated);
//    }


    /** Save an image file into uploads/{subFolder} and return the relative path for database storage. */
    private String saveToUploads(MultipartFile file, String subFolder, String prefix) throws Exception {
        if (file == null || file.isEmpty()) return null;

        String ext = safeExt(file.getOriginalFilename());
        String filename = prefix + "_" + UUID.randomUUID() + "." + ext;

        Path folder = Paths.get(uploadDir, subFolder);
        Files.createDirectories(folder);

        System.out.println("UPLOAD_DIR_ABS=" + folder.toAbsolutePath().normalize());

        Path target = folder.resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return subFolder + "/" + filename;
    }


    /** Get a safe image file extension; fallback to png if unknown. */
    private String safeExt(String original) {
        if (original == null) return "png";
        String lower = original.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "jpg";
        if (lower.endsWith(".png")) return "png";
        if (lower.endsWith(".webp")) return "webp";
        return "png";
    }
}
