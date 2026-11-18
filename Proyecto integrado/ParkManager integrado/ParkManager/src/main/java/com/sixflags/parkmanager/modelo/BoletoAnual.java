package com.sixflags.parkmanager.modelo;

import java.math.BigDecimal;

public class BoletoAnual extends Boleto {

    @Override
    public BigDecimal getPrecioBase() {
        return new BigDecimal("2400.00");
    }

    @Override
    public TipoBoleto getTipo() {
        return TipoBoleto.PASE_ANUAL;
    }
}
