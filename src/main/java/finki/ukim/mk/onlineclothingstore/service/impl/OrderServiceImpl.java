package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.dto.OrderDto;
import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;
import finki.ukim.mk.onlineclothingstore.model.exceptions.OrderNotFoundException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.PriceCannotBeLessThanZeroException;
import finki.ukim.mk.onlineclothingstore.repository.OrderRepository;
import finki.ukim.mk.onlineclothingstore.repository.OrderVariantRepository;
import finki.ukim.mk.onlineclothingstore.service.DeliveryService;
import finki.ukim.mk.onlineclothingstore.service.OrderService;
import finki.ukim.mk.onlineclothingstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static finki.ukim.mk.onlineclothingstore.service.FieldFilterSpecification.*;
import static finki.ukim.mk.onlineclothingstore.service.FieldFilterSpecification.filterEquals;
import static finki.ukim.mk.onlineclothingstore.service.FieldFilterSpecification.filterEqualsV;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderVariantRepository orderVariantRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findByUsername(String username) {
        User user = userService.findByUsername(username);
        return orderRepository.findByUser(user);
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Override
    public Order create(String username, String address, String email, String phone, String city, int zip, String country, String paymentMethod, Double total) {
        if (total < 0) {
            throw new PriceCannotBeLessThanZeroException();
        }
        User user = userService.findByUsername(username);
        Order order = new Order(user, LocalDateTime.now(), total, address, email, phone, city, zip, country, paymentMethod);
        order.setDelivery(new Delivery(order));
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<OrderDto> toOrderDtos(List<Order> orders) {
        if (orders == null) {
            return List.of();
        }
        List<OrderDto> orderDtos = new ArrayList<>();
        int size = orders.size();
        for (int i = 0; i < size; i++) {
            List<OrderVariant> orderVariants = orderVariantRepository.findByOrder(orders.get(i));
            OrderDto orderDto = OrderDto.from(orders.get(i), orderVariants);
            orderDtos.add(orderDto);
        }
        return orderDtos;
    }

    @Override
    public Page<Order> findPage(Long id,
                                DeliveryStatus status,
                                String username,
                                LocalDateTime fromDate,
                                LocalDateTime toDate,
                                Double fromPrice,
                                Double toPrice,
                                Integer pageNum,
                                Integer pageSize,
                                String sort) {
        Specification<Order> specification = Specification.allOf(
                filterEquals(Order.class, "id", id),
                filterEqualsV(Order.class, "delivery.status", status),
                filterEquals(Order.class, "user.username", username),
                greaterThan(Order.class, "madeAt", fromDate),
                lessThan(Order.class, "madeAt", toDate),
                greaterThan(Order.class, "total", fromPrice),
                lessThan(Order.class, "total", toPrice)
        );


        return this.orderRepository.findAll(
                specification,
                PageRequest.of(pageNum - 1, pageSize, sortFactory(sort)));
    }

    private Sort sortFactory(String sort) {
        switch (sort) {
            case "byUsernameDesc":
                return Sort.by(Sort.Direction.DESC, "user.username");
            case "byMadeAtDesc":
                return Sort.by(Sort.Direction.DESC, "madeAt");
            case "byMadeAtAsc":
                return Sort.by(Sort.Direction.ASC, "madeAt");
            case "byTotalAsc":
                return Sort.by(Sort.Direction.ASC, "total");
            case "byTotalDesc":
                return Sort.by(Sort.Direction.DESC, "total");
            default:
                return Sort.by(Sort.Direction.ASC, "user.username");
        }
    }
}
