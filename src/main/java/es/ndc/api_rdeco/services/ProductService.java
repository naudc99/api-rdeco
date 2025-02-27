package es.ndc.api_rdeco.services;

import es.ndc.api_rdeco.entities.*;
import es.ndc.api_rdeco.models.ProductDto;
import es.ndc.api_rdeco.repositories.CategoryRepository;
import es.ndc.api_rdeco.repositories.DiscountRepository;
import es.ndc.api_rdeco.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DiscountRepository discountRepository;

    // Obtener todos los productos
    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    // Obtener un producto por ID
    public ProductEntity getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    // Crear un nuevo producto
    public ProductEntity createProduct(ProductDto productDTO) {
        // Crear un nuevo producto
        ProductEntity product = new ProductEntity();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setOriginalPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setReferenceNumber(productDTO.getReferenceNumber());
        product.setProductPhoto(productDTO.getProductPhoto());

        // Setear categoría
        CategoryEntity category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productDTO.getCategoryId()));
        product.setCategory(category);

        return productRepository.save(product);
    }


    // Actualizar un producto existente
    public ProductEntity updateProduct(Long id, ProductDto productDTO) {
        // Obtener el producto por ID
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));

        // Actualizar los campos del producto
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setStock(productDTO.getStock());
        product.setReferenceNumber(productDTO.getReferenceNumber());
        product.setProductPhoto(productDTO.getProductPhoto());

        // Actualizar el precio actual (price)
        product.setPrice(productDTO.getPrice());

        // Establecer el precio original igual al precio actual
        product.setOriginalPrice(productDTO.getPrice());

        // Actualizar categoría si se especifica un ID de categoría
        if (productDTO.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + productDTO.getCategoryId()));
            product.setCategory(category);
        }

        // Guardar y devolver el producto actualizado
        return productRepository.save(product);
    }

    // Eliminar un producto
    public void deleteProduct(Long id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
        productRepository.delete(product);
    }

    // Obtener productos paginados y filtrados por nombre
    @Transactional
    public Map<String, Object> getAllProductsPaginated(int page, int size, String sortBy, String name) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));

        Page<ProductEntity> productsPage;
        if (name != null && !name.isEmpty()) {
            productsPage = productRepository.findByNameContainingIgnoreCase(name, paging);
        } else {
            productsPage = productRepository.findAll(paging);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", productsPage.getContent());
        response.put("totalElements", productsPage.getTotalElements());
        response.put("totalPages", productsPage.getTotalPages());
        response.put("currentPage", productsPage.getNumber());

        return response;
    }

}
