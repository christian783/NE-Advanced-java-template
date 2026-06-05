package io.app.my_app.controller;

import io.app.my_app.model.domain.ApiWrapper;
import io.app.my_app.model.dtos.meter.MeterFilter;
import io.app.my_app.model.dtos.meter.MeterRequest;
import io.app.my_app.model.dtos.meter.MeterResponse;
import io.app.my_app.service.MeterService;
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
@RequestMapping("/api/v1/meters")
@RequiredArgsConstructor
@Tag(name = "Meters", description = "Meter device registration and management")
public class MeterController {

    private final MeterService meterService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "List all meters",
            description = "Retrieve a paginated list of meters with optional filtering by customer, meter number, type, or status. Requires ADMIN, OPERATOR, or FINANCE role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meters retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<Page<MeterResponse>>> findAll(
            @ParameterObject
            MeterFilter filter,
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiWrapper<>(meterService.findAll(filter, pageable), localize("responses.meter.list"), HttpStatus.OK).toResponseEntity();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get meter by ID",
            description = "Retrieve detailed information about a specific meter including installation date, status, and associated customer."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meter retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Meter not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<MeterResponse>> findById(
            @PathVariable
            UUID id
    ) {
        return new ApiWrapper<>(meterService.findById(id), localize("responses.meter.get"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Register a new meter",
            description = "Register a new meter device for a customer. Meter number must be unique. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Meter registered successfully", content = @Content(schema = @Schema(implementation = MeterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed - duplicate meter number, invalid customer, or missing fields"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<MeterResponse>> create(
            @RequestBody
            @Valid MeterRequest request
    ) {
        return new ApiWrapper<>(meterService.create(request), localize("responses.meter.create"), HttpStatus.CREATED).toResponseEntity();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update meter information",
            description = "Update meter details such as installation date, status, or customer assignment. Requires ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meter updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed or invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Meter or customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<MeterResponse>> update(
            @PathVariable
            UUID id,
            @RequestBody
            @Valid MeterRequest request
    ) {
        return new ApiWrapper<>(meterService.update(id, request), localize("responses.meter.update"), HttpStatus.OK).toResponseEntity();
    }

    private String localize(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
