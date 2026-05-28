package io.app.my_app.model.dtos.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "{validation.product.sku.required}")
    @Size(max = 100, message = "{validation.product.sku.size}")
    private String sku;

    @NotBlank(message = "{validation.product.name.required}")
    @Size(max = 255, message = "{validation.product.name.size}")
    private String name;

    @NotBlank(message = "{validation.product.description.required}")
    @Size(max = 1000, message = "{validation.product.description.size}")
    private String description;

    @NotNull(message = "{validation.product.price.required}")
    @PositiveOrZero(message = "{validation.product.price.positive}")
    private Double price;
}
