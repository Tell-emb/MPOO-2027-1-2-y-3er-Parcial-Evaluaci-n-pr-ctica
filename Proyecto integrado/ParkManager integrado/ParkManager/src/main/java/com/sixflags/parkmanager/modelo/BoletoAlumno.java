package com.sixflags.parkmanager.modelo;

import java.math.BigDecimal;

public class BoletoAlumno extends Boleto {

    @Override
    public BigDecimal getPrecioBase() {
        return new BigDecimal("650.00");
    }

    @Override
    public TipoBoleto getTipo() {
        return TipoBoleto.ALUMNO;
    }
}
