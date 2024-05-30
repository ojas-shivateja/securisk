package com.insure.rfq.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NatureofBusinessDto {
    @NotEmpty
    @NotBlank(message = "Name must not be blank or empty")
    private String nameofNatureofBusiness;
}
