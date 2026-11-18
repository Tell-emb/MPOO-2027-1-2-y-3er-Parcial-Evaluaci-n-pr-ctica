package com.sixflags.parkmanager.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DescuentoInvalidoException.class)
    public ResponseEntity<ErrorRespuesta> manejarDescuento(
            DescuentoInvalidoException ex) {
        return crearError("DESCUENTO_INVALIDO", ex.getMessage());
    }

    @ExceptionHandler(CantidadInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> manejarCantidad(
            CantidadInvalidaException ex) {
        return crearError("CANTIDAD_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(TipoBoletoInvalidoException.class)
    public ResponseEntity<ErrorRespuesta> manejarTipo(
            TipoBoletoInvalidoException ex) {
        return crearError("TIPO_BOLETO_INVALIDO", ex.getMessage());
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> manejarSolicitud(
            SolicitudInvalidaException ex) {
        return crearError("SOLICITUD_INVALIDA", ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> manejarJsonInvalido(
            HttpMessageNotReadableException ex) {
        return crearError("SOLICITUD_INVALIDA",
                "El cuerpo de la solicitud está vacío o tiene un formato JSON inválido");
    }

    private ResponseEntity<ErrorRespuesta> crearError(
            String codigo, String mensaje) {
        ErrorRespuesta error = new ErrorRespuesta(codigo, mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
