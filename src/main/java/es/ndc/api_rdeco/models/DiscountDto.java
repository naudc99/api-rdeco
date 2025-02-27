package es.ndc.api_rdeco.models;

import lombok.Data;


@Data
public class DiscountDto {
    private Long id;
    private Double percentage;
    private Long productId;
}
