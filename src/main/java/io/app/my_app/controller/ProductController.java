package io.app.my_app.controller;

import io.app.my_app.model.domain.ApiWrapper;
import io.app.my_app.model.dtos.product.ProductFilter;
import io.app.my_app.model.dtos.product.ProductRequest;
import io.app.my_app.model.dtos.product.ProductResponse;
import io.app.my_app.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;
    private final MessageSource messageSource;

    @GetMapping
    @Operation(summary = "List products")
    public ResponseEntity<ApiWrapper<Page<ProductResponse>>> findAll(
            @ParameterObject ProductFilter filter,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiWrapper<>(productService.findAll(filter, pageable), localize("responses.product.list"), HttpStatus.OK).toResponseEntity();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a product")
    public ResponseEntity<ApiWrapper<ProductResponse>> findById(@PathVariable UUID id) {
        return new ApiWrapper<>(productService.findById(id), localize("responses.product.get"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping
    @Operation(summary = "Create a product")
    public ResponseEntity<ApiWrapper<ProductResponse>> create(
            @Valid @RequestBody ProductRequest request
    ) {
        return new ApiWrapper<>(productService.create(request), localize("responses.product.create"), HttpStatus.CREATED).toResponseEntity();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    public ResponseEntity<ApiWrapper<ProductResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ProductRequest request
    ) {
        return new ApiWrapper<>(productService.update(id, request), localize("responses.product.update"), HttpStatus.OK).toResponseEntity();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product")
    public ResponseEntity<ApiWrapper<Void>> delete(@PathVariable UUID id) {
        productService.delete(id);
        return new ApiWrapper<Void>(null, localize("responses.product.delete"), HttpStatus.OK).toResponseEntity();
    }


    private String localize(String code){
        return messageSource.getMessage(code,
                null,
                LocaleContextHolder.getLocale()
        );
    }
}
