package io.app.my_app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;
import java.util.Date;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2L;
    private final Date timestamp;
    private Object[] args;


    public BadRequestException(String message) {
        super(message);
        this.timestamp = new Date();
    }

    public BadRequestException(String message, Object... args) {
        super(message);
        this.timestamp = new Date();
        this.args = args;
    }

}