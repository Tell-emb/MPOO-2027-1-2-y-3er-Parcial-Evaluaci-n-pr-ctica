package com.sixflags.parkmanager.modelo;

import java.math.BigDecimal;

public abstract class Boleto {

    public abstract BigDecimal getPrecioBase();

    public abstract TipoBoleto getTipo();
}
