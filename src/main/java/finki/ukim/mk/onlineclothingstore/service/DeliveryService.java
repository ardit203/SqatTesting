package finki.ukim.mk.onlineclothingstore.service;



import finki.ukim.mk.onlineclothingstore.model.Delivery;

import java.util.List;

public interface DeliveryService {
    List<Delivery> findAll();

    Delivery findById(Long id);

    Delivery create(Long orderId);

    Delivery confirm(Long id);

    Delivery ship(Long id);

    Delivery deliver(Long id);

    Delivery cancel(Long id);

    void deleteById(Long id);
}