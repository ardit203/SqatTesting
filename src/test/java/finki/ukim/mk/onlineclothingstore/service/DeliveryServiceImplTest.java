package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.Delivery;
import finki.ukim.mk.onlineclothingstore.model.enums.DeliveryStatus;
import finki.ukim.mk.onlineclothingstore.model.exceptions.*;
import finki.ukim.mk.onlineclothingstore.repository.DeliveryRepository;
import finki.ukim.mk.onlineclothingstore.repository.OrderRepository;
import finki.ukim.mk.onlineclothingstore.service.impl.DeliveryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    private Delivery mockDeliveryWithStatus(DeliveryStatus status) {
        Delivery delivery = mock(Delivery.class);
        when(delivery.getStatus()).thenReturn(status);
        return delivery;
    }

    /* -------------------------------------------------
       confirm tests (DS_1 - DS_4)
     ------------------------------------------------- */

    @Test // DS_1 + DS_3 (valid id + valid status)
    void DS_1_confirmWhenValidIdAndPending() {
        Long id = 1L;
        Delivery delivery = mockDeliveryWithStatus(DeliveryStatus.PENDING);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(delivery)).thenReturn(delivery);

        Delivery result = deliveryService.confirm(id);

        assertNotNull(result);
        verify(deliveryRepository).findById(id);
        verify(delivery).confirm();
        verify(deliveryRepository).save(delivery);
        verifyNoMoreInteractions(deliveryRepository, delivery);
        verifyNoInteractions(orderRepository);
    }

    @Test // DS_2 (invalid id)
    void DS_2_confirmWhenInvalidId() {
        Long id = 999L;
        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.confirm(id));

        verify(deliveryRepository).findById(id);
        verify(deliveryRepository, never()).save(any());
        verifyNoInteractions(orderRepository);
    }

    @Test // DS_4 (invalid status)
    void DS_4_confirmWhenInvalidStatus() {
        Long id = 2L;
        Delivery delivery = mockDeliveryWithStatus(DeliveryStatus.CONFIRMED);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        assertThrows(DeliveryCannotBeConfirmedException.class, () -> deliveryService.confirm(id));

        verify(deliveryRepository).findById(id);
        verify(delivery, never()).confirm();
        verify(deliveryRepository, never()).save(any());
        verifyNoInteractions(orderRepository);
    }

    /* -------------------------------------------------
       ship tests (DS_5 - DS_8)
     ------------------------------------------------- */

    @Test // DS_5 + DS_7 (valid id + valid status)
    void DS_5_shipWhenValidIdAndStatus() {
        Long id = 3L;
        Delivery delivery = mockDeliveryWithStatus(DeliveryStatus.CONFIRMED);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(delivery)).thenReturn(delivery);

        Delivery result = deliveryService.ship(id);

        assertNotNull(result);
        verify(deliveryRepository).findById(id);
        verify(delivery).ship();
        verify(deliveryRepository).save(delivery);
        verifyNoInteractions(orderRepository);
    }

    @Test // DS_6 (invalid id)
    void DS_6_shipWhenInvalidId() {
        Long id = 1000L;
        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.ship(id));

        verify(deliveryRepository).findById(id);
        verify(deliveryRepository, never()).save(any());
        verifyNoInteractions(orderRepository);
    }

    @Test // DS_8 (invalid status)
    void DS_8_shipWhenInvalidStatus() {
        Long id = 4L;
        Delivery delivery = mockDeliveryWithStatus(DeliveryStatus.PENDING);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        assertThrows(DeliveryCannotBeShippedException.class, () -> deliveryService.ship(id));

        verify(deliveryRepository).findById(id);
        verify(delivery, never()).ship();
        verify(deliveryRepository, never()).save(any());
        verifyNoInteractions(orderRepository);
    }

    /* -------------------------------------------------
       deliver tests (DS_9 - DS_12)
     ------------------------------------------------- */

    @Test // DS_9 + DS_11 (valid id + valid status)
    void DS_9_deliverWhenValidIdAndStatus() {
        Long id = 5L;
        Delivery delivery = mockDeliveryWithStatus(DeliveryStatus.SHIPPED);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(delivery)).thenReturn(delivery);

        Delivery result = deliveryService.deliver(id);

        assertNotNull(result);
        verify(deliveryRepository).findById(id);
        verify(delivery).deliver();
        verify(deliveryRepository).save(delivery);
        verifyNoInteractions(orderRepository);
    }

    @Test // DS_10 (invalid id)
    void DS_10_deliverWhenInvalidId() {
        Long id = 2000L;
        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.deliver(id));

        verify(deliveryRepository).findById(id);
        verify(deliveryRepository, never()).save(any());
        verifyNoInteractions(orderRepository);
    }

    @Test // DS_12 (invalid status)
    void DS_12_shipWhenInvalidStatus() {
        Long id = 6L;
        Delivery delivery = mockDeliveryWithStatus(DeliveryStatus.CONFIRMED);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        assertThrows(DeliveryCannotBeDeliveredException.class, () -> deliveryService.deliver(id));

        verify(deliveryRepository).findById(id);
        verify(delivery, never()).deliver();
        verify(deliveryRepository, never()).save(any());
        verifyNoInteractions(orderRepository);
    }

    /* -------------------------------------------------
       cancel tests (DS_13 - DS_16)
     ------------------------------------------------- */

    @Test // DS_13 + DS_15 (valid id + valid status)
    void DS_13_cancelWhenValidIdAndValidStaus() {
        Long id = 7L;
        Delivery delivery = mockDeliveryWithStatus(DeliveryStatus.PENDING);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));
        when(deliveryRepository.save(delivery)).thenReturn(delivery);

        Delivery result = deliveryService.cancel(id);

        assertNotNull(result);
        verify(deliveryRepository).findById(id);
        verify(delivery).cancel();
        verify(deliveryRepository).save(delivery);
        verifyNoInteractions(orderRepository);
    }

    @Test // DS_14 (invalid id)
    void DS_14_cancelWhenInvalidId() {
        Long id = 3000L;
        when(deliveryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(DeliveryNotFoundException.class, () -> deliveryService.cancel(id));

        verify(deliveryRepository).findById(id);
        verify(deliveryRepository, never()).save(any());
        verifyNoInteractions(orderRepository);
    }

    @Test // DS_16 (invalid status)
    void DS_16_cancelWhenInvalidStaus() {
        Long id = 8L;
        Delivery delivery = mockDeliveryWithStatus(DeliveryStatus.SHIPPED);

        when(deliveryRepository.findById(id)).thenReturn(Optional.of(delivery));

        assertThrows(DeliveryCannotBeCanceledException.class, () -> deliveryService.cancel(id));

        verify(deliveryRepository).findById(id);
        verify(delivery, never()).cancel();
        verify(deliveryRepository, never()).save(any());
        verifyNoInteractions(orderRepository);
    }
}
