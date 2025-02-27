package es.ndc.api_rdeco.repositories;

import es.ndc.api_rdeco.entities.DiscountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {
    Page<DiscountEntity> findByPercentageBetween(Double min, Double max, Pageable pageable);
}
