package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.dto.OrderDto;
import finki.ukim.mk.onlineclothingstore.model.Order;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    List<Order> findAll();

    List<Order> findByUsername(String username);

    Order findById(Long id);

    Order create(String username, String address, String email, String phone, String city,
                 int zip, String country, String paymentMethod, Double total);

    void deleteById(Long id);

    List<OrderDto> toOrderDtos(List<Order> orders);

    Page<Order> findPage(Long id,
                         DeliveryStatus status,
                         String username,
                         LocalDateTime fromDate,
                         LocalDateTime toDate,
                         Double fromPrice,
                         Double toPrice,
                         Integer pageNum,
                         Integer pageSize,
                         String sort);
}