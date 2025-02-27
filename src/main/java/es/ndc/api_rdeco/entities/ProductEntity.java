package es.ndc.api_rdeco.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    private String description;

    @Column(name = "price")
    private Double price; // El precio actual (con descuento si aplica)

    @Column(name = "original_price")
    private Double originalPrice; // El precio original antes del descuento

    private Integer stock;

    @NotBlank
    @Column(unique = true)
    private String referenceNumber;

    @Lob
    @Column(name = "productPhoto", length = 10485760)
    private byte[] productPhoto;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CategoryEntity category;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "discount_id", referencedColumnName = "id")
    @JsonIgnore
    private DiscountEntity discount;


}


