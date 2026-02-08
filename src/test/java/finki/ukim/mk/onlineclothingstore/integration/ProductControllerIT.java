package finki.ukim.mk.onlineclothingstore.integration;

import finki.ukim.mk.onlineclothingstore.model.Category;
import finki.ukim.mk.onlineclothingstore.model.Product;
import finki.ukim.mk.onlineclothingstore.model.Rating;
import finki.ukim.mk.onlineclothingstore.model.User;
import finki.ukim.mk.onlineclothingstore.model.Variant;
import finki.ukim.mk.onlineclothingstore.model.enums.Department;
import finki.ukim.mk.onlineclothingstore.model.enums.Role;
import finki.ukim.mk.onlineclothingstore.model.enums.Size;
import finki.ukim.mk.onlineclothingstore.model.exceptions.ProductNotFoundException;
import finki.ukim.mk.onlineclothingstore.repository.CategoryRepository;
import finki.ukim.mk.onlineclothingstore.repository.ProductRepository;
import finki.ukim.mk.onlineclothingstore.repository.RatingRepository;
import finki.ukim.mk.onlineclothingstore.repository.UserRepository;
import finki.ukim.mk.onlineclothingstore.repository.VariantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductControllerIT {

    @Autowired private MockMvc mockMvc;

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private VariantRepository variantRepository;
    @Autowired private RatingRepository ratingRepository;
    @Autowired private UserRepository userRepository;

    private User testUser;
    private User adminUser;

    private Category hoodiesCat;
    private Category jeansCat;

    private Product hoodie;
    private Product jeans;

    @BeforeEach
    void setUp() {
        testUser = userRepository.save(new User("user1", "pass", "Ardit", "Selmani", Role.ROLE_USER));
        adminUser = userRepository.save(new User("admin1", "pass", "Admin", "User", Role.ROLE_ADMIN));

        hoodiesCat = categoryRepository.save(new Category("Hoodies", "Hoodies category"));
        jeansCat = categoryRepository.save(new Category("Jeans", "Jeans category"));

        hoodie = productRepository.save(new Product(
                "Blue Hoodie",
                "Warm hoodie",
                50.0,
                Department.MEN,
                "/uploads/products/hoodie.jpg",
                hoodiesCat
        ));

        jeans = productRepository.save(new Product(
                "Red Jeans",
                "Jeans description",
                70.0,
                Department.WOMEN,
                "/uploads/products/jeans.jpg",
                jeansCat
        ));

        variantRepository.save(new Variant(hoodie, Size.M, 10));
        variantRepository.save(new Variant(hoodie, Size.L, 5));
        variantRepository.save(new Variant(jeans, Size.S, 7));

        ratingRepository.save(new Rating(4, hoodie));
        ratingRepository.save(new Rating(5, hoodie));
        ratingRepository.save(new Rating(3, jeans));
    }

    // -------------------------
    // GET /products (getProductsPage)
    // -------------------------

    @Test
    void PC_01_getProductsPage_anonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void PC_02_getProductsPage_admin_forbidden_forwardOrRedirectToAccessDenied() throws Exception {
        MvcResult res = mockMvc.perform(get("/products").with(user(adminUser)))
                .andExpect(status().isForbidden())
                .andReturn();

        String forwarded = res.getResponse().getForwardedUrl();
        String redirected = res.getResponse().getRedirectedUrl();

        String target = (forwarded != null) ? forwarded : redirected;

        assertThat(target)
                .as("Expected forward/redirect to /access-denied but got forwarded=%s redirected=%s", forwarded, redirected)
                .isNotNull()
                .contains("access-denied");
    }


    @Test
    void PC_03_getProductsPage_user_filtersAbsent_contractAndModel() throws Exception {
        MvcResult result = mockMvc.perform(get("/products").with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/products"))
                .andExpect(model().attributeExists("page", "departments", "categories", "variants"))
                .andExpect(model().attribute("selectedSort", "byPriceAsc"))
                .andExpect(model().attributeExists("cartEmpty"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        @SuppressWarnings("unchecked")
        Page<Product> page = (Page<Product>) model.get("page");
        assertThat(page.getTotalElements()).isEqualTo(2);

        @SuppressWarnings("unchecked")
        List<Category> cats = (List<Category>) model.get("categories");
        assertThat(cats).extracting(Category::getName).contains("Hoodies", "Jeans");

        assertThat((Boolean) model.get("cartEmpty")).isTrue();

        assertThat(page.getContent()).isNotEmpty();
        assertThat(page.getContent().get(0).getId()).isEqualTo(hoodie.getId());
    }

    @Test
    void PC_04_getProductsPage_user_filtersPresent_contractAndFiltering() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/products")
                                .with(user(testUser))
                                .param("name", "hood")
                                .param("greaterThan", "40")
                                .param("lessThan", "60")
                                .param("categoryId", hoodiesCat.getId().toString())
                                .param("department", "MEN")
                                .param("pageNum", "1")
                                .param("pageSize", "9")
                                .param("sort", "byNameDesc")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/products"))
                .andExpect(model().attribute("selectedName", "hood"))
                .andExpect(model().attribute("selectedGreater", 40.0))
                .andExpect(model().attribute("selectedLess", 60.0))
                .andExpect(model().attribute("selectedCat", hoodiesCat.getId()))
                .andExpect(model().attribute("selectedDepartment", Department.MEN))
                .andExpect(model().attribute("selectedSort", "byNameDesc"))
                .andReturn();

        @SuppressWarnings("unchecked")
        Page<Product> page = (Page<Product>) result.getModelAndView().getModel().get("page");

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(hoodie.getId());
    }

    @Test
    void PC_05_getProductsPage_invalidEnumBinding_returns400() throws Exception {
        mockMvc.perform(
                        get("/products")
                                .with(user(testUser))
                                .param("department", "NOT_A_REAL_DEPARTMENT")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void PC_06_getProductsPage_pageNumZero_isNormalized_notRejected() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/products")
                                .with(user(testUser))
                                .param("pageNum", "0")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/products"))
                .andReturn();

        @SuppressWarnings("unchecked")
        Page<Product> page = (Page<Product>) result.getModelAndView().getModel().get("page");

        assertThat(page.getNumber()).isEqualTo(0);
    }

    // -------------------------
    // GET /products/details/{id} (getProductDetails)
    // -------------------------

    @Test
    void PC_07_getProductDetails_existingId_contractAndModel() throws Exception {
        MvcResult result = mockMvc.perform(get("/products/details/{id}", hoodie.getId()).with(user(testUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("master-template"))
                .andExpect(model().attribute("bodyContent", "userTemplates/product-details"))
                .andExpect(model().attributeExists("product", "variants", "avgRating", "ratingCount"))
                .andExpect(model().attributeExists("cartEmpty"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();

        Product p = (Product) model.get("product");
        assertThat(p.getId()).isEqualTo(hoodie.getId());

        Double avg = (Double) model.get("avgRating");
        Integer cnt = (Integer) model.get("ratingCount");

        assertThat(avg).isEqualTo(4.5);
        assertThat(cnt).isEqualTo(2);

        assertThat((Boolean) model.get("cartEmpty")).isTrue();
    }

    @Test
    void PC_08_getProductDetails_nonExistingId_exceptionPropagates() throws Exception {
        Exception ex = assertThrows(Exception.class, () ->
                mockMvc.perform(get("/products/details/{id}", 999999L).with(user(testUser)))
                        .andReturn()
        );

        Throwable root = ex;
        while (root.getCause() != null) root = root.getCause();

        assertThat(root).isInstanceOf(ProductNotFoundException.class);
    }
}
