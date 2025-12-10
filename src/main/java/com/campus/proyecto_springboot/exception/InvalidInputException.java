package com.campus.proyecto_springboot.exception;

public class InvalidInputException extends RuntimeException {

    // Constructor por defecto
    public InvalidInputException() {
        super("Entrada inválida.");
    }

    // Constructor con mensaje personalizado
    public InvalidInputException(String message) {
        super(message);
    }

    // Constructor con mensaje personalizado y causa
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor con causa
    public InvalidInputException(Throwable cause) {
        super(cause);
    }
}
