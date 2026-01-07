package CinemaBooking.Group2.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.dtos.movie.MovieCreateDtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieDtos;
import CinemaBooking.Group2.mappers.MovieMapper;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.repositories.MovieRepository;

@Service
public class MovieService {

    @Autowired private MovieRepository movieRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.public-prefix:/media}")
    private String publicPrefix;

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");


    /*
     * Lấy danh sách tất cả phim và chuyển đổi sang định dạng DTO.
     */
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


    /*
     * Lấy danh sách phim đang hiển thị (Sắp chiếu & Đang chiếu) có phân trang.
     */
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


    /*
     * Đếm tổng số lượng phim có trạng thái Sắp chiếu hoặc Đang chiếu.
     */
    public int countMovieStatus() {
        try {
            return movieRepository.countMovieComingSoonNowShowing();
        } catch (Exception e) {
            throw new RuntimeException("Failed to count movies by status.", e);
        }
    }


    /*
     * Tìm kiếm và lấy thông tin chi tiết của một bộ phim dựa trên ID.
     */
    public MovieDetailDtos getMovieDetailById(int id) {
        try {
            Movie movie = movieRepository.getMovieDetailById(id);
            if (movie == null) return null;
            return MovieMapper.toDetailDto(movie);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get movie detail in service layer.", e);
        }
    }


    /*
     * Quy trình tạo mới phim: Lưu file ảnh, map dữ liệu vào Database và trả về thông tin chi tiết.
     */
    public MovieDetailDtos createMovie(MovieCreateDtos data, MultipartFile poster, MultipartFile banner) {
        if (poster == null || poster.isEmpty()) {
            throw new IllegalArgumentException("Poster is required");
        }
        if (banner == null || banner.isEmpty()) {
            throw new IllegalArgumentException("Banner is required");
        }

        Path posterPath = null;
        Path bannerPath = null;

        try {
        	posterPath = saveImageToFolder(poster, "movie/posters");
        	bannerPath = saveImageToFolder(banner, "movie/banners");


            String posterRel = toRelativePath(posterPath);
            String bannerRel = borderPath(bannerPath); // logic giữ nguyên: toRelativePath

            Movie movie = MovieMapper.toModel(data);

            if (movie.getStatus() == null) {
                movie.setStatus(Movie.MovieStatus.COMING_SOON);
            }

            movie.setPosterUrl(posterRel);
            movie.setBannerUrl(bannerRel);

            int newId = movieRepository.createNewMovieReturnId(movie);
            if (newId <= 0) {
                safeDelete(posterPath);
                safeDelete(bannerPath);
                throw new RuntimeException("Failed to insert movie (no generated id returned).");
            }
            movie.setId(newId);

            MovieDetailDtos res = MovieMapper.toDetailDto(movie);

            String prefix = normalizePrefix(publicPrefix);
            res.setPosterUrl(prefix + "/" + posterRel);
            res.setBannerUrl(prefix + "/" + bannerRel);

            return res;

        } catch (Exception ex) {
            safeDelete(posterPath);
            safeDelete(bannerPath);
            throw new RuntimeException("Create movie failed: " + ex.getMessage(), ex);
        }
    }


    /*
     * Chuẩn hóa đường dẫn prefix cho các tài nguyên static (media).
     */
    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) return "/media";

        prefix = prefix.trim();

        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }

        if (prefix.length() > 1 && prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length() - 1);
        }

        return prefix;
    }


    /*
     * Xóa tệp tin một cách an toàn, không gây ngắt quãng luồng xử lý chính.
     */
    private void safeDelete(Path path) {
        if (path == null) return;
        try {
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
        }
    }


    /*
     * Xóa thông tin phim khỏi hệ thống theo ID.
     */
    public boolean deleteMovie(int id) {
        try {
            if (id <= 0) return false;
            return movieRepository.deleteMovieById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete movie by id", e);
        }
    }


    /*
     * Phân tích và lấy định dạng (extension) của tệp tin từ tên gốc.
     */
    private String getExtension(String filename) {
        if (filename == null) return "";
        String name = filename.trim();
        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) name = name.substring(slash + 1);

        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return "";
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }


    /*
     * Lưu trữ tệp tin hình ảnh vào thư mục chỉ định với tên tệp duy nhất (UUID).
     */
    private Path saveImageToFolder(MultipartFile file, String folderName) throws Exception {
        // validate content-type cơ bản
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // validate extension
        String original = file.getOriginalFilename();
        String ext = getExtension(original);
        if (ext.isBlank() || !ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("Invalid image extension: " + ext);
        }

        // <uploadDir>/<folderName>  (folderName: "poster" | "banner")
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path dir = root.resolve(folderName).normalize();
        Files.createDirectories(dir);

        String filename = UUID.randomUUID() + "." + ext;
        Path target = dir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return target; // đây chính là posterPath / bannerPath
    }



    /*
     * Chuyển đổi đường dẫn tuyệt đối sang đường dẫn tương đối phục vụ lưu trữ database.
     */
    private String toRelativePath(Path absoluteSavedPath) {
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        return root.relativize(absoluteSavedPath.toAbsolutePath().normalize())
                   .toString().replace('\\', '/');
    }

    /*
     * 
     */
    private String borderPath(Path bannerPath) {
        return toRelativePath(bannerPath);
    }

}