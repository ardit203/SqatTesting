package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Rating;
import finki.ukim.mk.onlineclothingstore.model.exceptions.InvalidRatingException;
import finki.ukim.mk.onlineclothingstore.repository.RatingRepository;
import finki.ukim.mk.onlineclothingstore.service.ProductService;
import finki.ukim.mk.onlineclothingstore.service.RatingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final ProductService productService;
    @Override
    public List<Rating> findByProductId(Long productId) {
        return ratingRepository.findByProduct_Id(productId);
    }

    @Override
    public Rating create(int rating, Long productId) {
        if(rating < 0 || rating > 5){
            throw new InvalidRatingException();
        }
        return ratingRepository.save(new Rating(rating, productService.findById(productId)));
    }

    @Override
    public Double getAvgRating(List<Rating> ratings) {
        if(ratings == null){
            return 0.0;
        }
        return ratings
                .stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0);
    }
}
