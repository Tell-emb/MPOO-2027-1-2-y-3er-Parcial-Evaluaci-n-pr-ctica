package com.sixflags.parkmanager.modelo;

import java.math.BigDecimal;

public class BoletoProfesor extends Boleto {

    @Override
    public BigDecimal getPrecioBase() {
        return new BigDecimal("700.00");
    }

    @Override
    public TipoBoleto getTipo() {
        return TipoBoleto.PROFESOR;
    }
}
