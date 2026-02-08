package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.model.exceptions.*;
import finki.ukim.mk.onlineclothingstore.repository.VariantRepository;
import finki.ukim.mk.onlineclothingstore.service.ProductService;
import finki.ukim.mk.onlineclothingstore.service.impl.VariantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VariantServiceImplTest {

    @Mock
    private VariantRepository variantRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private VariantServiceImpl variantService;

    private Long productId;
    private Long invalidProductId;
    private Long variantId;
    private Long invalidVariantId;

    private Product product;
    private Variant variant;

    @BeforeEach
    void setUp() {
        productId = 1L;
        invalidProductId = 999L;

        variantId = 10L;
        invalidVariantId = 888L;

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");

        variant = new Variant(product, Size.M, 5);
        variant.setId(variantId);
    }

    /* -------------------------------------------------
       findByProductId tests
       VS_1 - VS_4
     ------------------------------------------------- */

    @Test // VS_1
    void VS_1_findByProductIdWhenNoVariantsInDb() {
        when(productService.findById(productId)).thenReturn(product);
        when(variantRepository.findByProduct(product)).thenReturn(List.of());

        List<Variant> result = variantService.findByProductId(productId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productService).findById(productId);
        verify(variantRepository).findByProduct(product);
        verifyNoMoreInteractions(productService, variantRepository);
    }

    @Test // VS_2
    void VS_2_findByProductIdWhenVariantsExist() {
        Variant v1 = new Variant(product, Size.S, 3); v1.setId(1L);
        Variant v2 = new Variant(product, Size.M, 7); v2.setId(2L);

        when(productService.findById(productId)).thenReturn(product);
        when(variantRepository.findByProduct(product)).thenReturn(List.of(v1, v2));

        List<Variant> result = variantService.findByProductId(productId);

        assertEquals(2, result.size());
        assertEquals(List.of(v1, v2), result);
        verify(productService).findById(productId);
        verify(variantRepository).findByProduct(product);
    }

    @Test // VS_3
    void VS_3_findByProductIdWhenValidProductId() {
        when(productService.findById(productId)).thenReturn(product);
        when(variantRepository.findByProduct(product)).thenReturn(List.of(variant));

        variantService.findByProductId(productId);

        verify(productService).findById(productId);
        verify(variantRepository).findByProduct(product);
    }

    @Test // VS_4 (actual behavior: throws from ProductService)
    void VS_4_findByProductIdWhenInvalidProductId() {
        when(productService.findById(invalidProductId))
                .thenThrow(new ProductNotFoundException(invalidProductId));

        assertThrows(ProductNotFoundException.class, () -> variantService.findByProductId(invalidProductId));

        verify(productService).findById(invalidProductId);
        verifyNoInteractions(variantRepository);
    }

    /* -------------------------------------------------
       findById tests
       VS_5 - VS_6
     ------------------------------------------------- */

    @Test // VS_5
    void VS_5_findByIdWhenValidId() {
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));

        Variant result = variantService.findById(variantId);

        assertNotNull(result);
        assertEquals(variantId, result.getId());
        verify(variantRepository).findById(variantId);
        verifyNoMoreInteractions(variantRepository);
        verifyNoInteractions(productService);
    }

    @Test // VS_6
    void VS_6_findByIdWhenInvalidId() {
        when(variantRepository.findById(invalidVariantId)).thenReturn(Optional.empty());

        assertThrows(VariantNotFoundException.class, () -> variantService.findById(invalidVariantId));

        verify(variantRepository).findById(invalidVariantId);
        verifyNoInteractions(productService);
    }

    /* -------------------------------------------------
       create tests
       VS_7 - VS_10
     ------------------------------------------------- */

    @Test // VS_7 (0 is VALID in code)
    void VS_7_createWhenStockIsZero() {
        when(productService.findById(productId)).thenReturn(product);
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        Variant created = variantService.create(Size.L, 0, productId);

        assertNotNull(created);
        assertEquals(product, created.getProduct());
        assertEquals(Size.L, created.getSize());
        assertEquals(0, created.getStock());

        verify(productService).findById(productId);
        verify(variantRepository).save(any(Variant.class));
    }

    @Test // VS_8
    void VS_8_createWhenStockNegative() {
        assertThrows(InvalidStockValueException.class, () ->
                variantService.create(Size.M, -1, productId)
        );

        verifyNoInteractions(productService);
        verifyNoInteractions(variantRepository);
    }

    @Test // VS_9
    void VS_9_createWhenValidProductId() {
        when(productService.findById(productId)).thenReturn(product);
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        variantService.create(Size.S, 5, productId);

        verify(productService).findById(productId);
        verify(variantRepository).save(any(Variant.class));
    }

    @Test // VS_10
    void VS_10_createWhenInvalidProductId() {
        when(productService.findById(invalidProductId))
                .thenThrow(new ProductNotFoundException(invalidProductId));

        assertThrows(ProductNotFoundException.class, () ->
                variantService.create(Size.S, 5, invalidProductId)
        );

        verify(productService).findById(invalidProductId);
        verify(variantRepository, never()).save(any());
    }

    /* -------------------------------------------------
       update tests
       VS_11 - VS_16
     ------------------------------------------------- */

    @Test // VS_11
    void VS_11_updateWhenStockNegative() {
        assertThrows(InvalidStockValueException.class, () ->
                variantService.update(variantId, Size.M, -1, productId)
        );

        verifyNoInteractions(productService);
        verifyNoInteractions(variantRepository);
    }

    @Test // VS_12
    void VS_12_updateWhenStockValid() {
        when(productService.findById(productId)).thenReturn(product);
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        Variant updated = variantService.update(variantId, Size.S, 12, productId);

        assertEquals(Size.S, updated.getSize());
        assertEquals(12, updated.getStock());
        assertEquals(product, updated.getProduct());

        verify(productService).findById(productId);
        verify(variantRepository).findById(variantId);
        verify(variantRepository).save(variant);
    }

    @Test // VS_13
    void VS_13_updateWhenValidProductId() {
        when(productService.findById(productId)).thenReturn(product);
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        variantService.update(variantId, Size.M, 5, productId);

        verify(productService).findById(productId);
    }

    @Test // VS_14
    void VS_14_updateWhenInvalidProductId() {
        when(productService.findById(invalidProductId))
                .thenThrow(new ProductNotFoundException(invalidProductId));

        assertThrows(ProductNotFoundException.class, () ->
                variantService.update(variantId, Size.M, 5, invalidProductId)
        );

        verify(productService).findById(invalidProductId);
        verify(variantRepository, never()).findById(any());
        verify(variantRepository, never()).save(any());
    }

    @Test // VS_15
    void VS_15_updateWhenValidId() {
        when(productService.findById(productId)).thenReturn(product);
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        variantService.update(variantId, Size.M, 6, productId);

        verify(variantRepository).findById(variantId);
        verify(variantRepository).save(variant);
    }

    @Test // VS_16
    void VS_16_updateWhenInvalidId() {
        when(productService.findById(productId)).thenReturn(product);
        when(variantRepository.findById(invalidVariantId)).thenReturn(Optional.empty());

        assertThrows(VariantNotFoundException.class, () ->
                variantService.update(invalidVariantId, Size.M, 6, productId)
        );

        verify(productService).findById(productId);
        verify(variantRepository).findById(invalidVariantId);
        verify(variantRepository, never()).save(any());
    }

    /* -------------------------------------------------
       deleteById tests
       VS_17
     ------------------------------------------------- */

    @Test // VS_17
    void VS_17_deleteById() {
        doNothing().when(variantRepository).deleteById(variantId);

        variantService.deleteById(variantId);

        verify(variantRepository, times(1)).deleteById(variantId);
        verifyNoMoreInteractions(variantRepository);
        verifyNoInteractions(productService);
    }

    /* -------------------------------------------------
       addToStock tests
       VS_18 - VS_21
     ------------------------------------------------- */

    @Test // VS_18
    void VS_18_addToStockWhenValidQuantity() {
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        Variant updated = variantService.addToStock(variantId, 3);

        assertEquals(8, updated.getStock());
        verify(variantRepository).findById(variantId);
        verify(variantRepository).save(variant);
        verifyNoInteractions(productService);
    }

    @Test // VS_19
    void VS_19_addToStockWhenInvalidQuantity() {
        assertThrows(NegativeQuantityException.class, () ->
                variantService.addToStock(variantId, 0)
        );

        verifyNoInteractions(variantRepository);
        verifyNoInteractions(productService);
    }

    @Test // VS_20
    void VS_20_addToStockWhenValidId() {
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        variantService.addToStock(variantId, 1);

        verify(variantRepository).findById(variantId);
        verify(variantRepository).save(any(Variant.class));
    }

    @Test // VS_21
    void VS_21_addToStockWhenInvalidId() {
        when(variantRepository.findById(invalidVariantId)).thenReturn(Optional.empty());

        assertThrows(VariantNotFoundException.class, () ->
                variantService.addToStock(invalidVariantId, 2)
        );

        verify(variantRepository).findById(invalidVariantId);
        verify(variantRepository, never()).save(any());
    }

    /* -------------------------------------------------
       removeFromStock tests
       VS_22 - VS_27
     ------------------------------------------------- */

    @Test // VS_22
    void VS_22_removeFromStockWhenValidQuantity() {
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        Variant updated = variantService.removeFromStock(variantId, 2);

        assertEquals(3, updated.getStock());
        verify(variantRepository).findById(variantId);
        verify(variantRepository).save(variant);
        verifyNoInteractions(productService);
    }

    @Test // VS_23
    void VS_23_removeFromStockWhenInvalidQuantity() {
        assertThrows(NegativeQuantityException.class, () ->
                variantService.removeFromStock(variantId, -1)
        );

        verifyNoInteractions(variantRepository);
        verifyNoInteractions(productService);
    }

    @Test // VS_24
    void VS_24_removeFromStockWhenValidId() {
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        variantService.removeFromStock(variantId, 1);

        verify(variantRepository).findById(variantId);
        verify(variantRepository).save(any(Variant.class));
    }

    @Test // VS_25
    void VS_25_removeFromStockWhenInvalidId() {
        when(variantRepository.findById(invalidVariantId)).thenReturn(Optional.empty());

        assertThrows(VariantNotFoundException.class, () ->
                variantService.removeFromStock(invalidVariantId, 1)
        );

        verify(variantRepository).findById(invalidVariantId);
        verify(variantRepository, never()).save(any());
    }

    @Test // VS_26
    void VS_26_removeFromStockWhenInsufficientStock() {
        Variant lowStock = new Variant(product, Size.S, 1);
        lowStock.setId(variantId);

        when(variantRepository.findById(variantId)).thenReturn(Optional.of(lowStock));

        assertThrows(VariantOutOfStockException.class, () ->
                variantService.removeFromStock(variantId, 2)
        );

        verify(variantRepository).findById(variantId);
        verify(variantRepository, never()).save(any());
        verifyNoInteractions(productService);
    }

    @Test // VS_27
    void VS_27_removeFromStockWhenSufficientStock() {
        when(variantRepository.findById(variantId)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenAnswer(inv -> inv.getArgument(0));

        variantService.removeFromStock(variantId, 5);

        assertEquals(0, variant.getStock());
        verify(variantRepository).save(variant);
    }

    /* -------------------------------------------------
       getVariantsForProducts tests (null / empty / non-empty)
     ------------------------------------------------- */

    @Test //VS_28
    void VS_28_getVariantsForProductsWhenNull() {
        List<Variant> result = variantService.getVariantsForProducts(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(variantRepository);
        verifyNoInteractions(productService);
    }

    @Test
    void VS_29_getVariantsForProductsWhenEmptyList() {
        List<Variant> result = variantService.getVariantsForProducts(List.of());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(variantRepository);
        verifyNoInteractions(productService);
    }

    @Test
    void VS_30_getVariantsForProductsWhenNonEmpty() {
        Product p1 = new Product(); p1.setId(1L); p1.setName("P1");
        Product p2 = new Product(); p2.setId(2L); p2.setName("P2");

        Variant v1 = new Variant(p1, Size.S, 1); v1.setId(101L);
        Variant v2 = new Variant(p1, Size.M, 2); v2.setId(102L);
        Variant v3 = new Variant(p2, Size.L, 3); v3.setId(201L);

        when(variantRepository.findByProduct(p1)).thenReturn(List.of(v1, v2));
        when(variantRepository.findByProduct(p2)).thenReturn(List.of(v3));

        List<Variant> result = variantService.getVariantsForProducts(List.of(p1, p2));

        assertEquals(3, result.size());
        assertEquals(List.of(v1, v2, v3), result);

        verify(variantRepository).findByProduct(p1);
        verify(variantRepository).findByProduct(p2);
        verifyNoInteractions(productService);
    }
}
