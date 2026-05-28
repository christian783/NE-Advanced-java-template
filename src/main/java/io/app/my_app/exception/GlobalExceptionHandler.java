package io.app.my_app.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import io.app.my_app.model.domain.ApiWrapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        String message = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                messageSource.getMessage("exceptions.resourceNotFound", null, "Resource not found", request.getLocale()),
                request.getLocale());

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("code", ex.getMessage());
        if (ex.getArgs() != null && ex.getArgs().length > 0) {
            error.put("args", Arrays.asList(ex.getArgs()));
        }

        return new ApiWrapper<String>(null, message, error, HttpStatus.NOT_FOUND).toResponseEntity();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> badRequestAlertException(BadRequestException ex, WebRequest request) {
        String message;
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("detail", ex.getMessage());
        try {
            message = messageSource.getMessage(ex.getMessage(), ex.getArgs(), ex.getMessage(), request.getLocale());
            error.put("code", ex.getMessage());
            if (ex.getArgs() != null && ex.getArgs().length > 0) {
                error.put("args", Arrays.asList(ex.getArgs()));
            }
        } catch (Exception e) {
            message = ex.getMessage();
            if (ex.getArgs() != null && ex.getArgs().length > 0) {
                error.put("args", Arrays.asList(ex.getArgs()));
            }
            log.debug("Message key not found in i18n files, using message directly: {}", ex.getMessage());
        }
        return new ApiWrapper<>(null, message, error, HttpStatus.BAD_REQUEST).toResponseEntity();
    }

    @ExceptionHandler(DuplicateRecordException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?> duplicateRecordException(DuplicateRecordException ex, WebRequest request) {
        Map<String, Object> error = new LinkedHashMap<>();
        String message;
        try {
            message = messageSource.getMessage(ex.getMessage(), ex.getArgs(), ex.getMessage(), request.getLocale());
            error.put("code", ex.getMessage());
            if (ex.getArgs() != null && ex.getArgs().length > 0) {
                error.put("args", Arrays.asList(ex.getArgs()));
            }
        } catch (Exception e) {
            message = ex.getMessage();
            error.put("detail", ex.getMessage());
            if (ex.getArgs() != null && ex.getArgs().length > 0) {
                error.put("args", Arrays.asList(ex.getArgs()));
            }
        }
        return new ApiWrapper<>(null, message, error, HttpStatus.CONFLICT).toResponseEntity();
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (org.springframework.validation.ObjectError error : ex.getBindingResult().getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                Class<?> fieldType = resolveFieldType(ex.getBindingResult(), ex.getTarget(), fieldError);
                if (fieldType == null) {
                    fieldType = resolveEnumTypeFromMessage(fieldError.getDefaultMessage());
                }
                if (fieldType != null && fieldType.isEnum() && fieldError.getRejectedValue() != null) {
                    return invalidEnumValueResponse(
                            fieldError.getField(),
                            String.valueOf(fieldError.getRejectedValue()),
                            fieldType,
                            request
                    );
                }
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        }
        String message = messageSource.getMessage("validation.invalid.inputs", null, request.getLocale());
        return new ApiWrapper<>(null, message, errors, HttpStatus.BAD_REQUEST).toResponseEntity();
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleBindException(BindException ex, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getFieldErrors()) {
            Class<?> fieldType = resolveFieldType(ex.getBindingResult(), ex.getTarget(), fieldError);
            if (fieldType == null) {
                fieldType = resolveEnumTypeFromMessage(fieldError.getDefaultMessage());
            }
            if (fieldType != null && fieldType.isEnum() && fieldError.getRejectedValue() != null) {
                return invalidEnumValueResponse(
                        fieldError.getField(),
                        String.valueOf(fieldError.getRejectedValue()),
                        fieldType,
                        request
                );
            }
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        String message = messageSource.getMessage("validation.invalid.inputs", null, request.getLocale());
        return new ApiWrapper<>(null, message, errors, HttpStatus.BAD_REQUEST).toResponseEntity();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        InvalidFormatException invalidFormatException = findCause(ex, InvalidFormatException.class);
        if (invalidFormatException != null && invalidFormatException.getTargetType() != null
                && invalidFormatException.getTargetType().isEnum()) {
            return invalidEnumValueResponse(
                    resolveFieldName(invalidFormatException),
                    String.valueOf(invalidFormatException.getValue()),
                    invalidFormatException.getTargetType(),
                    request
            );
        }

        String message = messageSource.getMessage("exceptions.malformed.json", null, request.getLocale());
        return new ApiWrapper<>(null, message, ex.getMessage(), HttpStatus.BAD_REQUEST).toResponseEntity();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            return invalidEnumValueResponse(
                    ex.getName(),
                    String.valueOf(ex.getValue()),
                    ex.getRequiredType(),
                    request
            );
        }

        String message = messageSource.getMessage("validation.invalid.inputs", null, request.getLocale());
        Map<String, Object> error = new LinkedHashMap<>();
        error.put(ex.getName(), ex.getMessage());
        return new ApiWrapper<>(null, message, error, HttpStatus.BAD_REQUEST).toResponseEntity();
    }

    @ExceptionHandler(UserLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> userLoginException(UserLoginException ex) {
        return new ApiWrapper<>(null, ex.getMessage(), HttpStatus.UNAUTHORIZED).toResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> exceptionHandler(Exception ex, WebRequest request) {
        String message;
        String error = ex.getMessage();
        HttpStatus status;
        String exceptionType = ex.getClass().getSimpleName();

        if (exceptionType.equals("InternalAuthenticationServiceException")) {
            status = HttpStatus.UNAUTHORIZED;
            message = messageSource.getMessage("exceptions.unauthorized", null, request.getLocale());
            log.warn("Authentication exception: {}", ex.getMessage());
        } else if (exceptionType.equals("HttpMessageNotReadableException")) {
            status = HttpStatus.BAD_REQUEST;
            message = messageSource.getMessage("exceptions.malformed.json", null, request.getLocale());
            log.warn("Malformed JSON request: {}", ex.getMessage());
        } else {
            // Default to 500 for all unrecognizable errors
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = messageSource.getMessage("exceptions.internal.server.error", null, request.getLocale());
            log.error("Unhandled exception of type {}: {}", exceptionType, ex.getMessage(), ex);
        }

        return new ApiWrapper<>(null, message, error, status).toResponseEntity();
    }

    private ResponseEntity<?> invalidEnumValueResponse(String fieldName, String rejectedValue, Class<?> enumType, WebRequest request) {
        String allowedValues = Arrays.stream(enumType.getEnumConstants())
                .filter(Objects::nonNull)
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String message = messageSource.getMessage(
                "validation.invalid.enum.value",
                new Object[]{fieldName, allowedValues},
                request.getLocale()
        );

        Map<String, Object> error = new LinkedHashMap<>();
        error.put(fieldName, message);
        error.put("rejectedValue", rejectedValue);
        error.put("allowedValues", allowedValues);

        return new ApiWrapper<>(null, message, error, HttpStatus.BAD_REQUEST).toResponseEntity();
    }

    private String resolveFieldName(InvalidFormatException ex) {
        if (ex.getPath() == null || ex.getPath().isEmpty()) {
            return "value";
        }

        JsonMappingException.Reference lastReference = ex.getPath().get(ex.getPath().size() - 1);
        String fieldName = lastReference.getFieldName();
        if (fieldName != null) {
            return fieldName;
        }

        if (lastReference.getIndex() >= 0) {
            return String.valueOf(lastReference.getIndex());
        }

        return "value";
    }

    private Class<?> resolveFieldType(org.springframework.validation.BindingResult bindingResult, Object target, FieldError fieldError) {
        if (bindingResult != null && fieldError != null) {
            Class<?> fieldType = bindingResult.getFieldType(fieldError.getField());
            if (fieldType != null) {
                return fieldType;
            }
        }

        if (fieldError != null) {
            Class<?> resolvedFromMessage = resolveEnumTypeFromMessage(fieldError.getDefaultMessage());
            if (resolvedFromMessage != null) {
                return resolvedFromMessage;
            }
        }

        return resolveFieldType(target, fieldError != null ? fieldError.getField() : null);
    }

    private Class<?> resolveFieldType(Object target, String fieldName) {
        if (target == null || fieldName == null || fieldName.isBlank()) {
            return null;
        }

        Class<?> currentType = target.getClass();
        String[] path = fieldName.split("\\.");
        for (String pathSegment : path) {
            try {
                Field field = currentType.getDeclaredField(pathSegment);
                currentType = field.getType();
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
        return currentType;
    }

    private Class<?> resolveEnumTypeFromMessage(String message) {
        if (message == null || message.isBlank()) {
            return null;
        }

        Pattern pattern = Pattern.compile("required type '([^']+)'");
        Matcher matcher = pattern.matcher(message);
        if (!matcher.find()) {
            return null;
        }

        String className = matcher.group(1);
        try {
            Class<?> resolvedType = Class.forName(className);
            return resolvedType.isEnum() ? resolvedType : null;
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private <T extends Throwable> T findCause(Throwable throwable, Class<T> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }

    @ExceptionHandler(LazyInitializationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<?> lazyInitializationException(LazyInitializationException ex, WebRequest request) {
        log.error("Lazy initialization error: {}", ex.getMessage(), ex);
        String message = messageSource.getMessage("exceptions.data.loading.error", null, "Error loading related data", request.getLocale());
        return new ApiWrapper<>(null, message, "Unable to load associated data", HttpStatus.INTERNAL_SERVER_ERROR).toResponseEntity();
    }
}
