package io.app.my_app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
@Getter
public class DuplicateRecordException extends RuntimeException {
    private Object[] args;

    public DuplicateRecordException(String message, Object... args) {
        super(message);
        this.args = args;
    }
}