//package finki.ukim.mk.onlineclothingstore.service;
//
//import finki.ukim.mk.onlineclothingstore.model.Category;
//import finki.ukim.mk.onlineclothingstore.model.Discount;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public interface DiscountService {
//    List<Discount> listAll();
//
//    Discount findById(Long id);
//
//    Discount create(Long categoryId, int value, LocalDateTime validFrom, LocalDateTime validTo);
//
//    Discount update(Long id, Long categoryId, int value, LocalDateTime validFrom, LocalDateTime validTo);
//
//    Discount activate(Discount discount);
//
//    Discount deactivate(Discount discount);
//
//    boolean isActive(Discount discount);
//
//    void check();
//
//    void deleteById(Long id);
//}
