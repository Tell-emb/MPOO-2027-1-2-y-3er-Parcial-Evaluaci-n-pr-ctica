package com.sixflags.parkmanager.api;

import com.sixflags.parkmanager.excepciones.SolicitudInvalidaException;
import com.sixflags.parkmanager.excepciones.TipoBoletoInvalidoException;
import com.sixflags.parkmanager.modelo.Compra;
import com.sixflags.parkmanager.modelo.TipoBoleto;
import com.sixflags.parkmanager.servicio.ParkManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ServiciosREST {

    private final ParkManagerService servicio;

    public ServiciosREST(ParkManagerService servicio) {
        this.servicio = servicio;
    }

    private TipoBoleto convertirTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new TipoBoletoInvalidoException("El tipo de boleto es obligatorio");
        }

        try {
            return TipoBoleto.valueOf(tipo);
        } catch (IllegalArgumentException ex) {
            throw new TipoBoletoInvalidoException(
                    "El tipo de boleto no es válido");
        }
    }

    @PutMapping("/descuentos/{tipo}")
    public ResponseEntity<Map<String, Object>> configurarDescuento(
            @PathVariable String tipo,
            @RequestBody(required = false) DescuentoRequest solicitud) {

        if (solicitud == null || solicitud.getPorcentaje() == null) {
            throw new SolicitudInvalidaException("El porcentaje es obligatorio");
        }

        TipoBoleto modalidad = convertirTipo(tipo);
        servicio.configurarDescuento(modalidad, solicitud.getPorcentaje());

        return ResponseEntity.ok(Map.of(
                "tipo", modalidad,
                "porcentaje", solicitud.getPorcentaje(),
                "mensaje", "Descuento configurado"
        ));
    }

    @DeleteMapping("/descuentos/{tipo}")
    public ResponseEntity<Map<String, String>> eliminarDescuento(
            @PathVariable String tipo) {

        TipoBoleto modalidad = convertirTipo(tipo);
        servicio.eliminarDescuento(modalidad);

        return ResponseEntity.ok(Map.of(
                "tipo", modalidad.name(),
                "mensaje", "Descuento eliminado"
        ));
    }

    @PostMapping("/compras")
    public ResponseEntity<Map<String, Object>> registrarCompra(
            @RequestBody(required = false) CompraRequest solicitud) {

        if (solicitud == null
                || solicitud.getTipo() == null
                || solicitud.getCantidad() == null) {
            throw new SolicitudInvalidaException(
                    "La solicitud debe incluir tipo y cantidad");
        }

        TipoBoleto tipo = convertirTipo(solicitud.getTipo());
        Compra compra = servicio.registrarCompra(tipo, solicitud.getCantidad());

        return ResponseEntity.status(201).body(Map.of(
                "tipo", compra.getTipo(),
                "cantidad", compra.getCantidad(),
                "precioBase", compra.getPrecioBase(),
                "descuentoAplicado", compra.getDescuentoAplicado(),
                "precioUnitarioFinal", compra.getPrecioUnitarioFinal(),
                "total", compra.getTotal(),
                "ingresosAcumulados", servicio.getIngresosAcumulados()
        ));
    }
}
