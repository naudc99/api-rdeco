package es.ndc.api_rdeco.repositories;

import es.ndc.api_rdeco.entities.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<SaleEntity, Long> {
}
