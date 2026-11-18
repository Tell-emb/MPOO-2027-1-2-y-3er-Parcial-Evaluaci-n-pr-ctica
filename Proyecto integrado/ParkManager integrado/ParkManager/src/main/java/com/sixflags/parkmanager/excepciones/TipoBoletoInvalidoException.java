package com.sixflags.parkmanager.excepciones;

public class TipoBoletoInvalidoException extends RuntimeException {

    public TipoBoletoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
