package finki.ukim.mk.onlineclothingstore.service;
import finki.ukim.mk.onlineclothingstore.model.ShoppingCart;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.model.exceptions.ShoppingCartNotFoundException;
import finki.ukim.mk.onlineclothingstore.model.exceptions.UserNotFoundException;
import finki.ukim.mk.onlineclothingstore.repository.ShoppingCartRepository;
import finki.ukim.mk.onlineclothingstore.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {
    @Mock
    ShoppingCartRepository shoppingCartRepository;
    @Mock
    UserService userService;

    @InjectMocks
    ShoppingCartServiceImpl shoppingCartService;

    /* -------------------------------------------------
        findById(Long id)
    ------------------------------------------------- */

    @Test
    void SCS_1_findByIdWhenValidId(){
        // given
        Long id = 1L;
        ShoppingCart existing = new ShoppingCart(id, null);
        when(shoppingCartRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        ShoppingCart result = shoppingCartService.findById(id);

        // then
        assertSame(existing, result);
        verify(shoppingCartRepository).findById(id);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    void SCS_2_findByIdWhenInvalidId(){
        // given
        Long id = 1L;
        when(shoppingCartRepository.findById(id)).thenReturn(Optional.empty());

        // when + then
        assertThrows(ShoppingCartNotFoundException.class, () -> shoppingCartService.findById(id));
        verify(shoppingCartRepository).findById(id);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    /* -------------------------------------------------
        findOrCreate(String username)
    ------------------------------------------------- */

    @Test
    void SC_3_findOrCreateWhenValidUserAndExistingCart() {
        // given
        String username = "user";
        User user = new User(username, "", "", "", Role.ROLE_USER);

        ShoppingCart existingCart = new ShoppingCart(user);
        existingCart.setId(1L);

        when(userService.findByUsername(username)).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.of(existingCart));

        // when
        ShoppingCart result = shoppingCartService.findOrCreate(username);

        // then
        assertSame(existingCart, result);

        verify(userService).findByUsername(username);
        verify(shoppingCartRepository).findByUser(user);
        verify(shoppingCartRepository, never()).save(any(ShoppingCart.class));
        verifyNoMoreInteractions(shoppingCartRepository, userService);
    }

    @Test
    void SC_4_findOrCreateWhenValidUserNotExistingCart() {
        // given
        String username = "user";
        User user = new User(username, "", "", "", Role.ROLE_USER);

        when(userService.findByUsername(username)).thenReturn(user);
        when(shoppingCartRepository.findByUser(user)).thenReturn(Optional.empty());
        when(shoppingCartRepository.save(any(ShoppingCart.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ShoppingCart result = shoppingCartService.findOrCreate(username);

        // then
        assertNotNull(result);
        assertSame(user, result.getUser());
        verify(userService).findByUsername(username);
        verify(shoppingCartRepository).findByUser(user);
        verify(shoppingCartRepository).save(result);
        assertSame(user, result.getUser());
        verifyNoMoreInteractions(shoppingCartRepository, userService);
    }

    @Test
    void SC_5_findOrCreateWhenInvalidUser() {
        // given
        String username = "missing";
        when(userService.findByUsername(username))
                .thenThrow(new UserNotFoundException(username));

        // when + then
        assertThrows(UserNotFoundException.class,
                () -> shoppingCartService.findOrCreate(username));

        verify(userService).findByUsername(username);
        verifyNoInteractions(shoppingCartRepository);
        verifyNoMoreInteractions(userService);
    }

    /* -------------------------------------------------
        deleteById(Long id)
    ------------------------------------------------- */

    @Test
    void SC_6_deleteById_delegatesToRepository() {
        // given
        shoppingCartService.deleteById(1L);

        // when + then
        verify(shoppingCartRepository).deleteById(1L);
        verifyNoMoreInteractions(shoppingCartRepository);
        verifyNoInteractions(userService);
    }

}
