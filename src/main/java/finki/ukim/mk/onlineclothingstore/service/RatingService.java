package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Rating;

import java.util.List;

public interface RatingService {
    List<Rating> findByProductId(Long productId);
    Rating create(int rating, Long productId);
    Double getAvgRating(List<Rating> ratings);
}