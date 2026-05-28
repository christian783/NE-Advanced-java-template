package io.app.my_app.model.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class ApiWrapper<T> {
    private final String timestamp = LocalDateTime.now().toString();
    private T data;
    private String message;
    private Object error;
    private HttpStatus status;

    public ApiWrapper(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.error = null;
    }

    public ApiWrapper(T data, String message, Object error, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
        this.error = error;
    }

    public ApiWrapper(T data, HttpStatus status) {
        this.data = data;
        this.message = null;
        this.status = status;
        this.error = null;
    }

    public ApiWrapper(T data){
        this.data = data;
        this.message = null;
        this.status = HttpStatus.OK;
        this.error = null;
    }

    public ApiWrapper(String message){
        this.message = message;
        this.status = HttpStatus.OK;
        this.error = null;
        this.data = null;
    }

    public ApiWrapper(String message, HttpStatus status){
        this.message = message;
        this.status = status;
        this.error = null;
        this.data = null;
    }

    public ResponseEntity<ApiWrapper<T>> toResponseEntity() {
        assert this.status != null;
        return ResponseEntity.status(this.status).body(this);
    }
}
