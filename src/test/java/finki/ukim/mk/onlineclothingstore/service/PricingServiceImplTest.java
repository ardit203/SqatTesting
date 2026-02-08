package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.*;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.model.exceptions.UserNotFoundException;
import finki.ukim.mk.onlineclothingstore.service.impl.PricingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PricingServiceImplTest {
    @Mock
    ShoppingCartService shoppingCartService;
    @Mock
    CartVariantService cartVariantService;

    @InjectMocks
    PricingServiceImpl pricingService;

    /* -------------------------------------------------
        calculateTotal(List<CartVariant> cartVariants)
    ------------------------------------------------- */

    @Test
    void PS_1_calculateTotalWhenCartVariantsIsNull(){
        assertEquals(0.0, pricingService.calculateTotal((List<CartVariant>) null));
    }

    @Test
    void PS_2_calculateTotalWhenCartVariantsIsEmpty(){
        assertEquals(0.0, pricingService.calculateTotal(List.of()));
    }

    @Test
    void PS_3_calculateTotalWhenCartVariantsIsNotNullAndNotEmpty(){
        Category category = new Category("Tops", "All tops");
        Product tshirt = new Product("T-Shirt", "basic", 10.0, Department.MEN, category);
        Product hoodie = new Product("Hoodie", "warm", 25.5, Department.MEN, category);

        Variant v1 = new Variant(tshirt, Size.M, 100);
        Variant v2 = new Variant(hoodie, Size.L, 50);

        CartVariant cv1 = new CartVariant(null, v1, 4);
        CartVariant cv2 = new CartVariant(null, v1, 2);
        CartVariant cv3 = new CartVariant(null, v2, 1);

        Double total = pricingService.calculateTotal(List.of(cv1, cv2, cv3));

        assertEquals(85.5, total);
    }

    /* -------------------------------------------------
        calculateTotal(String username)
    ------------------------------------------------- */

    @Test
    void PS_4_calculateTotalWhenInvalidUsername(){
        //given
        String username = "user";
        when(shoppingCartService.findOrCreate(username)).thenThrow(new UserNotFoundException(username));

        //when
        assertThrows(UserNotFoundException.class, () -> pricingService.calculateTotal(username));
        verify(shoppingCartService).findOrCreate(username);
        verifyNoInteractions(cartVariantService);
        verifyNoMoreInteractions(shoppingCartService);
    }


    @Test
    void PS_5_calculateTotalValidUsername() {
        // given
        String username = "user";

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);

        Category category = new Category("Shoes", "All shoes");
        Product sneakers = new Product("Sneakers", "sport", 50.0, Department.MEN, category);
        Variant v1 = new Variant(sneakers, Size.XL, 10);
        Variant v2 = new Variant(sneakers, Size.L, 10);

        List<CartVariant> cartVariants = List.of(
                new CartVariant(cart, v1, 1),
                new CartVariant(cart, v2, 3)
        );

        when(shoppingCartService.findOrCreate(username)).thenReturn(cart);
        when(cartVariantService.findByCartId(1L)).thenReturn(cartVariants);

        // when
        Double total = pricingService.calculateTotal(username);

        // then
        assertEquals(200.0, total);

        verify(shoppingCartService).findOrCreate(username);
        verify(cartVariantService).findByCartId(1L);
        verifyNoMoreInteractions(shoppingCartService, cartVariantService);
    }

}
