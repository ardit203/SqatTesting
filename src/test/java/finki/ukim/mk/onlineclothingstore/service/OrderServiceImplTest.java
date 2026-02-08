package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.dto.OrderDto;
import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.model.exceptions.PriceCannotBeLessThanZeroException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.UserNotFoundException;
import finki.ukim.mk.onlineclothingstore.repository.OrderRepository;
import finki.ukim.mk.onlineclothingstore.repository.OrderVariantRepository;
import finki.ukim.mk.onlineclothingstore.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Mock
    private OrderVariantRepository orderVariantRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private String username;
    private User user;

    private String address, email, phone, city, country, paymentMethod;

    @BeforeEach
    void setUp() {
        username = "user1";

        user = new User();
        user.setUsername(username);

        address = "Street 1";
        email = "u@u.com";
        phone = "070000000";
        city = "Skopje";
        country = "MK";
        paymentMethod = "CARD";
    }

    /* -------------------------------------------------
       findByUsername tests
       OS_1 - OS_2
     ------------------------------------------------- */

    @Test // OS_1
    void OS_1_findByUsernameWhenValidUsername() {
        when(userService.findByUsername(username)).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(List.of());

        List<Order> result = orderService.findByUsername(username);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userService).findByUsername(username);
        verify(orderRepository).findByUser(user);
        verifyNoMoreInteractions(userService, orderRepository);
        verifyNoInteractions(orderVariantRepository);
    }

    @Test // OS_2
    void OS_2_findByUsernameWhenInvalidUsername() {
        when(userService.findByUsername(username)).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () -> orderService.findByUsername(username));

        verify(userService).findByUsername(username);
        verify(orderRepository, never()).findByUser(any());
        verifyNoInteractions(orderVariantRepository);
    }

    /* -------------------------------------------------
       create tests
       OS_3 - OS_6
     ------------------------------------------------- */

    @Test // OS_3
    void OS_3_createWhenInvalidTotal() {
        assertThrows(PriceCannotBeLessThanZeroException.class, () ->
                orderService.create(username, address, email, phone, city, 1000, country, paymentMethod, -1.0)
        );

        verifyNoInteractions(userService);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(orderVariantRepository);
    }

    @Test // OS_4 + OS_6 (valid price + valid username)
    void OS_4_createWhenValidPriceAndUsername() {
        when(userService.findByUsername(username)).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order saved = orderService.create(username, address, email, phone, city, 1000, country, paymentMethod, 250.0);

        assertNotNull(saved);
        assertEquals(user, saved.getUser());
        assertEquals(250.0, saved.getTotal());
        assertNotNull(saved.getDelivery(), "Delivery should be created and set on the order");

        verify(userService).findByUsername(username);
        verify(orderRepository).save(any(Order.class));
        verifyNoInteractions(orderVariantRepository);
    }

    @Test // OS_5 (invalid username)
    void OS_5_createWhenInvalidUsername() {
        when(userService.findByUsername(username)).thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () ->
                orderService.create(username, address, email, phone, city, 1000, country, paymentMethod, 10.0)
        );

        verify(userService).findByUsername(username);
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(orderVariantRepository);
    }

    /* -------------------------------------------------
       toOrderDtos tests (All-dupaths)
     ------------------------------------------------- */

    @Test // Path: 1,2,7
    void OS_6_toOrderDtosWhenOrdersNull() {
        List<OrderDto> result = orderService.toOrderDtos(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(orderVariantRepository);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(userService);
    }

    @Test // Path: 1,2,3,4,8
    void OS_7_toOrderDtosWhenOrdersEmpty() {
        List<OrderDto> result = orderService.toOrderDtos(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(orderVariantRepository);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(userService);
    }

    @Test // Path: 1,2,3,4,5,6,4,5,6,4,8 (2 iterations)
    void OS_8_toOrderDtosWhenTwoOrders() {
        Order o1 = new Order(user, java.time.LocalDateTime.now(), 10.0,
                address, email, phone, city, 1000, country, paymentMethod);
        o1.setDelivery(new Delivery(o1));

        Order o2 = new Order(user, java.time.LocalDateTime.now(), 20.0,
                address, email, phone, city, 1000, country, paymentMethod);
        o2.setDelivery(new Delivery(o2));

        when(orderVariantRepository.findByOrder(o1)).thenReturn(List.of());
        when(orderVariantRepository.findByOrder(o2)).thenReturn(List.of());

        List<OrderDto> result = orderService.toOrderDtos(List.of(o1, o2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertNotNull(result.get(0));
        assertNotNull(result.get(1));

        verify(orderVariantRepository).findByOrder(o1);
        verify(orderVariantRepository).findByOrder(o2);
        verifyNoMoreInteractions(orderVariantRepository);
        verifyNoInteractions(orderRepository);
        verifyNoInteractions(userService);
    }
}
