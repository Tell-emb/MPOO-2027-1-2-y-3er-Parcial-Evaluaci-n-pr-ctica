package com.sixflags.parkmanager.excepciones;

public class DescuentoInvalidoException extends RuntimeException {

    public DescuentoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
