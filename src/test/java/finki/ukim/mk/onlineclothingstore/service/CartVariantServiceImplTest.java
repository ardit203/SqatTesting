package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.dto.CartVariantDto;
import finki.ukim.mk.onlineclothingstore.model.CartVariant;
import finki.ukim.mk.onlineclothingstore.model.ShoppingCart;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.exceptions.*;
import finki.ukim.mk.onlineclothingstore.repository.CartVariantRepository;
import finki.ukim.mk.onlineclothingstore.service.impl.CartVariantServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartVariantServiceImplTest {

    @Mock private ShoppingCartService shoppingCartService;
    @Mock private CartVariantRepository cartVariantRepository;
    @Mock private VariantService variantService;
    @Mock private UserService userService;

    @InjectMocks
    private CartVariantServiceImpl cartVariantService;

    /* -------------------------------------------------
       createOrMerge tests (CVS_1 - CVS_6)
     ------------------------------------------------- */

    @Test // CVS_1
    void CSV_1_createOrMergeWhenInvalidQuantity() {
        assertThrows(NegativeQuantityException.class,
                () -> cartVariantService.createOrMerge(1L, 1L, 0));

        verifyNoInteractions(shoppingCartService, variantService, cartVariantRepository, userService);
    }

    @Test // CVS_2 (+ merges existing)
    void CSV_2_createOrMergeWhenValidQuantityExisting() {
        Long cartId = 1L;
        Long variantId = 2L;
        int addQty = 3;

        ShoppingCart cart = new ShoppingCart();
        Variant variant = new Variant();

        CartVariant existing = new CartVariant(cart, variant, 2);

        when(shoppingCartService.findById(cartId)).thenReturn(cart);
        when(variantService.findById(variantId)).thenReturn(variant);
        when(cartVariantRepository.findByCartAndVariant(cart, variant)).thenReturn(Optional.of(existing));
        when(cartVariantRepository.save(any(CartVariant.class))).thenAnswer(inv -> inv.getArgument(0));

        CartVariant saved = cartVariantService.createOrMerge(cartId, variantId, addQty);

        assertNotNull(saved);
        assertEquals(5, saved.getQuantity());

        verify(shoppingCartService).findById(cartId);
        verify(variantService).findById(variantId);
        verify(cartVariantRepository).findByCartAndVariant(cart, variant);
        verify(cartVariantRepository).save(existing);
        verifyNoMoreInteractions(shoppingCartService, variantService, cartVariantRepository);
        verifyNoInteractions(userService);
    }

    @Test // (still CVS_2)
    void CSV_2_createOrMergeWhenValidQuantityNewInsert() {
        Long cartId = 1L;
        Long variantId = 2L;
        int qty = 4;

        ShoppingCart cart = new ShoppingCart();
        Variant variant = new Variant();

        when(shoppingCartService.findById(cartId)).thenReturn(cart);
        when(variantService.findById(variantId)).thenReturn(variant);
        when(cartVariantRepository.findByCartAndVariant(cart, variant)).thenReturn(Optional.empty());
        when(cartVariantRepository.save(any(CartVariant.class))).thenAnswer(inv -> inv.getArgument(0));

        CartVariant saved = cartVariantService.createOrMerge(cartId, variantId, qty);

        assertNotNull(saved);
        assertEquals(qty, saved.getQuantity());
        assertSame(cart, saved.getCart());
        assertSame(variant, saved.getVariant());

        verify(shoppingCartService).findById(cartId);
        verify(variantService).findById(variantId);
        verify(cartVariantRepository).findByCartAndVariant(cart, variant);
        verify(cartVariantRepository).save(any(CartVariant.class));
        verifyNoInteractions(userService);
    }

    @Test // CVS_3
    void CSV_3_createOrMergeWhenInvalidCartId() {
        Long cartId = 999L;

        when(shoppingCartService.findById(cartId)).thenThrow(new ShoppingCartNotFoundException(cartId));

        assertThrows(ShoppingCartNotFoundException.class,
                () -> cartVariantService.createOrMerge(cartId, 1L, 1));

        verify(shoppingCartService).findById(cartId);
        verifyNoInteractions(variantService);
        verifyNoInteractions(cartVariantRepository);
        verifyNoInteractions(userService);
    }

    @Test // CVS_4 (valid cartId - interaction check)
    void CSV_4_createOrMergeWhenValidCarId() {
        Long cartId = 1L;
        Long variantId = 2L;

        ShoppingCart cart = new ShoppingCart();
        Variant variant = new Variant();

        when(shoppingCartService.findById(cartId)).thenReturn(cart);
        when(variantService.findById(variantId)).thenReturn(variant);
        when(cartVariantRepository.findByCartAndVariant(cart, variant)).thenReturn(Optional.empty());
        when(cartVariantRepository.save(any(CartVariant.class))).thenAnswer(inv -> inv.getArgument(0));

        cartVariantService.createOrMerge(cartId, variantId, 1);

        verify(shoppingCartService).findById(cartId);
    }

    @Test // CVS_5
    void CSV_5_createOrMergeWhenInvalidVariantId() {
        Long cartId = 1L;
        Long variantId = 999L;

        ShoppingCart cart = new ShoppingCart();

        when(shoppingCartService.findById(cartId)).thenReturn(cart);
        when(variantService.findById(variantId)).thenThrow(new VariantNotFoundException(variantId));

        assertThrows(VariantNotFoundException.class,
                () -> cartVariantService.createOrMerge(cartId, variantId, 1));

        verify(shoppingCartService).findById(cartId);
        verify(variantService).findById(variantId);
        verifyNoInteractions(cartVariantRepository);
        verifyNoInteractions(userService);
    }

    @Test // CVS_6 (valid variantId - interaction check)
    void CSV_6_createOrMergeWhenValidVariantId() {
        Long cartId = 1L;
        Long variantId = 2L;

        ShoppingCart cart = new ShoppingCart();
        Variant variant = new Variant();

        when(shoppingCartService.findById(cartId)).thenReturn(cart);
        when(variantService.findById(variantId)).thenReturn(variant);
        when(cartVariantRepository.findByCartAndVariant(cart, variant)).thenReturn(Optional.empty());
        when(cartVariantRepository.save(any(CartVariant.class))).thenAnswer(inv -> inv.getArgument(0));

        cartVariantService.createOrMerge(cartId, variantId, 1);

        verify(variantService).findById(variantId);
    }

    /* -------------------------------------------------
       update tests (CSV_7 - CSV_10)
     ------------------------------------------------- */

    @Test // CSV_7
    void CSV_7_updateWhenInvalidQuantity() {
        assertThrows(NegativeQuantityException.class,
                () -> cartVariantService.update(1L, -1));

        verifyNoInteractions(cartVariantRepository, shoppingCartService, variantService, userService);
    }

    @Test // CSV_8 + CSV_10 (valid qty + valid id)
    void CSV_8_updateWhenValidQuantityAndValidId() {
        Long id = 1L;
        int newQty = 7;

        CartVariant cartVariant = new CartVariant(new ShoppingCart(), new Variant(), 1);

        when(cartVariantRepository.findById(id)).thenReturn(Optional.of(cartVariant));
        when(cartVariantRepository.save(any(CartVariant.class))).thenAnswer(inv -> inv.getArgument(0));

        CartVariant saved = cartVariantService.update(id, newQty);

        assertNotNull(saved);
        assertEquals(newQty, saved.getQuantity());

        verify(cartVariantRepository).findById(id);
        verify(cartVariantRepository).save(cartVariant);
        verifyNoInteractions(shoppingCartService, variantService, userService);
    }

    @Test // CSV_9
    void CSV_9_updateWhenInvalidId() {
        Long id = 999L;

        when(cartVariantRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(CartVariantNotFoundException.class,
                () -> cartVariantService.update(id, 3));

        verify(cartVariantRepository).findById(id);
        verify(cartVariantRepository, never()).save(any());
        verifyNoInteractions(shoppingCartService, variantService, userService);
    }

    /* -------------------------------------------------
       toCartVariants tests (Prime Path Coverage)
       Provided test paths:
       1,2,6
       1,2,3,4,7
       1,2,3,4,5,4,7
     ------------------------------------------------- */

    @Test // Path: 1,2,6
    void CSV_10_toCartVariantsWhenNull() {
        List<CartVariant> result = cartVariantService.toCartVariants(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(cartVariantRepository, shoppingCartService, variantService, userService);
    }

    @Test // Path: 1,2,3,4,7
    void CSV_11_toCartVariantsWhenEmpty() {
        List<CartVariant> result = cartVariantService.toCartVariants(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(cartVariantRepository, shoppingCartService, variantService, userService);
    }

    @Test // Path: 1,2,3,4,5,4,7  (size=1)
    void CSV_12_toCartVariantsWhenNonEmpty() {
        Long id = 55L;

        CartVariant cartVariant = new CartVariant(new ShoppingCart(), new Variant(), 2);

        when(cartVariantRepository.findById(id)).thenReturn(Optional.of(cartVariant));

        CartVariantDto dto = dtoWithCartVariantId(id);

        List<CartVariant> result = cartVariantService.toCartVariants(List.of(dto));

        assertNotNull(result);
        assertEquals(1, result.size());
        assertSame(cartVariant, result.get(0));

        verify(cartVariantRepository).findById(id);
        verifyNoMoreInteractions(cartVariantRepository);
        verifyNoInteractions(shoppingCartService, variantService, userService);
    }

    /* -------------------------------------------------
       Helper
     ------------------------------------------------- */
    private CartVariantDto dtoWithCartVariantId(Long id) {
        try {
            Class<CartVariantDto> clazz = CartVariantDto.class;

            if (clazz.isRecord()) {
                var components = clazz.getRecordComponents();
                Object[] args = new Object[components.length];
                Class<?>[] paramTypes = new Class<?>[components.length];

                for (int i = 0; i < components.length; i++) {
                    var rc = components[i];
                    paramTypes[i] = rc.getType();

                    if ("cartVariantId".equals(rc.getName())) {
                        args[i] = id;
                    } else {
                        args[i] = defaultValueFor(paramTypes[i]);
                    }
                }

                Constructor<CartVariantDto> ctor = clazz.getDeclaredConstructor(paramTypes);
                ctor.setAccessible(true);
                return ctor.newInstance(args);
            }

            Constructor<?> ctor = clazz.getDeclaredConstructors()[0];
            ctor.setAccessible(true);
            Class<?>[] types = ctor.getParameterTypes();
            Object[] args = new Object[types.length];
            boolean idSet = false;

            for (int i = 0; i < types.length; i++) {
                if (!idSet && (types[i] == Long.class || types[i] == long.class)) {
                    args[i] = id;
                    idSet = true;
                } else {
                    args[i] = defaultValueFor(types[i]);
                }
            }
            return (CartVariantDto) ctor.newInstance(args);

        } catch (Exception e) {
            fail("Failed to construct CartVariantDto. Paste CartVariantDto definition so the test can be adjusted. Error: " + e);
            return null;
        }
    }

    private Object defaultValueFor(Class<?> type) {
        if (type == String.class) return "";
        if (type == Integer.class || type == int.class) return 0;
        if (type == Long.class || type == long.class) return 0L;
        if (type == Double.class || type == double.class) return 0.0;
        if (type == Boolean.class || type == boolean.class) return false;

        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return (constants != null && constants.length > 0) ? constants[0] : null;
        }
        return null;
    }
}
