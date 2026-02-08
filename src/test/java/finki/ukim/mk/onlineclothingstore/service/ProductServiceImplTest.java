package finki.ukim.mk.onlineclothingstore.service;

import finki.ukim.mk.onlineclothingstore.model.Category;
import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.exceptions.*;
import finki.ukim.mk.onlineclothingstore.repository.ProductRepository;
import finki.ukim.mk.onlineclothingstore.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    private Long productId;
    private Long categoryId;
    private String name;
    private Double price;
    private String description;
    private Department department;
    private String image;

    private Category category;
    private Product existingProduct;

    @BeforeEach
    void setUp() {
        productId = 1L;
        categoryId = 10L;
        name = "T-Shirt";
        price = 49.99;
        description = "Basic cotton shirt";
        department = Department.MEN; // <-- change if your enum differs
        image = "https://example.com/img.png";

        category = new Category();
        // if you have setters/constructors, set id/name as you want

        existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old name");
        existingProduct.setPrice(10.0);
        existingProduct.setDescription("Old desc");
        existingProduct.setDepartment(department);
        existingProduct.setCategory(category);
        existingProduct.setImage("old.png");
    }

    /* -------------------------------------------------
       findAll tests
     ------------------------------------------------- */

    @Test // PS_1
    void PS_1_findAllWhenNoRecords() {
        when(productRepository.findAll()).thenReturn(List.of());

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(categoryService);
    }

    @Test // PS_2
    void PS_2_findAllWhenRecords() {
        Product p1 = new Product(); p1.setId(1L);
        Product p2 = new Product(); p2.setId(2L);

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Product> result = productService.findAll();

        assertEquals(2, result.size());
        assertEquals(List.of(p1, p2), result);
        verify(productRepository, times(1)).findAll();
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(categoryService);
    }

    /* -------------------------------------------------
       findById tests
     ------------------------------------------------- */

    @Test // PS_3
    void PS_3_findByIdWhenValidId() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        Product result = productService.findById(productId);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(categoryService);
    }

    @Test // PS_4
    void PS_4_findByIdWhenInvalidId() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(productId));

        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(categoryService);
    }

    /* -------------------------------------------------
       create (with image) tests
     ------------------------------------------------- */

    @Test // PS_5
    void PS_5_createWhenInvalidPrice() {
        assertThrows(PriceCannotBeLessThanZeroException.class, () ->
                productService.create(name, -1.0, description, department, image, categoryId)
        );

        verifyNoInteractions(categoryService);
        verifyNoInteractions(productRepository);
    }

    @Test // PS_6
    void PS_6_createWhenValidPrice() {
        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Product created = productService.create(name, price, description, department, image, categoryId);

        assertNotNull(created);
        verify(categoryService).findById(categoryId);
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository, categoryService);
    }

    @Test // PS_7
    void PS_7_createWhenInvalidCategoryId() {
        when(categoryService.findById(categoryId)).thenThrow(new CategoryNotFoundException(categoryId));

        assertThrows(CategoryNotFoundException.class, () ->
                productService.create(name, price, description, department, image, categoryId)
        );

        verify(categoryService).findById(categoryId);
        verify(productRepository, never()).save(any());
        verifyNoMoreInteractions(categoryService);
        verifyNoInteractions(productRepository);
    }

    @Test // PS_8
    void PS_8_createWhenValidCategoryId() {
        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Product created = productService.create(name, price, description, department, image, categoryId);

        assertEquals(name, created.getName());
        assertEquals(price, created.getPrice());
        assertEquals(description, created.getDescription());
        assertEquals(department, created.getDepartment());
        assertEquals(image, created.getImage());
        assertEquals(category, created.getCategory());

        verify(categoryService).findById(categoryId);
        verify(productRepository).save(any(Product.class));
    }

    /* -------------------------------------------------
       create (without image) tests
       PS_9 - PS_12: same idea as PS_5 - PS_8
     ------------------------------------------------- */

    @Test // PS_9
    void PS_9_createWhenInvalidPrice() {
        assertThrows(PriceCannotBeLessThanZeroException.class, () ->
                productService.create(name, -0.01, description, department, categoryId)
        );

        verifyNoInteractions(categoryService);
        verifyNoInteractions(productRepository);
    }

    @Test // PS_10
    void PS_10_createWhenValidPrice() {
        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Product created = productService.create(name, price, description, department, categoryId);

        assertNotNull(created);
        verify(categoryService).findById(categoryId);
        verify(productRepository).save(any(Product.class));
    }

    @Test // PS_11
    void PS_11_createWhenInvalidCategoryId() {
        when(categoryService.findById(categoryId)).thenThrow(new CategoryNotFoundException(categoryId));

        assertThrows(CategoryNotFoundException.class, () ->
                productService.create(name, price, description, department, categoryId)
        );

        verify(categoryService).findById(categoryId);
        verify(productRepository, never()).save(any());
    }

    @Test // PS_12
    void PS_12_createWhenValidCategoryId() {
        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Product created = productService.create(name, price, description, department, categoryId);

        assertEquals(name, created.getName());
        assertEquals(price, created.getPrice());
        assertEquals(description, created.getDescription());
        assertEquals(department, created.getDepartment());
        assertEquals(category, created.getCategory());

        verify(categoryService).findById(categoryId);
        verify(productRepository).save(any(Product.class));
    }

    /* -------------------------------------------------
       addImage tests
     ------------------------------------------------- */

    @Test // PS_13
    void PS_13_addImageWhenImageIsNull() {
        assertThrows(IllegalImageUrlException.class, () -> productService.addImage(productId, null));
        verifyNoInteractions(productRepository);
        verifyNoInteractions(categoryService);
    }

    @Test // PS_14
    void PS_14_addImageWhenImageIsEmpty() {
        assertThrows(IllegalImageUrlException.class, () -> productService.addImage(productId, ""));
        verifyNoInteractions(productRepository);
        verifyNoInteractions(categoryService);
    }

    @Test // PS_15 (non-empty image -> success path)
    void PS_15_addImageWhenImageIsNonEmpty() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        String newImage = "new.png";
        Product updated = productService.addImage(productId, newImage);

        assertEquals(newImage, updated.getImage());
        verify(productRepository).findById(productId);
        verify(productRepository).save(existingProduct);
        verifyNoInteractions(categoryService);
    }

    @Test // PS_16 (valid id check - explicitly)
    void PS_16_addImageWhenValidId() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        productService.addImage(productId, "ok.png");

        verify(productRepository).findById(productId);
        verify(productRepository).save(any(Product.class));
    }

    @Test // PS_17
    void PS_17_addImageWhenInvalidId() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                productService.addImage(productId, "ok.png")
        );

        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
        verifyNoInteractions(categoryService);
    }

    /* -------------------------------------------------
       update tests
     ------------------------------------------------- */

    @Test // PS_18
    void PS_18_updateWhenInvalidPrice() {
        assertThrows(PriceCannotBeLessThanZeroException.class, () ->
                productService.update(productId, name, -1.0, description, department, image, categoryId)
        );

        verifyNoInteractions(categoryService);
        verifyNoInteractions(productRepository);
    }

    @Test // PS_19
    void PS_19_updateWhenValidPrice() {
        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        productService.update(productId, name, 0.0, description, department, image, categoryId);

        verify(categoryService).findById(categoryId);
    }

    @Test // PS_20
    void PS_20_updateWhenInvalidCategoryId() {
        when(categoryService.findById(categoryId)).thenThrow(new CategoryNotFoundException(categoryId));

        assertThrows(CategoryNotFoundException.class, () ->
                productService.update(productId, name, price, description, department, image, categoryId)
        );

        verify(categoryService).findById(categoryId);
        verify(productRepository, never()).findById(any());
        verify(productRepository, never()).save(any());
    }

    @Test // PS_21
    void PS_21_updateWhenValidCategoryId() {
        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product updated = productService.update(productId, "New", price, "NewDesc", department, "new.png", categoryId);

        assertEquals("New", updated.getName());
        assertEquals(price, updated.getPrice());
        assertEquals("NewDesc", updated.getDescription());
        assertEquals("new.png", updated.getImage());
        assertEquals(category, updated.getCategory());

        verify(categoryService).findById(categoryId);
        verify(productRepository).findById(productId);
        verify(productRepository).save(existingProduct);
    }

    @Test // PS_22
    void PS_22_updateWhenInvalidProductId() {
        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                productService.update(productId, name, price, description, department, image, categoryId)
        );

        verify(categoryService).findById(categoryId);
        verify(productRepository).findById(productId);
        verify(productRepository, never()).save(any());
    }

    @Test // PS_23
    void PS_23_updateWhenValidProductId() {
        when(categoryService.findById(categoryId)).thenReturn(category);
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product updated = productService.update(productId, "X", 99.0, "D", department, "img2", categoryId);

        assertEquals("X", updated.getName());
        assertEquals(99.0, updated.getPrice());
        assertEquals("D", updated.getDescription());
        assertEquals("img2", updated.getImage());

        verify(categoryService).findById(categoryId);
        verify(productRepository).findById(productId);
        verify(productRepository).save(existingProduct);
    }

    /* -------------------------------------------------
       deleteById test
     ------------------------------------------------- */

    @Test // PS_24
    void PS_24_deleteByIdCallsRepositoryDeleteOnce() {
        doNothing().when(productRepository).deleteById(productId);

        productService.deleteById(productId);

        verify(productRepository, times(1)).deleteById(productId);
        verifyNoMoreInteractions(productRepository);
        verifyNoInteractions(categoryService);
    }

    /* -------------------------------------------------
       findPage
     ------------------------------------------------- */

    @Test // PS_25
    void PS_25_findPageWhenPageNumInvalid() {
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.findPage(null, null, null, null, null, null, 0, 10, "byIdAsc");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
    }

    @Test // PS_26
    void PS_26_findPageWhenPageNumValid() {
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.findPage(null, null, null, null, null, null, 1, 10, "byIdAsc");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(10, pageable.getPageSize());
        assertTrue(pageable.getSort().getOrderFor("id").isAscending());
    }

    @Test // PS_27
    void PS_27_findPageWhenPageSizeInvalid() {
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.findPage(null, null, null, null, null, null, 1, 2, "byPriceDesc");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(3, pageable.getPageSize());
    }

    @Test // PS_28
    void PS_28_findPageWhenPageSizeValid() {
        when(productRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.findPage(null, null, null, null, null, null, 1, 3, "byNameDesc");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(0, pageable.getPageNumber());
        assertEquals(3, pageable.getPageSize());
    }
}
