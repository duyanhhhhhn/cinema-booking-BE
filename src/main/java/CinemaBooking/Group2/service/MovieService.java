package CinemaBooking.Group2.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import CinemaBooking.Group2.dtos.movie.MovieCardtos;
import CinemaBooking.Group2.dtos.movie.MovieCreateDtos;
import CinemaBooking.Group2.dtos.movie.MovieDetailDtos;
import CinemaBooking.Group2.dtos.movie.MovieEditDtos;
import CinemaBooking.Group2.dtos.movie.MovieMediaDtos;
import CinemaBooking.Group2.dtos.movie.MoviePublicDtos;
import CinemaBooking.Group2.mappers.MovieMapper;
import CinemaBooking.Group2.models.Movie;
import CinemaBooking.Group2.models.Movie.MovieGenre;
import CinemaBooking.Group2.repositories.MovieRepository;

@Service
public class MovieService {

    @Autowired private MovieRepository movieRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.public-prefix:/media}")
    private String publicPrefix;

    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

    public List<MoviePublicDtos> getAllMovieCommingSoon(int page, int perPage, String keyword, Movie.MovieGenre genre) {
        try {
            if (page < 1) page = 1;
            if (perPage < 1) perPage = 10;

            String q = (keyword == null) ? null : keyword.trim();
            if (q != null && q.isBlank()) q = null;

            List<Movie> movies = movieRepository.getAllMovieCommingSoon(page, perPage, q, genre);

            return movies.stream().map(m -> {
            	MoviePublicDtos dto = MovieMapper.toPublicRes(m);
                dto.setPosterUrl(toPublicMediaUrl(m.getPosterUrl()));
                dto.setBannerUrl(toPublicMediaUrl(m.getBannerUrl()));
                return dto;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("FAILED TO FETCH MOVIE LIST (COMING_SOON/NOW_SHOWING).", e);
        }
    }

    
    
    public int countMovieStatus() {
        try {
            return movieRepository.countMovieComingSoonNowShowing();
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO COUNT MOVIES BY STATUS.", e);
        }
    }

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

    public List<MovieCardtos> getMoviesComingSoonAndNowShowing() {
        try {
            List<Movie> movies = movieRepository.getMoviesComingSoonAndNowShowing();
            if (movies == null) return List.of();

            List<MovieCardtos> res = new ArrayList<>();
            for (Movie m : movies) {
                MovieCardtos dto = MovieMapper.toPublicMovieStatus(m);
                if (dto != null) {
                    dto.setPosterUrl(toPublicMediaUrl(dto.getPosterUrl()));
                    res.add(dto);
                }
            }
            return res;

        } catch (Exception e) {
            throw new RuntimeException("FAILED TO FETCH MOVIES (COMING_SOON, NOW_SHOWING).", e);
        }
    }

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

    public MovieDetailDtos updateMovie(int id, MovieEditDtos data, MultipartFile poster, MultipartFile banner) {
        if (id <= 0) throw new IllegalArgumentException("INVALID MOVIE ID.");

        MovieMediaDtos oldMedia = movieRepository.getMediaPathById(id);
        if (oldMedia == null) throw new IllegalArgumentException("MOVIE NOT FOUND WITH ID=" + id);

        Movie existing = movieRepository.findById(id);
        if (existing == null) throw new IllegalArgumentException("MOVIE NOT FOUND WITH ID=" + id);

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

            Movie patch = MovieMapper.toModelEdit(data);
            patch.setId(id);

            patch.setTitle(normalizeText(patch.getTitle()));
            patch.setShortDescription(normalizeText(patch.getShortDescription()));
            patch.setDescription(normalizeText(patch.getDescription()));
            patch.setGenre(normalizeGenre(patch.getGenre()));
            patch.setLanguage(normalizeText(patch.getLanguage()));
            patch.setFormat(normalizeText(patch.getFormat()));
            patch.setDirector(normalizeText(patch.getDirector()));
            patch.setCast(normalizeText(patch.getCast()));
            patch.setTrailerUrl(normalizeText(patch.getTrailerUrl()));

            Integer durationPatch = normalizeDurationFromPatch(patch, data);

            Movie merged = new Movie();
            merged.setId(id);

            merged.setTitle(pick(patch.getTitle(), existing.getTitle()));
            merged.setShortDescription(pick(patch.getShortDescription(), existing.getShortDescription()));
            merged.setDescription(pick(patch.getDescription(), existing.getDescription()));

            int durationFinal = existing.getDurationMinutes();
            if (durationPatch != null) durationFinal = durationPatch;
            merged.setDurationMinutes(durationFinal);

            merged.setGenre(pick(patch.getGenre(), existing.getGenre()));
            merged.setLanguage(normalizeText(pick(patch.getLanguage(), existing.getLanguage())));
            merged.setFormat(pick(patch.getFormat(), existing.getFormat()));
            merged.setDirector(pick(patch.getDirector(), existing.getDirector()));
            merged.setCast(pick(patch.getCast(), existing.getCast()));

            merged.setPosterUrl(hasNewPoster ? newPosterRel : existing.getPosterUrl());
            merged.setBannerUrl(hasNewBanner ? newBannerRel : existing.getBannerUrl());
            merged.setTrailerUrl(pick(patch.getTrailerUrl(), existing.getTrailerUrl()));

            merged.setReleaseDate(pick(patch.getReleaseDate(), existing.getReleaseDate()));
            merged.setEndDate(pick(patch.getEndDate(), existing.getEndDate()));
            merged.setStatus(pick(patch.getStatus(), existing.getStatus()));
            merged.setCreatedAt(existing.getCreatedAt());

            boolean ok = movieRepository.updateMovieById(id, merged);
            if (!ok) {
                safeDelete(newPosterPath);
                safeDelete(newBannerPath);
                throw new RuntimeException("FAILED TO UPDATE MOVIE (NO ROWS AFFECTED).");
            }

            if (hasNewPoster) safeDelete(resolveUploadPath(oldMedia.getPosterUrl()));
            if (hasNewBanner) safeDelete(resolveUploadPath(oldMedia.getBannerUrl()));

            MovieDetailDtos res = MovieMapper.toDetailDto(merged);
            res.setPosterUrl(toPublicMediaUrl(merged.getPosterUrl()));
            res.setBannerUrl(toPublicMediaUrl(merged.getBannerUrl()));
            return res;

        } catch (Exception ex) {
            safeDelete(newPosterPath);
            safeDelete(newBannerPath);
            throw new RuntimeException("EDIT MOVIE FAILED: " + ex.getMessage(), ex);
        }
    }

    public boolean deleteMovie(int id) {
        try {
            if (id <= 0) return false;
            return movieRepository.deleteMovieById(id);
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO DELETE MOVIE BY ID.", e);
        }
    }

    private String toPublicMediaUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return null;
        return normalizePrefix(publicPrefix) + "/" + relativePath;
    }

    private boolean isProvidedFile(MultipartFile f) {
        return f != null && !f.isEmpty() && f.getSize() > 0
                && f.getOriginalFilename() != null && !f.getOriginalFilename().isBlank();
    }

    private String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) return "/media";
        String p = prefix.trim();
        if (!p.startsWith("/")) p = "/" + p;
        if (p.length() > 1 && p.endsWith("/")) p = p.substring(0, p.length() - 1);
        return p;
    }

    private void safeDelete(Path path) {
        if (path == null) return;
        try {
            Files.deleteIfExists(path);
        } catch (Exception ignored) {
        }
    }

    private Path uploadRoot() {
        Path p = Paths.get(uploadDir);
        if (!p.isAbsolute()) {
            p = Paths.get(System.getProperty("user.dir")).resolve(p);
        }
        return p.toAbsolutePath().normalize();
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        String name = filename.trim();

        int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
        if (slash >= 0) name = name.substring(slash + 1);

        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot == name.length() - 1) return "";
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

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

    private String toRelativePath(Path absoluteSavedPath) {
        Path root = uploadRoot();
        Path abs = absoluteSavedPath.toAbsolutePath().normalize();

        if (!abs.startsWith(root)) {
            throw new IllegalArgumentException("SAVED PATH IS OUTSIDE UPLOAD DIR: " + abs);
        }

        return root.relativize(abs).toString().replace('\\', '/');
    }

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

    public long countTotalMovies() {
        return movieRepository.countMovies();
    }

    private String normalizeText(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private Integer normalizePositive(Integer n) {
        if (n == null) return null;
        return n > 0 ? n : null;
    }

    private <T> T pick(T newVal, T oldVal) {
        return newVal != null ? newVal : oldVal;
    }

    private Integer normalizeDurationFromPatch(Movie patch, MovieEditDtos data) {
        try {
            return normalizePositive((Integer) (Object) patch.getDurationMinutes());
        } catch (Exception ignore) {
            int v = patch.getDurationMinutes();
            return v > 0 ? v : null;
        }
    }

    private MovieGenre normalizeGenre(MovieGenre genre) {
        return genre;
    }
    
    public long countMovieComingSoonNowShowing(String keyword, Movie.MovieGenre genre) {
        try {
            String q = (keyword == null) ? null : keyword.trim();
            if (q != null && q.isBlank()) q = null;

            return (long) movieRepository.countMoviesComingSoonNowShowing(q, genre);
        } catch (Exception e) {
            throw new RuntimeException("FAILED TO COUNT MOVIES (COMING_SOON, NOW_SHOWING).", e);
        }
    }

}
