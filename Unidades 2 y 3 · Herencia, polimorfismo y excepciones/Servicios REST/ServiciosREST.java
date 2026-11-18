
package com.sixflags.parkmanager;

import com.sixflags.errores.TipoBoletoInvalidoException;
import com.sixflags.errores.SolicitudInvalidaException;
import com.sixflags.parkmanager.model.Compra;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ServiciosREST {

    private final ParkManager parkManager;

    public ServiciosREST(ParkManager parkManager) {
        this.parkManager = parkManager;
    }

    @PutMapping("/descuentos/{tipo}")
    public ResponseEntity<Map<String, Object>> configurarDescuento(
            @PathVariable String tipo,
            @RequestBody Map<String, Object> solicitud) {

        TipoBoleto tipoBoleto = convertirTipo(tipo);

        Object porcentajeObjeto = solicitud.get("porcentaje");

        if (!(porcentajeObjeto instanceof Number)) {
            throw new SolicitudInvalidaException(
                    "El porcentaje debe ser un valor numerico.");
        }

        BigDecimal porcentaje;

        try {
            porcentaje = new BigDecimal(porcentajeObjeto.toString());
        } catch (NumberFormatException e) {
            throw new SolicitudInvalidaException(
                    "El porcentaje proporcionado no es valido.");
        }

        parkManager.configurarDescuento(tipoBoleto, porcentaje);

        return ResponseEntity.ok(Map.of(
                "tipo", tipoBoleto.name(),
                "porcentaje", porcentaje,
                "mensaje", "Descuento configurado correctamente."
        ));
    }

    @DeleteMapping("/descuentos/{tipo}")
    public ResponseEntity<Map<String, String>> eliminarDescuento(
            @PathVariable String tipo) {

        TipoBoleto tipoBoleto = convertirTipo(tipo);

        parkManager.eliminarDescuento(tipoBoleto);

        return ResponseEntity.ok(Map.of(
                "tipo", tipoBoleto.name(),
                "mensaje", "Descuento eliminado correctamente."
        ));
    }

    @PostMapping("/compras")
    public ResponseEntity<Map<String, Object>> registrarCompra(
            @RequestBody Map<String, Object> solicitud) {

        Object tipoObjeto = solicitud.get("tipo");
        Object cantidadObjeto = solicitud.get("cantidad");

        if (!(tipoObjeto instanceof String)) {
            throw new SolicitudInvalidaException(
                    "El tipo de boleto es obligatorio.");
        }

        if (!(cantidadObjeto instanceof Number)) {
            throw new SolicitudInvalidaException(
                    "La cantidad debe ser numerica.");
        }

        TipoBoleto tipo = convertirTipo((String) tipoObjeto);

        int cantidad;

        try {
            cantidad = new BigDecimal(cantidadObjeto.toString())
                    .intValueExact();
        } catch (ArithmeticException | NumberFormatException e) {
            throw new SolicitudInvalidaException(
                    "La cantidad debe ser un numero entero.");
        }

        Compra compra = parkManager.registrarCompra(tipo, cantidad);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "tipo", compra.getTipo().name(),
                "cantidad", compra.getCantidad(),
                "precioBase", compra.getPrecioBase(),
                "descuentoAplicado", compra.getDescuentoAplicado(),
                "precioUnitarioFinal", compra.getPrecioUnitarioFinal(),
                "total", compra.getTotal(),
                "boletosVendidos", parkManager.getBoletosVendidos(),
                "ingresosAcumulados", parkManager.getIngresosAcumulados()
        ));
    }

    private TipoBoleto convertirTipo(String tipo) {
        try {
            return TipoBoleto.valueOf(tipo.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new TipoBoletoInvalidoException(
                    "El tipo de boleto no existe.");
        }
    }
}
