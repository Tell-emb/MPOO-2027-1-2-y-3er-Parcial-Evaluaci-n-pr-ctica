package com.sixflags.parkmanager.modelo;

import java.math.BigDecimal;

public class Compra {

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
