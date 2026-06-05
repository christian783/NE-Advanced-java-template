package io.app.my_app.controller;

import io.app.my_app.model.domain.ApiWrapper;
import io.app.my_app.model.dtos.billing.BillFilter;
import io.app.my_app.model.dtos.billing.BillGenerateRequest;
import io.app.my_app.model.dtos.billing.BillResponse;
import io.app.my_app.service.BillService;
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
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
@Tag(name = "Bills", description = "Bill generation, approval, and management")
public class BillController {

    private final BillService billService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "List all bills",
            description = "Retrieve a paginated list of bills with optional filtering by customer, meter, status, or billing period. Requires ADMIN or FINANCE role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bills retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<Page<BillResponse>>> findAll(
            @ParameterObject BillFilter filter,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiWrapper<>(billService.findAll(filter, pageable), localize("responses.bill.list"), HttpStatus.OK).toResponseEntity();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE','CUSTOMER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get bill by ID",
            description = "Retrieve detailed bill information including consumption, charges, VAT, penalties, and payment status. CUSTOMER role can only view their own bills."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bill retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions or trying to access another customer's bill"),
            @ApiResponse(responseCode = "404", description = "Bill not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<BillResponse>> findById(
            @PathVariable
            UUID id
    ) {
        return new ApiWrapper<>(billService.findById(id), localize("responses.bill.get"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Generate a new bill",
            description = "Generate a bill from a meter reading. Calculates charges based on consumption, tariff, VAT, and penalties. Bill starts in DRAFT status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bill generated successfully", content = @Content(schema = @Schema(implementation = BillResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed - invalid reading, bill already exists for period, or missing fields"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN or FINANCE role required"),
            @ApiResponse(responseCode = "404", description = "Meter reading not found or no applicable tariff"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<BillResponse>> generate(
            @RequestBody
            @Valid BillGenerateRequest request
    ) {
        return new ApiWrapper<>(billService.generate(request), localize("responses.bill.generate"), HttpStatus.CREATED).toResponseEntity();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Approve a bill",
            description = "Move bill from DRAFT to APPROVED status. Only DRAFT bills can be approved. Approved bills can be sent to customers."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bill approved successfully"),
            @ApiResponse(responseCode = "400", description = "Bill is not in DRAFT status or validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN or FINANCE role required"),
            @ApiResponse(responseCode = "404", description = "Bill not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<BillResponse>> approve(
            @PathVariable
            UUID id
    ) {
        return new ApiWrapper<>(billService.approve(id), localize("responses.bill.approve"), HttpStatus.OK).toResponseEntity();
    }

    private String localize(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
