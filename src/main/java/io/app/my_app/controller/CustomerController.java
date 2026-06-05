package io.app.my_app.controller;

import io.app.my_app.model.domain.ApiWrapper;
import io.app.my_app.model.dtos.customer.CustomerFilter;
import io.app.my_app.model.dtos.customer.CustomerRequest;
import io.app.my_app.model.dtos.customer.CustomerResponse;
import io.app.my_app.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer account management and CRUD operations")
public class CustomerController {

    private final CustomerService customerService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','OPERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "List all customers",
            description = "Retrieve a paginated list of customers with optional filtering by name, phone, email, address, or status. Requires ADMIN, FINANCE, or OPERATOR role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully with pagination metadata"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<Page<CustomerResponse>>> findAll(
            @ParameterObject
            CustomerFilter filter,
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiWrapper<>(customerService.findAll(filter, pageable), localize("responses.customer.list"), HttpStatus.OK).toResponseEntity();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','OPERATOR','CUSTOMER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get customer by ID",
            description = "Retrieve a single customer record by their UUID. CUSTOMER role can only access their own record."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions or trying to access another customer's record"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<CustomerResponse>> findById(
            @PathVariable
            UUID id
    ) {
        return new ApiWrapper<>(customerService.findById(id), localize("responses.customer.get"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create a new customer",
            description = "Create a new customer account with full details. Requires ADMIN role. National ID must be unique."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer created successfully", content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed - invalid email format, duplicate national ID, or missing required fields"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<CustomerResponse>> create(
            @RequestBody
            @Valid CustomerRequest request
    ) {
        return new ApiWrapper<>(customerService.create(request), localize("responses.customer.create"), HttpStatus.CREATED).toResponseEntity();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update customer",
            description = "Update an existing customer's information. Requires ADMIN role. National ID cannot be changed."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<CustomerResponse>> update(
            @PathVariable
            UUID id,
            @RequestBody
            @Valid CustomerRequest request
    ) {
        return new ApiWrapper<>(customerService.update(id, request), localize("responses.customer.update"), HttpStatus.OK).toResponseEntity();
    }

    private String localize(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
