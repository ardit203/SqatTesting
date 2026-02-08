package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.model.exceptions.*;
import finki.ukim.mk.onlineclothingstore.repository.OrderVariantRepository;
import finki.ukim.mk.onlineclothingstore.service.CartVariantService;
import finki.ukim.mk.onlineclothingstore.service.OrderService;
import finki.ukim.mk.onlineclothingstore.service.VariantService;
import finki.ukim.mk.onlineclothingstore.service.impl.OrderVariantServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderVariantServiceImplTest {

    @Mock private OrderService orderService;
    @Mock private VariantService variantService;
    @Mock private OrderVariantRepository orderVariantRepository;
    @Mock private CartVariantService cartVariantService;

    @InjectMocks
    private OrderVariantServiceImpl orderVariantService;

    /* -------------------------------------------------
       create
     ------------------------------------------------- */

    @Test // OSV_1
    void OSV_1_createInvalidQuantity() throws Exception {
        assertThrows(NegativeQuantityException.class, () ->
                invokeCreate(1L, 2L, 0)
        );

        verifyNoInteractions(orderService, variantService, orderVariantRepository, cartVariantService);
    }

    @Test // OSV_2 + OSV_4 + OSV_6 (valid quantity + valid orderId + valid variantId)
    void OSV_2_createWhenValidQuantityValidOrderIdAndValidVariantId() throws Exception {
        Long orderId = 1L;
        Long variantId = 2L;
        int qty = 3;

        Order order = new Order();
        order.setId(orderId);

        Variant variant = new Variant();
        variant.setId(variantId);

        when(orderService.findById(orderId)).thenReturn(order);
        when(variantService.findById(variantId)).thenReturn(variant);
        when(orderVariantRepository.save(any(OrderVariant.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderVariant saved = invokeCreate(orderId, variantId, qty);

        assertNotNull(saved);
        assertEquals(order, saved.getOrder());
        assertEquals(variant, saved.getVariant());
        assertEquals(qty, saved.getQuantity());

        verify(orderService).findById(orderId);
        verify(variantService).findById(variantId);
        verify(orderVariantRepository).save(any(OrderVariant.class));
        verifyNoInteractions(cartVariantService);
    }

    @Test // OSV_3
    void OSV_3_createWhenInvalidOrderId() throws Exception {
        Long orderId = 999L;

        when(orderService.findById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        assertThrows(OrderNotFoundException.class, () ->
                invokeCreate(orderId, 2L, 1)
        );

        verify(orderService).findById(orderId);
        verifyNoInteractions(variantService);
        verify(orderVariantRepository, never()).save(any());
        verifyNoInteractions(cartVariantService);
    }

    @Test // OSV_5
    void OSV_5_createWhenInvalidVariantId() throws Exception {
        Long orderId = 1L;
        Long variantId = 999L;

        Order order = new Order();
        order.setId(orderId);

        when(orderService.findById(orderId)).thenReturn(order);
        when(variantService.findById(variantId)).thenThrow(new VariantNotFoundException(variantId));

        assertThrows(VariantNotFoundException.class, () ->
                invokeCreate(orderId, variantId, 1)
        );

        verify(orderService).findById(orderId);
        verify(variantService).findById(variantId);
        verify(orderVariantRepository, never()).save(any());
        verifyNoInteractions(cartVariantService);
    }

    /* -------------------------------------------------
       addVariantsToOrder
     ------------------------------------------------- */

    @Test // OSV_7
    void OSV_7_addVariantsToOrderWhenCartVariantsIsNull() {
        List<OrderVariant> result = orderVariantService.addVariantsToOrder(1L, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(orderService, variantService, orderVariantRepository, cartVariantService);
    }

    @Test // OSV_8
    void OSV_8_addVariantsToOrderWhenCartVariantsIsEmpty() {
        List<OrderVariant> result = orderVariantService.addVariantsToOrder(1L, List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verifyNoInteractions(orderService, variantService, orderVariantRepository, cartVariantService);
    }

    @Test // OSV_10
    void OSV_10_addVariantsToOrderWhenInvalidOrderId() {
        Long orderId = 999L;
        CartVariant cv = mock(CartVariant.class);

        when(orderService.findById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        assertThrows(OrderNotFoundException.class, () ->
                orderVariantService.addVariantsToOrder(orderId, List.of(cv))
        );

        verify(orderService).findById(orderId);
        verifyNoInteractions(cartVariantService);
        verifyNoInteractions(variantService);
        verifyNoInteractions(orderVariantRepository);
    }

    @Test // OSV_9 + OSV_11 (non-empty cartVariants + valid orderId)
    void OSV_9_addVariantsToOrderWhenNonEmptyAndValidOrderId() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);


        CartVariant cv1 = mock(CartVariant.class);
        Variant v1 = new Variant(); v1.setId(11L);
        when(cv1.getId()).thenReturn(101L);
        when(cv1.getQuantity()).thenReturn(2);
        when(cv1.getVariant()).thenReturn(v1);


        CartVariant cv2 = mock(CartVariant.class);
        Variant v2 = new Variant(); v2.setId(22L);
        when(cv2.getId()).thenReturn(202L);
        when(cv2.getQuantity()).thenReturn(1);
        when(cv2.getVariant()).thenReturn(v2);

        when(orderService.findById(orderId)).thenReturn(order);

        doNothing().when(cartVariantService).checkForSufficientStocks(anyList());

        when(orderVariantRepository.save(any(OrderVariant.class))).thenAnswer(inv -> inv.getArgument(0));

        List<OrderVariant> result = orderVariantService.addVariantsToOrder(orderId, List.of(cv1, cv2));

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getQuantity());
        assertEquals(1, result.get(1).getQuantity());


        verify(orderService, times(3)).findById(orderId);
        verify(cartVariantService).checkForSufficientStocks(List.of(cv1, cv2));

        verify(variantService).removeFromStock(11L, 2);
        verify(variantService).removeFromStock(22L, 1);

        verify(cartVariantService).deleteById(101L);
        verify(cartVariantService).deleteById(202L);

        verify(orderService, times(3)).findById(orderId);

        verify(variantService).findById(11L);
        verify(variantService).findById(22L);

        verify(orderVariantRepository, times(2)).save(any(OrderVariant.class));
    }

    /* -------------------------------------------------
       addVariantsToStockAfterOrderCancellation
     ------------------------------------------------- */

    @Test // OSV_12
    void OSV_12_addVariantsToStockAfterOrderCancellationWhenInvalidOrderId() {
        Long orderId = 999L;
        when(orderService.findById(orderId)).thenThrow(new OrderNotFoundException(orderId));

        assertThrows(OrderNotFoundException.class, () ->
                orderVariantService.addVariantsToStockAfterOrderCancellation(orderId)
        );

        verify(orderService).findById(orderId);
        verifyNoInteractions(orderVariantRepository);
        verifyNoInteractions(cartVariantService);
        verifyNoInteractions(variantService);
    }

    @Test // OSV_13
    void OSV_13_addVariantsToStockAfterOrderCancellationWhenValidOrderId() {
        Long orderId = 1L;

        Order order = new Order();
        order.setId(orderId);

        OrderVariant ov1 = new OrderVariant();
        Variant v1 = new Variant(); v1.setId(11L);
        ov1.setVariant(v1);
        ov1.setQuantity(2);

        OrderVariant ov2 = new OrderVariant();
        Variant v2 = new Variant(); v2.setId(22L);
        ov2.setVariant(v2);
        ov2.setQuantity(1);

        when(orderService.findById(orderId)).thenReturn(order);
        when(orderVariantRepository.findByOrder(order)).thenReturn(List.of(ov1, ov2));

        orderVariantService.addVariantsToStockAfterOrderCancellation(orderId);

        verify(orderService).findById(orderId);
        verify(orderVariantRepository).findByOrder(order);
        verify(variantService).addToStock(11L, 2);
        verify(variantService).addToStock(22L, 1);
        verifyNoInteractions(cartVariantService);
    }

    /* -------------------------------------------------
       helper
     ------------------------------------------------- */
    private OrderVariant invokeCreate(Long orderId, Long variantId, Integer quantity) throws Exception {
        Method m = OrderVariantServiceImpl.class.getDeclaredMethod("create", Long.class, Long.class, Integer.class);
        m.setAccessible(true);
        try {
            return (OrderVariant) m.invoke(orderVariantService, orderId, variantId, quantity);
        } catch (java.lang.reflect.InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException re) throw re;
            throw e;
        }
    }
}
