package es.ndc.api_rdeco.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pdfUrl; // Guardar URL del PDF generado

    @OneToOne
    @JoinColumn(name = "sale_id", referencedColumnName = "id")
    private SaleEntity sale;
}

