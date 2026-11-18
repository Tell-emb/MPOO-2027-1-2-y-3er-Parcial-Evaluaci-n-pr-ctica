package com.sixflags.parkmanager.servicio;

import com.sixflags.parkmanager.excepciones.CantidadInvalidaException;
import com.sixflags.parkmanager.excepciones.DescuentoInvalidoException;
import com.sixflags.parkmanager.excepciones.SolicitudInvalidaException;
import com.sixflags.parkmanager.modelo.Boleto;
import com.sixflags.parkmanager.modelo.Compra;
import com.sixflags.parkmanager.modelo.FabricaBoletos;
import com.sixflags.parkmanager.modelo.TipoBoleto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

@Service
public class ParkManagerService {

    private final Map<TipoBoleto, BigDecimal> descuentos =
            new EnumMap<>(TipoBoleto.class);

    private int boletosVendidos = 0;
    private BigDecimal ingresosAcumulados = BigDecimal.ZERO;

    public ParkManagerService() {
        for (TipoBoleto tipo : TipoBoleto.values()) {
            descuentos.put(tipo, BigDecimal.ZERO);
        }
    }

    public void configurarDescuento(TipoBoleto tipo, BigDecimal porcentaje) {
        if (tipo == null) {
            throw new SolicitudInvalidaException("El tipo de boleto es obligatorio");
        }

        if (porcentaje == null
                || porcentaje.compareTo(BigDecimal.ZERO) < 0
                || porcentaje.compareTo(new BigDecimal("100")) > 0) {
            throw new DescuentoInvalidoException(
                    "El porcentaje debe estar entre 0 y 100");
        }

        descuentos.put(tipo, porcentaje);
    }

    public void eliminarDescuento(TipoBoleto tipo) {
        if (tipo == null) {
            throw new SolicitudInvalidaException("El tipo de boleto es obligatorio");
        }

        descuentos.put(tipo, BigDecimal.ZERO);
    }

    public Compra registrarCompra(TipoBoleto tipo, int cantidad) {
        if (tipo == null) {
            throw new SolicitudInvalidaException("El tipo de boleto es obligatorio");
        }

        if (cantidad < 1 || cantidad > 10) {
            throw new CantidadInvalidaException(
                    "La cantidad debe estar entre 1 y 10");
        }

        Boleto boleto = FabricaBoletos.crear(tipo);
        BigDecimal precioBase = boleto.getPrecioBase();
        BigDecimal descuento = descuentos.get(tipo);

        BigDecimal factorDescuento = BigDecimal.ONE.subtract(
                descuento.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));

        BigDecimal precioFinal = precioBase.multiply(factorDescuento)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = precioFinal.multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);

        Compra compra = new Compra(
                tipo, cantidad, precioBase, descuento, precioFinal, total);

        boletosVendidos += cantidad;
        ingresosAcumulados = ingresosAcumulados.add(total);

        return compra;
    }

    public int getBoletosVendidos() {
        return boletosVendidos;
    }

    public BigDecimal getIngresosAcumulados() {
        return ingresosAcumulados;
    }
}
