package es.ndc.api_rdeco.repositories;

import es.ndc.api_rdeco.entities.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    Page<CategoryEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
