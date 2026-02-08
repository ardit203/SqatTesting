package finki.ukim.mk.onlineclothingstore.service.impl;

import finki.ukim.mk.onlineclothingstore.model.Delivery;
import finki.ukim.mk.onlineclothingstore.model.Order;
import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;
import finki.ukim.mk.onlineclothingstore.model.exceptions.*;
import finki.ukim.mk.onlineclothingstore.repository.DeliveryRepository;
import finki.ukim.mk.onlineclothingstore.repository.OrderRepository;
import finki.ukim.mk.onlineclothingstore.service.DeliveryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    @Override
    public List<Delivery> findAll() {
        return deliveryRepository.findAll();
    }

    @Override
    public Delivery findById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryNotFoundException(id));
    }

    @Override
    public Delivery create(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return deliveryRepository.save(new Delivery(order));
    }

    @Override
    public Delivery confirm(Long id) {
        Delivery delivery = findById(id);
        if(delivery.getStatus() != DeliveryStatus.PENDING){
            throw new DeliveryCannotBeConfirmedException(id);
        }

        delivery.confirm();
        return deliveryRepository.save(delivery);
    }

    @Override
    public Delivery ship(Long id) {
        Delivery delivery = findById(id);
        if(delivery.getStatus() != DeliveryStatus.CONFIRMED){
            throw new DeliveryCannotBeShippedException(id);
        }

        delivery.ship();
        return deliveryRepository.save(delivery);
    }

    @Override
    public Delivery deliver(Long id) {
        Delivery delivery = findById(id);
        if(delivery.getStatus() != DeliveryStatus.SHIPPED){
            throw new DeliveryCannotBeDeliveredException(id);
        }

        delivery.deliver();
        return deliveryRepository.save(delivery);
    }

    @Override
    public Delivery cancel(Long id) {
        Delivery delivery = findById(id);
        if(delivery.getStatus() != DeliveryStatus.PENDING){
            throw new DeliveryCannotBeCanceledException(id);
        }

        delivery.cancel();
        return deliveryRepository.save(delivery);
    }

    @Override
    public void deleteById(Long id) {
        deliveryRepository.deleteById(id);
    }
}
