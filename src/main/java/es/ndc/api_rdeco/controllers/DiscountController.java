package es.ndc.api_rdeco.controllers;

import es.ndc.api_rdeco.entities.DiscountEntity;
import es.ndc.api_rdeco.models.DiscountDto;
import es.ndc.api_rdeco.services.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/discount")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    // Obtener todos los descuentos
    @GetMapping
    public ResponseEntity<List<DiscountEntity>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    // Obtener un descuento por ID
    @GetMapping("/{id}")
    public ResponseEntity<DiscountEntity> getDiscountById(@PathVariable Long id) {
        return ResponseEntity.ok(discountService.getDiscountById(id));
    }

    // Crear un nuevo descuento
    @PostMapping
    public ResponseEntity<DiscountEntity> createDiscount(@RequestBody DiscountDto discountDto) {
        DiscountEntity newDiscount = discountService.createDiscount(discountDto);
        return ResponseEntity.ok(newDiscount);
    }

    // Actualizar un descuento existente (y asignar productos)
    @PutMapping("/{id}")
    public ResponseEntity<DiscountEntity> updateDiscount(
            @PathVariable Long id, @RequestBody DiscountDto discountDto) {
        DiscountEntity updatedDiscount = discountService.updateDiscount(id, discountDto);
        return ResponseEntity.ok(updatedDiscount);
    }

    // Eliminar un descuento
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }

    // Obtener descuentos paginados con filtro de porcentaje
    @GetMapping("/paginated")
    public ResponseEntity<Map<String, Object>> getAllDiscountsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) Double minPercentage,
            @RequestParam(required = false) Double maxPercentage) {

        return ResponseEntity.ok(discountService.getDiscountsByPercentage(page, size, sortBy, minPercentage, maxPercentage));
    }

}
