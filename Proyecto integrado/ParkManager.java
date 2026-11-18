
package com.sixflags.parkmanager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ===============================
// APLICACION PRINCIPAL
// ===============================

@SpringBootApplication
public class ParkManager {

    public static void main(String[] args) {
        SpringApplication.run(ParkManager.class, args);
    }
}

// ===============================
// MODALIDADES DE BOLETOS
// ===============================

enum TipoBoleto {
    GENERAL,
    PASE_ANUAL,
    ALUMNO,
    PROFESOR
}

// ===============================
// CLASE ABSTRACTA BOLETO
// ===============================

abstract class Boleto {

    private final BigDecimal precioBase;

    public Boleto(BigDecimal precioBase) {
        this.precioBase = precioBase;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public abstract TipoBoleto getTipo();
}

// ===============================
// CLASES HIJAS
// ===============================

class BoletoGeneral extends Boleto {

    public BoletoGeneral() {
        super(new BigDecimal("900.00"));
    }

    @Override
    public TipoBoleto getTipo() {
        return TipoBoleto.GENERAL;
    }
}

class BoletoAnual extends Boleto {

    public BoletoAnual() {
        super(new BigDecimal("2400.00"));
    }

    @Override
    public TipoBoleto getTipo() {
        return TipoBoleto.PASE_ANUAL;
    }
}

class BoletoAlumno extends Boleto {

    public BoletoAlumno() {
        super(new BigDecimal("650.00"));
    }

    @Override
    public TipoBoleto getTipo() {
        return TipoBoleto.ALUMNO;
    }
}

class BoletoProfesor extends Boleto {

    public BoletoProfesor() {
        super(new BigDecimal("700.00"));
    }

    @Override
    public TipoBoleto getTipo() {
        return TipoBoleto.PROFESOR;
    }
}

// ===============================
// FACTORIA DE BOLETOS
// ===============================

class FabricaBoletos {

    public static Boleto crear(TipoBoleto tipo) {

        switch (tipo) {
            case GENERAL:
                return new BoletoGeneral();

            case PASE_ANUAL:
                return new BoletoAnual();

            case ALUMNO:
                return new BoletoAlumno();

            case PROFESOR:
                return new BoletoProfesor();

            default:
                throw new TipoBoletoInvalidoException(
                        "Tipo de boleto inválido");
        }
    }
}

// ===============================
// CLASE COMPRA
// ===============================

class Compra {

    private final TipoBoleto tipo;
    private final int cantidad;
    private final BigDecimal precioBase;
    private final BigDecimal descuentoAplicado;
    private final BigDecimal precioUnitarioFinal;
    private final BigDecimal total;

    public Compra(TipoBoleto tipo, int cantidad,
                  BigDecimal precioBase,
                  BigDecimal descuentoAplicado,
                  BigDecimal precioUnitarioFinal,
                  BigDecimal total) {

        this.tipo = tipo;
        this.cantidad = cantidad;
        this.precioBase = precioBase;
        this.descuentoAplicado = descuentoAplicado;
        this.precioUnitarioFinal = precioUnitarioFinal;
        this.total = total;
    }

    public TipoBoleto getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioBase() {
        return precioBase;
    }

    public BigDecimal getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public BigDecimal getPrecioUnitarioFinal() {
        return precioUnitarioFinal;
    }

    public BigDecimal getTotal() {
        return total;
    }
}

// ===============================
// EXCEPCIONES PERSONALIZADAS
// ===============================

class DescuentoInvalidoException extends RuntimeException {

    public DescuentoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

class CantidadInvalidaException extends RuntimeException {

    public CantidadInvalidaException(String mensaje) {
        super(mensaje);
    }
}

class TipoBoletoInvalidoException extends RuntimeException {

    public TipoBoletoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

class SolicitudInvalidaException extends RuntimeException {

    public SolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}

// ===============================
// RESPUESTA DE ERROR
// ===============================

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

// ===============================
// SERVICIO PARKMANAGER
// ===============================

class ParkManagerService {

    private final Map<TipoBoleto, BigDecimal> descuentos =
            new EnumMap<>(TipoBoleto.class);

    private int boletosVendidos = 0;

    private BigDecimal ingresosAcumulados = BigDecimal.ZERO;

    public ParkManagerService() {

        for (TipoBoleto tipo : TipoBoleto.values()) {
            descuentos.put(tipo, BigDecimal.ZERO);
        }
    }

    public void configurarDescuento(
            TipoBoleto tipo, BigDecimal porcentaje) {

        if (porcentaje == null ||
            porcentaje.compareTo(BigDecimal.ZERO) < 0 ||
            porcentaje.compareTo(new BigDecimal("100")) > 0) {

            throw new DescuentoInvalidoException(
                    "El porcentaje debe estar entre 0 y 100");
        }

        descuentos.put(tipo, porcentaje);
    }

