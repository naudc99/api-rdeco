package es.ndc.api_rdeco.services;

import es.ndc.api_rdeco.entities.DiscountEntity;
import es.ndc.api_rdeco.entities.ProductEntity;
import es.ndc.api_rdeco.models.DiscountDto;
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
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<DiscountEntity> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public DiscountEntity getDiscountById(Long id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with ID: " + id));
    }

    @Transactional
    public DiscountEntity createDiscount(DiscountDto discountDTO) {
        validateDiscount(discountDTO);

        DiscountEntity discount = new DiscountEntity();
        discount.setPercentage(discountDTO.getPercentage());

        if (discountDTO.getProductId() != null) {
            ProductEntity product = productRepository.findById(discountDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + discountDTO.getProductId()));

            if (product.getOriginalPrice() == null) {
                product.setOriginalPrice(product.getPrice());
            }

            discount.setProduct(product);
            product.setDiscount(discount);
            applyDiscount(product, discountDTO.getPercentage());

            discount = discountRepository.save(discount);
            productRepository.save(product);
        } else {
            discount = discountRepository.save(discount);
        }

        return discount;
    }

    @Transactional
    public DiscountEntity updateDiscount(Long id, DiscountDto discountDTO) {
        DiscountEntity discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with ID: " + id));

        validateDiscount(discountDTO);
        discount.setPercentage(discountDTO.getPercentage());

        if (discount.getProduct() != null) {
            ProductEntity currentProduct = discount.getProduct();
            restoreOriginalPrice(currentProduct);
            currentProduct.setDiscount(null);
            productRepository.save(currentProduct);
        }

        if (discountDTO.getProductId() != null) {
            ProductEntity product = productRepository.findById(discountDTO.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with ID: " + discountDTO.getProductId()));

            discount.setProduct(product);
            product.setDiscount(discount);
            applyDiscount(product, discountDTO.getPercentage());
            productRepository.save(product);
        }

        return discountRepository.save(discount);
    }

    @Transactional
    public void deleteDiscount(Long id) {
        DiscountEntity discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found with ID: " + id));

        if (discount.getProduct() != null) {
            ProductEntity product = discount.getProduct();
            restoreOriginalPrice(product);
            product.setDiscount(null);
            productRepository.save(product);
        }

        discountRepository.delete(discount);
    }

    @Transactional
    public Map<String, Object> getDiscountsByPercentage(int page, int size, String sortBy, Double minPercentage, Double maxPercentage) {
        Pageable paging = PageRequest.of(page, size, Sort.by(sortBy));

        Page<DiscountEntity> discountsPage;
        if (minPercentage != null && maxPercentage != null) {
            discountsPage = discountRepository.findByPercentageBetween(minPercentage, maxPercentage, paging);
        } else {
            discountsPage = discountRepository.findAll(paging);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", discountsPage.getContent());
        response.put("totalElements", discountsPage.getTotalElements());
        response.put("totalPages", discountsPage.getTotalPages());
        response.put("currentPage", discountsPage.getNumber());

        return response;
    }


    private void validateDiscount(DiscountDto discountDTO) {
        if (discountDTO.getPercentage() < 0 || discountDTO.getPercentage() > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
    }

    private void applyDiscount(ProductEntity product, double percentage) {
        double discountAmount = product.getOriginalPrice() * (percentage / 100.0);
        product.setPrice(product.getOriginalPrice() - discountAmount);
    }

    private void restoreOriginalPrice(ProductEntity product) {
        if (product.getOriginalPrice() != null) {
            product.setPrice(product.getOriginalPrice());
        }
    }
}
