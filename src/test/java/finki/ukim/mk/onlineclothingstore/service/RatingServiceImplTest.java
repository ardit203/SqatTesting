package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Rating;
import finki.ukim.mk.onlineclothingstore.model.exceptions.InvalidRatingException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.ProductNotFoundException;
import finki.ukim.mk.onlineclothingstore.repository.RatingRepository;
import finki.ukim.mk.onlineclothingstore.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RatingServiceImplTest {
    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private RatingServiceImpl ratingService;

    /* -------------------------------------------------
        findByProductId(Long productId)
    ------------------------------------------------- */

    @Test
    void RS_1_findByProductIdWhenNoRecordsExist() {
        //given
        Long productId = 1L;

        when(ratingRepository.findByProduct_Id(productId)).thenReturn(List.of());

        //when
        List<Rating> ratings = ratingService.findByProductId(productId);

        //then
        assertEquals(0, ratings.size());
        assertSame(List.of(), ratings);
        verify(ratingRepository).findByProduct_Id(productId);
        verifyNoMoreInteractions(ratingRepository);
        verifyNoInteractions(productService);
    }

    @Test
    void RS_2_findByProductIdWhenRecordsExist() {
        //given
        Long productId = 1L;

        List<Rating> expected = List.of(
                new Rating(1L, 2, null),
                new Rating(2L, 4, null)
        );

        when(ratingRepository.findByProduct_Id(productId)).thenReturn(expected);

        //when
        List<Rating> ratings = ratingService.findByProductId(productId);

        //then
        assertEquals(expected.size(), ratings.size());
        assertSame(expected, ratings);
        verify(ratingRepository).findByProduct_Id(productId);
        verifyNoMoreInteractions(ratingRepository);
        verifyNoInteractions(productService);
    }

    /* -------------------------------------------------
        create(int rating, Long productId)
    ------------------------------------------------- */

    @ParameterizedTest
    @CsvSource({//invalid boundary values
            "-1",
            "6"
    })
    void RS_3_createWithInvalidRatings(int invalidRating){
        // when + then
        assertThrows(InvalidRatingException.class,
                () -> ratingService.create(invalidRating, 1L));


        verifyNoInteractions(ratingRepository, productService);

    }

    @ParameterizedTest
    @CsvSource({//valid boundary values
            "0",
            "5"
    })
    void RS_4_createWithValidRatings(int rating){
        // given
        Long productId = 1L;
        Product product = mock(Product.class);

        when(productService.findById(productId)).thenReturn(product);
        when(ratingRepository.save(any(Rating.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Rating created = ratingService.create(rating, productId);

        // then
        assertEquals(rating, created.getRating());
        assertSame(product, created.getProduct());

        verify(productService).findById(productId);
        verify(ratingRepository).save(created);
        verifyNoMoreInteractions(ratingRepository, productService);
    }

    @Test
    void RS_5_createWhenInvalidProductId(){
        // given
        Long missingProductId = 1L;
        when(productService.findById(missingProductId))
                .thenThrow(new ProductNotFoundException(missingProductId));

        // when + then
        assertThrows(ProductNotFoundException.class,
                () -> ratingService.create(5, missingProductId));


        verify(productService).findById(missingProductId);
        verify(ratingRepository, never()).save(any(Rating.class));
        verifyNoMoreInteractions(productService);
        verifyNoMoreInteractions(ratingRepository);
    }

    @Test
    void RS_6_createWithValidRatingAndValidProductId(){
        // given
        Long productId = 1L;
        int ratingValue = 3;
        Product product = mock(Product.class);

        when(productService.findById(productId)).thenReturn(product);
        when(ratingRepository.save(any(Rating.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Rating created = ratingService.create(ratingValue, productId);

        // then
        assertEquals(ratingValue, created.getRating());
        assertSame(product, created.getProduct());

        verify(productService).findById(productId);
        verify(ratingRepository).save(created);
        verifyNoMoreInteractions(ratingRepository, productService);

    }

    /* -------------------------------------------------
        getAvgRating(List<Rating> ratings)
    ------------------------------------------------- */

    @Test
    void RS_7_getAvgRatingWhenListIsNull(){
        assertEquals(0.0, ratingService.getAvgRating(null));
        verifyNoInteractions(ratingRepository, productService);
    }

    @Test
    void RS_8_getAvgRatingWhenListIsNotNotEmpty(){
        //given
        List<Rating> ratings = List.of(
                new Rating(1L, 5, null),
                new Rating(2L, 4, null),
                new Rating(3L, 3, null)
        );

        //when
        double avg = ratingService.getAvgRating(ratings);

        //then
        assertEquals(4.0, avg);
        verifyNoInteractions(ratingRepository, productService);
    }

    @Test
    void RS_9_getAvgRatingWhenListIsEmpty(){
        //when
        double avg = ratingService.getAvgRating(List.of());

        //then
        assertEquals(0.0, avg);
        verifyNoInteractions(ratingRepository, productService);
    }
}
