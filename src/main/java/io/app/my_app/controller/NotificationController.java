package io.app.my_app.controller;

import io.app.my_app.model.domain.ApiWrapper;
import io.app.my_app.model.dtos.notification.NotificationFilter;
import io.app.my_app.model.dtos.notification.NotificationResponse;
import io.app.my_app.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "System notifications and alerts")
public class NotificationController {

    private final NotificationService notificationService;
    private final MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCE')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "List all notifications",
            description = "Retrieve a paginated list of notifications with optional filtering by recipient, type, or read status. Requires ADMIN or FINANCE role."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or filter parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ApiWrapper<Page<NotificationResponse>>> findAll(
            @ParameterObject NotificationFilter filter,
            @ParameterObject @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return new ApiWrapper<>(notificationService.findAll(filter, pageable), localize("responses.notification.list"), HttpStatus.OK).toResponseEntity();
    }

    private String localize(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