    public void eliminarDescuento(TipoBoleto tipo) {
        descuentos.put(tipo, BigDecimal.ZERO);
    }

    public Compra registrarCompra(TipoBoleto tipo, int cantidad) {

        if (tipo == null) {
            throw new SolicitudInvalidaException(
                    "El tipo de boleto es obligatorio");
        }

        if (cantidad < 1 || cantidad > 10) {
            throw new CantidadInvalidaException(
                    "La cantidad debe estar entre 1 y 10");
        }

        Boleto boleto = FabricaBoletos.crear(tipo);

        BigDecimal precioBase = boleto.getPrecioBase();

        BigDecimal descuento = descuentos.get(tipo);

        BigDecimal precioFinal = precioBase.multiply(
                BigDecimal.ONE.subtract(
                        descuento.divide(
                                new BigDecimal("100"),
                                4,
                                RoundingMode.HALF_UP)));

        precioFinal = precioFinal.setScale(
                2, RoundingMode.HALF_UP);

        BigDecimal total = precioFinal.multiply(
                BigDecimal.valueOf(cantidad));

        total = total.setScale(2, RoundingMode.HALF_UP);

        Compra compra = new Compra(
                tipo,
                cantidad,
                precioBase,
                descuento,
                precioFinal,
                total);

        boletosVendidos += cantidad;

        ingresosAcumulados =
                ingresosAcumulados.add(total);

        return compra;
    }

    public int getBoletosVendidos() {
        return boletosVendidos;
    }

    public BigDecimal getIngresosAcumulados() {
        return ingresosAcumulados;
    }
}

// ===============================
// SOLICITUDES JSON
// ===============================

class DescuentoRequest {

    private BigDecimal porcentaje;

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }
}

class CompraRequest {

    private String tipo;
    private Integer cantidad;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}

// ===============================
// CONTROLADOR REST
// ===============================

@RestController
@RequestMapping("/api")
class ServiciosREST {

    private final ParkManagerService servicio =
            new ParkManagerService();

    private TipoBoleto convertirTipo(String tipo) {

        try {
            if (tipo == null) {
                throw new IllegalArgumentException();
            }

            return TipoBoleto.valueOf(tipo);

        } catch (IllegalArgumentException ex) {
            throw new TipoBoletoInvalidoException(
                    "El tipo de boleto no es válido");
        }
    }

    @PutMapping("/descuentos/{tipo}")
    public ResponseEntity<Map<String, Object>>
    configurarDescuento(
            @PathVariable String tipo,
            @RequestBody(required = false)
            DescuentoRequest solicitud) {

        if (solicitud == null ||
            solicitud.getPorcentaje() == null) {

            throw new SolicitudInvalidaException(
                    "El porcentaje es obligatorio");
        }

        TipoBoleto modalidad = convertirTipo(tipo);

        servicio.configurarDescuento(
                modalidad, solicitud.getPorcentaje());

        return ResponseEntity.ok(Map.of(
                "tipo", modalidad,
                "porcentaje", solicitud.getPorcentaje(),
                "mensaje", "Descuento configurado"
        ));
    }

    @DeleteMapping("/descuentos/{tipo}")
    public ResponseEntity<Map<String, String>>
    eliminarDescuento(@PathVariable String tipo) {

        TipoBoleto modalidad = convertirTipo(tipo);

        servicio.eliminarDescuento(modalidad);

        return ResponseEntity.ok(Map.of(
                "tipo", modalidad.name(),
                "mensaje", "Descuento eliminado"
        ));
    }

    @PostMapping("/compras")
    public ResponseEntity<Map<String, Object>>
    registrarCompra(
            @RequestBody(required = false)
            CompraRequest solicitud) {

        if (solicitud == null ||
            solicitud.getTipo() == null ||
            solicitud.getCantidad() == null) {

            throw new SolicitudInvalidaException(
                    "La solicitud debe incluir tipo y cantidad");
        }

        TipoBoleto tipo = convertirTipo(solicitud.getTipo());

        Compra compra = servicio.registrarCompra(
                tipo, solicitud.getCantidad());

        return ResponseEntity.ok(Map.of(
                "tipo", compra.getTipo(),
                "cantidad", compra.getCantidad(),
                "precioBase", compra.getPrecioBase(),
                "descuentoAplicado",
                        compra.getDescuentoAplicado(),
                "precioUnitarioFinal",
                        compra.getPrecioUnitarioFinal(),
                "total", compra.getTotal(),
                "boletosVendidos",
                        servicio.getBoletosVendidos(),
                "ingresosAcumulados",
                        servicio.getIngresosAcumulados()
        ));
    }
}

// ===============================
// MANEJADOR GLOBAL DE ERRORES
// ===============================

@RestControllerAdvice
class GlobalExceptionHandler {

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

    private ResponseEntity<ErrorRespuesta> crearError(
            String codigo, String mensaje) {

        ErrorRespuesta error =
                new ErrorRespuesta(codigo, mensaje);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
}
