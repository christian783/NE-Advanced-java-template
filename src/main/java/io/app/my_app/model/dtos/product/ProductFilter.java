package io.app.my_app.model.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private UUID id;
    private String name;
    private String sku;
    private String description;
    private Double price;
}
