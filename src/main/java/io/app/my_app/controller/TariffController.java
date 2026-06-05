package io.app.my_app.controller;

import io.app.my_app.model.domain.ApiWrapper;
import io.app.my_app.model.dtos.tariff.TariffFilter;
import io.app.my_app.model.dtos.tariff.TariffRequest;
import io.app.my_app.model.dtos.tariff.TariffResponse;
import io.app.my_app.service.TariffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tariffs")
@RequiredArgsConstructor
@Tag(name = "Tariffs", description = "Tariff configuration and pricing tier management")
public class TariffController {

    private final TariffService tariffService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "List all tariffs",
            description = "Retrieve a paginated list of tariffs with optional filtering by product, name, or status. Requires ADMIN or FINANCE role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tariffs retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<Page<TariffResponse>>> findAll(
            @ParameterObject TariffFilter filter,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiWrapper<>(tariffService.findAll(filter, pageable), localize("responses.tariff.list"), HttpStatus.OK).toResponseEntity();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get tariff by ID",
            description = "Retrieve detailed tariff configuration including pricing tiers and effective dates. Requires ADMIN or FINANCE role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tariff retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Tariff not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<TariffResponse>> findById(@PathVariable UUID id) {
        return new ApiWrapper<>(tariffService.findById(id), localize("responses.tariff.get"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Configure a new tariff",
            description = "Create and configure a new tariff with pricing tiers. Each tier defines consumption ranges and prices. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tariff configured successfully", content = @Content(schema = @Schema(implementation = TariffResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed - overlapping tiers, invalid ranges, or missing fields"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<TariffResponse>> configure(@Valid @RequestBody TariffRequest request) {
        return new ApiWrapper<>(tariffService.configure(request), localize("responses.tariff.create"), HttpStatus.CREATED).toResponseEntity();
    }

    private String localize(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
