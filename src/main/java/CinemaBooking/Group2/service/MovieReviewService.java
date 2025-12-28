package CinemaBooking.Group2.service;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.movie_review.MovieReviewDtos;
import CinemaBooking.Group2.repositories.MovieReviewRepository;

@Service
public class MovieReviewService {
	@Autowired
	private MovieReviewRepository mvRepositories;
	

    public List<MovieReviewDtos> getAllReview() {
        try {
            return mvRepositories.getAllReview();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch movie review list. Please check repository/database.", e);
        }
    }
}
