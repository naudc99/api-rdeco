package es.ndc.api_rdeco.controllers;

import es.ndc.api_rdeco.entities.CategoryEntity;
import es.ndc.api_rdeco.models.CategoryDto;
import es.ndc.api_rdeco.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Obtener todas las categorías sin paginación
    @GetMapping
    public ResponseEntity<List<CategoryEntity>> getAllCategories() {
        List<CategoryEntity> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Obtener categorías paginadas y filtradas por nombre
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getAllCategoriesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(required = false) String name) {
        Map<String, Object> categories = categoryService.getAllCategoriesPaginated(page, size, sortBy, name);
        return ResponseEntity.ok(categories);
    }

    // Obtener una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryEntity> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // Crear una nueva categoría
    @PostMapping
    public ResponseEntity<CategoryEntity> createCategory(@Valid @RequestBody CategoryDto categoryDTO) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
    }

    // Actualizar una categoría
    @PutMapping("/{id}")
    public ResponseEntity<CategoryEntity> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDTO));
    }

    // Eliminar una categoría
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

