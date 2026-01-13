package CinemaBooking.Group2.service;

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
import CinemaBooking.Group2.dtos.movie.MovieEditDtos;
import CinemaBooking.Group2.dtos.movie.MovieMediaDtos;
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

    // LẤY DANH SÁCH TẤT CẢ PHIM VÀ CHUYỂN SANG DTO (GHÉP THÊM /MEDIA).
    public List<MovieDtos> getAllMovie(int page, int perPage) {
        try {
            List<Movie> movies = movieRepository.getAllMovie(page, perPage);
            return movies.stream().map(m -> {
                MovieDtos dto = MovieMapper.toResponseDto(m);
                dto.setPosterUrl(toPublicMediaUrl(m.getPosterUrl()));
                dto.setBannerUrl(toPublicMediaUrl(m.getBannerUrl()));
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO FETCH MOVIE LIST.", e);
        }
    }

    // LẤY DANH SÁCH PHIM THEO TRẠNG THÁI (COMING_SOON/NOW_SHOWING) CÓ PHÂN TRANG (GHÉP THÊM /MEDIA).
    public List<MovieDtos> getAllMovieStatus(int page, int perPage) {
        try {
            List<Movie> movies = movieRepository.getAllMovieCommingSoon(page, perPage);
            return movies.stream().map(m -> {
                MovieDtos dto = MovieMapper.toResponseDto(m);
                dto.setPosterUrl(toPublicMediaUrl(m.getPosterUrl()));
                dto.setBannerUrl(toPublicMediaUrl(m.getBannerUrl()));
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO FETCH MOVIES BY STATUS WITH PAGINATION.", e);
        }
    }

    // ĐẾM TỔNG SỐ PHIM TRẠNG THÁI COMING_SOON HOẶC NOW_SHOWING.
    public int countMovieStatus() {
        try {
            return movieRepository.countMovieComingSoonNowShowing();
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO COUNT MOVIES BY STATUS.", e);
        }
    }

    // LẤY CHI TIẾT PHIM THEO ID (GHÉP THÊM /MEDIA).
    public MovieDetailDtos getMovieDetailById(int id) {
        try {
            Movie movie = movieRepository.getMovieDetailById(id);
            if (movie == null) return null;

            MovieDetailDtos res = MovieMapper.toDetailDto(movie);
            res.setPosterUrl(toPublicMediaUrl(movie.getPosterUrl()));
            res.setBannerUrl(toPublicMediaUrl(movie.getBannerUrl()));
            return res;

        } catch (Exception e) {
            throw new RuntimeException("FAILED TO GET MOVIE DETAIL.", e);
        }
    }

    // TẠO PHIM: LƯU POSTER/BANNER, LƯU RELATIVE PATH VÀO DB, TRẢ DTO (GHÉP THÊM /MEDIA).
    public MovieDetailDtos createMovie(MovieCreateDtos data, MultipartFile poster, MultipartFile banner) {
        if (poster == null || poster.isEmpty()) throw new IllegalArgumentException("POSTER IS REQUIRED.");
        if (banner == null || banner.isEmpty()) throw new IllegalArgumentException("BANNER IS REQUIRED.");

        Path posterPath = null;
        Path bannerPath = null;
        boolean inserted = false;

        try {
            posterPath = saveImageToFolder(poster, "movie/posters");
            bannerPath = saveImageToFolder(banner, "movie/banners");

            String posterRel = toRelativePath(posterPath);
            String bannerRel = toRelativePath(bannerPath);

            Movie movie = MovieMapper.toModel(data);
            if (movie.getStatus() == null) movie.setStatus(Movie.MovieStatus.COMING_SOON);

            movie.setPosterUrl(posterRel);
            movie.setBannerUrl(bannerRel);

            int newId = movieRepository.createNewMovieReturnId(movie);
            if (newId <= 0) throw new RuntimeException("FAILED TO INSERT MOVIE (NO GENERATED ID).");

            inserted = true;
            movie.setId(newId);

            MovieDetailDtos res = MovieMapper.toDetailDto(movie);
            res.setPosterUrl(toPublicMediaUrl(posterRel));
            res.setBannerUrl(toPublicMediaUrl(bannerRel));
            return res;

        } catch (Exception ex) {
            if (!inserted) {
                safeDelete(posterPath);
                safeDelete(bannerPath);
            }
            throw new RuntimeException("CREATE MOVIE FAILED: " + ex.getMessage(), ex);
        }
    }

    // EDIT MOVIE: UPDATE FIELD + OPTIONAL POSTER/BANNER, DELETE OLD FILES AFTER DB UPDATE SUCCESS (GHÉP THÊM /MEDIA).
    public MovieDetailDtos updateMovie(int id, MovieEditDtos data, MultipartFile poster, MultipartFile banner) {
        if (id <= 0) throw new IllegalArgumentException("INVALID MOVIE ID.");

        MovieMediaDtos oldMedia = movieRepository.getMediaPathById(id);
        if (oldMedia == null) throw new IllegalArgumentException("MOVIE NOT FOUND WITH ID=" + id);

        boolean hasNewPoster = isProvidedFile(poster);
        boolean hasNewBanner = isProvidedFile(banner);

        Path newPosterPath = null;
        Path newBannerPath = null;

        try {
            String newPosterRel = null;
            String newBannerRel = null;

            if (hasNewPoster) {
                newPosterPath = saveImageToFolder(poster, "movie/posters");
                newPosterRel = toRelativePath(newPosterPath);
            }

            if (hasNewBanner) {
                newBannerPath = saveImageToFolder(banner, "movie/banners");
                newBannerRel = toRelativePath(newBannerPath);
            }

            Movie movie = MovieMapper.toModelEdit(data);
            movie.setId(id);

            // SET NULL WHEN NO NEW IMAGE (REQUIRES COALESCE IN SQL TO KEEP OLD URL).
            movie.setPosterUrl(hasNewPoster ? newPosterRel : null);
            movie.setBannerUrl(hasNewBanner ? newBannerRel : null);

            if (movie.getStatus() == null) movie.setStatus(Movie.MovieStatus.COMING_SOON);

            boolean ok = movieRepository.updateMovieById(id, movie);
            if (!ok) {
                safeDelete(newPosterPath);
                safeDelete(newBannerPath);
                throw new RuntimeException("FAILED TO UPDATE MOVIE (NO ROWS AFFECTED).");
            }

            if (hasNewPoster) safeDelete(resolveUploadPath(oldMedia.getPosterUrl()));
            if (hasNewBanner) safeDelete(resolveUploadPath(oldMedia.getBannerUrl()));

            String posterFinalRel = hasNewPoster ? newPosterRel : oldMedia.getPosterUrl();
            String bannerFinalRel = hasNewBanner ? newBannerRel : oldMedia.getBannerUrl();

            MovieDetailDtos res = MovieMapper.toDetailDto(movie);
            res.setPosterUrl(toPublicMediaUrl(posterFinalRel));
            res.setBannerUrl(toPublicMediaUrl(bannerFinalRel));
            return res;

        } catch (Exception ex) {
            safeDelete(newPosterPath);
            safeDelete(newBannerPath);
            throw new RuntimeException("EDIT MOVIE FAILED: " + ex.getMessage(), ex);
        }
    }

    // XÓA PHIM THEO ID.
    public boolean deleteMovie(int id) {
        try {
            if (id <= 0) return false;
            return movieRepository.deleteMovieById(id);
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO DELETE MOVIE BY ID.", e);
        }
    }

    // GHÉP URL PUBLIC TỪ RELATIVE PATH TRONG DB (VD: movie/posters/a.jpg -> /media/movie/posters/a.jpg).
    private String toPublicMediaUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return null;
        return normalizePrefix(publicPrefix) + "/" + relativePath;
    }

    // KIỂM TRA FILE CÓ THỰC SỰ ĐƯỢC GỬI LÊN (TRÁNH PART RỖNG).
    private boolean isProvidedFile(MultipartFile f) {
        return f != null && !f.isEmpty() && f.getSize() > 0
                && f.getOriginalFilename() != null && !f.getOriginalFilename().isBlank();
    }

    // CHUẨN HÓA PUBLIC PREFIX ĐỂ GHÉP URL MEDIA (VD: /MEDIA).
    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) return "/media";
        String p = prefix.trim();
        if (!p.startsWith("/")) p = "/" + p;
        if (p.length() > 1 && p.endsWith("/")) p = p.substring(0, p.length() - 1);
        return p;
    }

    // XÓA FILE AN TOÀN (KHÔNG LÀM GIÁN ĐOẠN LUỒNG CHÍNH).
    private void safeDelete(Path path) {
        if (path == null) return;
        try {
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
        }
    }

    // TẠO ROOT UPLOADS ỔN ĐỊNH (NẾU uploadDir RELATIVE THÌ NEO VÀO user.dir).
    private Path uploadRoot() {
        Path p = Paths.get(uploadDir);
        if (!p.isAbsolute()) {
            p = Paths.get(System.getProperty("user.dir")).resolve(p);
        }
        return p.toAbsolutePath().normalize();
    }

    // LẤY EXTENSION TỪ TÊN FILE (VD: JPG, PNG, WEBP).
    private String getExtension(String filename) {
        if (filename == null) return "";
        String name = filename.trim();

        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) name = name.substring(slash + 1);

        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return "";
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    // LƯU ẢNH VÀO UPLOADS/<FOLDER> VỚI TÊN UUID, KIỂM TRA EXTENSION, CONTENT-TYPE (NẾU CÓ).
    private Path saveImageToFolder(MultipartFile file, String folderName) throws Exception {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("FILE MUST BE AN IMAGE.");
        }

        String original = file.getOriginalFilename();
        String ext = getExtension(original);
        if (ext.isBlank() || !ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("INVALID IMAGE EXTENSION: " + ext);
        }

        Path root = uploadRoot();
        Path dir = root.resolve(folderName).normalize();
        Files.createDirectories(dir);

        String filename = UUID.randomUUID() + "." + ext;
        Path target = dir.resolve(filename);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        if (!Files.exists(target) || Files.size(target) <= 0) {
            throw new RuntimeException("FILE SAVE FAILED: " + target);
        }

        return target;
    }

    // CHUYỂN PATH TUYỆT ĐỐI THÀNH PATH RELATIVE ĐỂ LƯU DB (DẠNG movie/posters/xxx.jpg).
    private String toRelativePath(Path absoluteSavedPath) {
        Path root = uploadRoot();
        Path abs = absoluteSavedPath.toAbsolutePath().normalize();

        if (!abs.startsWith(root)) {
            throw new IllegalArgumentException("SAVED PATH IS OUTSIDE UPLOAD DIR: " + abs);
        }

        return root.relativize(abs).toString().replace('\\', '/');
    }

    // CHUYỂN RELATIVE TRONG DB THÀNH PATH TUYỆT ĐỐI TRONG UPLOADS ĐỂ XÓA FILE.
    private Path resolveUploadPath(String relative) {
        if (relative == null || relative.isBlank()) return null;

        Path rel = Paths.get(relative).normalize();
        if (rel.isAbsolute() || rel.startsWith("..")) {
            throw new IllegalArgumentException("INVALID STORED PATH: " + relative);
        }

        Path root = uploadRoot();
        Path full = root.resolve(rel).normalize();
        if (!full.startsWith(root)) {
            throw new IllegalArgumentException("PATH ESCAPES UPLOAD DIR: " + relative);
        }

        return full;
    }
}
