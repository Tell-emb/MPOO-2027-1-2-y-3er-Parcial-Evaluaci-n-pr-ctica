package com.sixflags.parkmanager.excepciones;

public class CantidadInvalidaException extends RuntimeException {

    public CantidadInvalidaException(String mensaje) {
        super(mensaje);
    }
}
