package finki.ukim.mk.onlineclothingstore.service;


import finki.ukim.mk.onlineclothingstore.model.Category;
import finki.ukim.mk.onlineclothingstore.model.exceptions.CategoryNotFoundException;
import finki.ukim.mk.onlineclothingstore.repository.CategoryRepository;
import finki.ukim.mk.onlineclothingstore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    /* -------------------------------------------------
        findAll()
    ------------------------------------------------- */

    @Test
    void CS_1_findAllWhenDBEmpty(){
        //given
        when(categoryRepository.findAll()).thenReturn(List.of());

        //when
        List<Category> result = categoryRepository.findAll();

        //then
        assertEquals(0, result.size());
        assertSame(List.of(), result);
        verify(categoryRepository).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }


    @Test
    void CS_2_findAllWhenRecordsExist() {
        //given
        List<Category> expected = List.of(
                new Category(1L, "Jeans", ""),
                new Category(2L, "Hoodies", "")
        );
        when(categoryRepository.findAll()).thenReturn(expected);

        //when
        List<Category> result = categoryRepository.findAll();

        //then
        assertEquals(expected.size(), result.size());
        assertSame(expected, result);
        verify(categoryRepository).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }
    /* -------------------------------------------------
        findById(Long id)
    ------------------------------------------------- */

    @Test
    void CS_3_findByIdWhenValidId(){
        // given
        Long id = 1L;
        Category existing = new Category(id, "Jeans", "");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));

        // when
        Category result = categoryService.findById(id);

        // then
        assertSame(existing, result);
        verify(categoryRepository).findById(id);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void CS_4_findByIdWhenInvalidId(){
        // given
        Long id = 1L;
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        // when + then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(id));
        verify(categoryRepository).findById(id);
        verifyNoMoreInteractions(categoryRepository);
    }

    /* -------------------------------------------------
        create(String name, String description)
    ------------------------------------------------- */

    @Test
    void CS_5_createTestRepositoryCalls(){
        //given
        String name = "Name";
        String description = "Description";
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Category createdCategory = categoryService.create(name, description);

        //then
        assertInstanceOf(Category.class, createdCategory);
        assertEquals(name, createdCategory.getName());
        assertEquals(description, createdCategory.getDescription());
        verify(categoryRepository).save(createdCategory);
        verifyNoMoreInteractions(categoryRepository);
    }

    /* -------------------------------------------------
        update(Long id, String name, String description)
    ------------------------------------------------- */

    @Test
    void CS_6_updateWhenIdValidId(){
        //given
        Category category = new Category(1L, "Name", "Desc");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Category updated = categoryService.update(1L, "NewName", "NewDesc");

        //then
        assertSame(category, updated);
        assertEquals("NewName", updated.getName());
        assertEquals("NewDesc", updated.getDescription());
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(updated);
        verifyNoMoreInteractions(categoryRepository);


    }

    @Test
    void CS_7_updateWhenInvalidId(){
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.update(1L, "", ""));

        verify(categoryRepository).findById(1L);
        verifyNoMoreInteractions(categoryRepository);

    }

    /* -------------------------------------------------
        deleteByID(Long id)
    ------------------------------------------------- */

    @Test
    void CS_8_deleteByIdTestRepositoryCalls() {
        // when
        categoryService.deleteById(1L);

        // Assert
        verify(categoryRepository).deleteById(1L);
        verifyNoMoreInteractions(categoryRepository);
    }

}
