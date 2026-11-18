package com.sixflags.parkmanager.modelo;

import java.math.BigDecimal;

public class BoletoGeneral extends Boleto {

    @Override
    public BigDecimal getPrecioBase() {
        return new BigDecimal("900.00");
    }

    @Override
    public TipoBoleto getTipo() {
        return TipoBoleto.GENERAL;
    }
}
