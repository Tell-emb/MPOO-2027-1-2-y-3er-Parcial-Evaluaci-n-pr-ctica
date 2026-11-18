
package com.sixflags.errores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(DescuentoInvalidoException.class)
    public ResponseEntity<ErrorRespuesta> manejarDescuentoInvalido(
            DescuentoInvalidoException e) {

        return respuesta("DESCUENTO_INVALIDO", e.getMessage());
    }

    @ExceptionHandler(CantidadInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> manejarCantidadInvalida(
            CantidadInvalidaException e) {

        return respuesta("CANTIDAD_INVALIDA", e.getMessage());
    }

    @ExceptionHandler(TipoBoletoInvalidoException.class)
    public ResponseEntity<ErrorRespuesta> manejarTipoInvalido(
            TipoBoletoInvalidoException e) {

        return respuesta("TIPO_BOLETO_INVALIDO", e.getMessage());
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorRespuesta> manejarSolicitudInvalida(
            SolicitudInvalidaException e) {

        return respuesta("SOLICITUD_INVALIDA", e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorRespuesta> manejarJSONInvalido(
            HttpMessageNotReadableException e) {

        return respuesta(
                "SOLICITUD_INVALIDA",
                "El cuerpo de la solicitud no contiene un JSON valido."
        );
    }

    private ResponseEntity<ErrorRespuesta> respuesta(
            String codigo,
            String mensaje) {

        ErrorRespuesta error = new ErrorRespuesta(codigo, mensaje);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}

class ErrorRespuesta {

    private final String codigo;
    private final String mensaje;

    public ErrorRespuesta(String codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
