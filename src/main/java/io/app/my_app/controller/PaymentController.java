package io.app.my_app.controller;

import io.app.my_app.model.domain.ApiWrapper;
import io.app.my_app.model.dtos.payment.PaymentFilter;
import io.app.my_app.model.dtos.payment.PaymentRequest;
import io.app.my_app.model.dtos.payment.PaymentResponse;
import io.app.my_app.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment recording and tracking for bills")
public class PaymentController {

    private final PaymentService paymentService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "List all payments",
            description = "Retrieve a paginated list of payments with optional filtering by customer, bill, payment method, or status. Requires ADMIN or FINANCE role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<Page<PaymentResponse>>> findAll(
            @ParameterObject PaymentFilter filter,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiWrapper<>(paymentService.findAll(filter, pageable), localize("responses.payment.list"), HttpStatus.OK).toResponseEntity();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Record a new payment",
            description = "Record a payment received from customer towards a bill. Validates payment amount, method, and bill status. Requires ADMIN or FINANCE role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment recorded successfully", content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed - invalid amount, duplicate reference number, or bill status issues"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN or FINANCE role required"),
            @ApiResponse(responseCode = "404", description = "Bill or customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<PaymentResponse>> record(@Valid @RequestBody PaymentRequest request) {
        return new ApiWrapper<>(paymentService.record(request), localize("responses.payment.create"), HttpStatus.CREATED).toResponseEntity();
    }

    private String localize(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
