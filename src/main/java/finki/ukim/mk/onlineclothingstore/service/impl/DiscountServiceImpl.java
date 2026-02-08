//package finki.ukim.mk.onlineclothingstore.service.impl;
//
//import finki.ukim.mk.onlineclothingstore.model.Category;
//import finki.ukim.mk.onlineclothingstore.model.Discount;
//import finki.ukim.mk.onlineclothingstore.model.exceptions.DiscountNotFoundException;
//import finki.ukim.mk.onlineclothingstore.repository.DiscountRepository;
//import finki.ukim.mk.onlineclothingstore.service.CategoryService;
//import finki.ukim.mk.onlineclothingstore.service.DiscountService;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@AllArgsConstructor
//public class DiscountServiceImpl implements DiscountService {
//    private final DiscountRepository discountRepository;
//    private final CategoryService categoryService;
//    @Override
//    public List<Discount> listAll() {
//        return discountRepository.findAll();
//    }
//
//    @Override
//    public Discount findById(Long id) {
//        return discountRepository.findById(id)
//                .orElseThrow(() -> new DiscountNotFoundException(id));
//    }
//
//    @Override
//    public Discount create(Long categoryId, int value, LocalDateTime validFrom, LocalDateTime validTo) {
//        Category category = categoryService.findById(categoryId);
//        Discount discount =  discountRepository.save(new Discount(category, value, validFrom, validTo));
//        categoryService.addDiscount(category, discount);
//        return discount;
//    }
//
//    @Override
//    public Discount update(Long id, Long categoryId, int value, LocalDateTime validFrom, LocalDateTime validTo) {
//        return null;
//    }
//
//    @Override
//    public Discount activate(Discount discount) {
//        discount.activate();
//        return discountRepository.save(discount);
//    }
//
//    @Override
//    public Discount deactivate(Discount discount) {
//        discount.deactivate();
//        return discountRepository.save(discount);
//    }
//
//    @Override
//    public boolean isActive(Discount discount) {
//        LocalDateTime now = LocalDateTime.now();
//        return now.isAfter(discount.getValidFrom()) && now.isBefore(discount.getValidTo());
//    }
//
//    @Override
//    public void check() {
//        List<Discount> discounts = listAll();
//
//        for (Discount discount : discounts){
//            if(isActive(discount)){
//                activate(discount);
//            }else {
//                deactivate(discount);
//            }
//        }
//    }
//
//    @Override
//    public void deleteById(Long id) {
//        discountRepository.deleteById(id);
//    }
//}
