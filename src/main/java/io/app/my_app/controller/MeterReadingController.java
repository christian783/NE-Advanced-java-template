package io.app.my_app.controller;

import io.app.my_app.model.domain.ApiWrapper;
import io.app.my_app.model.dtos.reading.MeterReadingFilter;
import io.app.my_app.model.dtos.reading.MeterReadingRequest;
import io.app.my_app.model.dtos.reading.MeterReadingResponse;
import io.app.my_app.service.MeterReadingService;
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
@RequestMapping("/api/v1/meter-readings")
@RequiredArgsConstructor
@Tag(name = "Meter Readings", description = "Meter consumption reading capture and history")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "List meter readings",
            description = "Retrieve a paginated list of meter readings with optional filtering by meter ID, reading month/year. Requires ADMIN, OPERATOR, or FINANCE role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meter readings retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<Page<MeterReadingResponse>>> findAll(
            @ParameterObject
            MeterReadingFilter filter,
            @ParameterObject
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiWrapper<>(meterReadingService.findAll(filter, pageable), localize("responses.reading.list"), HttpStatus.OK).toResponseEntity();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get meter reading by ID",
            description = "Retrieve detailed meter reading information including previous/current readings and calculated consumption."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Meter reading retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Meter reading not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<MeterReadingResponse>> findById(
            @PathVariable
            UUID id
    ) {
        return new ApiWrapper<>(meterReadingService.findById(id), localize("responses.reading.get"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Capture a meter reading",
            description = "Record a new meter reading for consumption tracking. Requires ADMIN or OPERATOR role. Readings must be ordered chronologically and cannot go backward."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Meter reading recorded successfully", content = @Content(schema = @Schema(implementation = MeterReadingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed - invalid readings, meter not found, or duplicate reading"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN or OPERATOR role required"),
            @ApiResponse(responseCode = "404", description = "Meter not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<MeterReadingResponse>> capture(
            @RequestBody
            @Valid MeterReadingRequest request
    ) {
        return new ApiWrapper<>(meterReadingService.capture(request), localize("responses.reading.create"), HttpStatus.CREATED).toResponseEntity();
    }

    private String localize(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
