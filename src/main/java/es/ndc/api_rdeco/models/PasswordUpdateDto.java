package es.ndc.api_rdeco.models;


import lombok.Data;


@Data
public class PasswordUpdateDto {

    private String passwordNew;
    private String passwordOld;
}
