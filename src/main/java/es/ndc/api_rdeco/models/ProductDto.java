package es.ndc.api_rdeco.models;

import lombok.Data;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double originalPrice;
    private Integer stock;
    private String referenceNumber;
    private byte[] productPhoto;
    private Long categoryId;
    private Long discountId;
}
