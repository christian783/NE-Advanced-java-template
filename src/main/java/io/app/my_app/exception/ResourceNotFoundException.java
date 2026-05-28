package io.app.my_app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Date;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final Date timestamp;
    private Object[] args;

    public ResourceNotFoundException(String message) {
        super(message);
        this.timestamp = new Date();
    }

    public ResourceNotFoundException(String message, Object... args) {
        super(message);
        this.timestamp = new Date();
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}