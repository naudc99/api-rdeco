package es.ndc.api_rdeco.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {

    @NotBlank
    @Size(min = 3, max = 50)
    private String name;
}

