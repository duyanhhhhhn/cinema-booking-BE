package CinemaBooking.Group2.service;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import CinemaBooking.Group2.dtos.PageResponse;
import CinemaBooking.Group2.dtos.movie_review.admin.MovieReviewDtos;
import CinemaBooking.Group2.dtos.movie_review.client.MovieReviewClientDtos;
import CinemaBooking.Group2.dtos.movie_review.client.RatingSummaryDtos;
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
    
    public PageResponse<MovieReviewClientDtos> getAllReviewClient(int page, int size) {
        try {
            if (page < 1) page = 1;
            if (size < 1) size = 10;
            if (size > 100) size = 100;
            int offset = (page - 1) * size;
            long totalItems = mvRepositories.countByRoleId(); 
            List<MovieReviewClientDtos> items = mvRepositories.getAllRating(size, offset);
            PageResponse<MovieReviewClientDtos> res = new PageResponse<>();
            res.setItems(items);
            res.setPage(page);
            res.setSize(size);
            res.setTotalItems(totalItems);
            res.setTotalPages((long) Math.ceil(totalItems * 1.0 / size));
            res.setSuccess(true);
            res.setMessage("success");
            return res;
        } 
        catch (Exception e) {
            throw new RuntimeException("Failed to fetch paged movie review list. Please check repository/database.", e);
        }
    }
    
    // function help map repositorires count average rating services
    public RatingSummaryDtos countRatingAverage(int movie_id) {
    	try {
    		float avg = mvRepositories.countRatingAverage(movie_id);
    		return new RatingSummaryDtos(movie_id, avg);
    	}
    	catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính trung bình tổng đánh giá hãy kiểm tra repo", e);
        }
    	
    }

}
