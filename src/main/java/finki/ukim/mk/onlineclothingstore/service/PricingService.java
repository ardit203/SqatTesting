package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.CartVariant;
import finki.ukim.mk.onlineclothingstore.model.User;

import java.util.List;

public interface PricingService {
    Double calculateTotal(List<CartVariant> cartVariants);
    Double calculateTotal(String username);
}
