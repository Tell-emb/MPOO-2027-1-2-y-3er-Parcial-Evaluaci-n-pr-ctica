
package com.sixflags.parkmanager;

import com.sixflags.errores.CantidadInvalidaException;
import com.sixflags.errores.DescuentoInvalidoException;
import com.sixflags.errores.TipoBoletoInvalidoException;
import com.sixflags.parkmanager.model.Boleto;
import com.sixflags.parkmanager.model.Compra;
import com.sixflags.parkmanager.model.FabricaBoletos;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

@Service
public class ParkManager {

    private final Map<TipoBoleto, BigDecimal> descuentos =
            new EnumMap<>(TipoBoleto.class);

    private int boletosVendidos = 0;

    private BigDecimal ingresosAcumulados = BigDecimal.ZERO;

    public ParkManager() {
        for (TipoBoleto tipo : TipoBoleto.values()) {
            descuentos.put(tipo, BigDecimal.ZERO);
        }
    }

    public void configurarDescuento(
            TipoBoleto tipo,
            BigDecimal porcentaje) {

        if (tipo == null) {
            throw new TipoBoletoInvalidoException(
                    "El tipo de boleto no existe.");
        }

        if (porcentaje == null
                || porcentaje.compareTo(BigDecimal.ZERO) < 0
                || porcentaje.compareTo(new BigDecimal("100")) > 0) {

            throw new DescuentoInvalidoException(
                    "El descuento debe estar entre 0 y 100.");
        }

        descuentos.put(tipo, porcentaje);
    }

    public void eliminarDescuento(TipoBoleto tipo) {

        if (tipo == null || !descuentos.containsKey(tipo)) {
            throw new TipoBoletoInvalidoException(
                    "El tipo de boleto no existe.");
        }

        descuentos.put(tipo, BigDecimal.ZERO);
    }

    public Compra registrarCompra(
            TipoBoleto tipo,
            int cantidad) {

        if (tipo == null) {
            throw new TipoBoletoInvalidoException(
                    "El tipo de boleto no existe.");
        }

        if (cantidad < 1 || cantidad > 10) {
            throw new CantidadInvalidaException(
                    "La cantidad debe estar entre 1 y 10 boletos.");
        }

        Boleto boleto;

        try {
            boleto = FabricaBoletos.crear(tipo);
        } catch (IllegalArgumentException e) {
            throw new TipoBoletoInvalidoException(
                    "El tipo de boleto no existe.");
        }

        BigDecimal precioBase = boleto.getPrecioBase();
        BigDecimal descuento = descuentos.get(tipo);

        BigDecimal factorDescuento = BigDecimal.ONE.subtract(
                descuento.divide(new BigDecimal("100"), 4,
                        RoundingMode.HALF_UP));

        BigDecimal precioFinal = precioBase
                .multiply(factorDescuento)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = precioFinal
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);

        Compra compra = new Compra(
                tipo,
                cantidad,
                precioBase,
                descuento,
                precioFinal,
                total
        );

        boletosVendidos += cantidad;
        ingresosAcumulados = ingresosAcumulados
                .add(total)
                .setScale(2, RoundingMode.HALF_UP);

        return compra;
    }

    public int getBoletosVendidos() {
        return boletosVendidos;
    }

    public BigDecimal getIngresosAcumulados() {
        return ingresosAcumulados;
    }

    public BigDecimal consultarDescuento(TipoBoleto tipo) {
        return descuentos.get(tipo);
    }
}
