package com.ina.pos.receipt.exception;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CommonValidationException extends RuntimeException {

    private final String message;
    private final int error;

    public CommonValidationException(String message, int error) {
        super(message);
        this.message = message;
        this.error = error;
    }

}