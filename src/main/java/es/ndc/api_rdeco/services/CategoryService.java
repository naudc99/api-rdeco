package es.ndc.api_rdeco.services;

import es.ndc.api_rdeco.entities.CategoryEntity;
import es.ndc.api_rdeco.models.CategoryDto;
import es.ndc.api_rdeco.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Obtener todas las categorías
    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Obtener una categoría por ID
    public CategoryEntity getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
    }

    // Crear una nueva categoría
    public CategoryEntity createCategory(CategoryDto categoryDTO) {
        CategoryEntity category = new CategoryEntity();
        category.setName(categoryDTO.getName());
        return categoryRepository.save(category);
    }

    // Actualizar una categoría existente
    public CategoryEntity updateCategory(Long id, CategoryDto categoryDTO) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));

        category.setName(categoryDTO.getName());
        return categoryRepository.save(category);
    }

    // Eliminar una categoría
    public void deleteCategory(Long id) {
        CategoryEntity category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + id));
        categoryRepository.delete(category);
    }

    // Obtener todas las categorías paginadas
    public Map<String, Object> getAllCategoriesPaginated(int page, int size, String sortBy, String name) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));
        Page<CategoryEntity> categoryPage;

        if (name != null && !name.isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(name, paging);
        } else {
            categoryPage = categoryRepository.findAll(paging);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", categoryPage.getContent());
        response.put("totalElements", categoryPage.getTotalElements());
        response.put("totalPages", categoryPage.getTotalPages());
        response.put("currentPage", categoryPage.getNumber());

        return response;
    }
}
