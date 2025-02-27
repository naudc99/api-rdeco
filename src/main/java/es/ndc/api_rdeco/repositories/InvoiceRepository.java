package es.ndc.api_rdeco.repositories;

import es.ndc.api_rdeco.entities.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
}
