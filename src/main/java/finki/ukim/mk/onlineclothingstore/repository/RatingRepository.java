package finki.ukim.mk.onlineclothingstore.repository;

import finki.ukim.mk.onlineclothingstore.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProduct_Id(Long productId);
}
