package com.sixflags.parkmanager.modelo;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class FabricaBoletos {

    private static final Map<TipoBoleto, Supplier<Boleto>> FABRICAS =
            new EnumMap<>(TipoBoleto.class);

    static {
        FABRICAS.put(TipoBoleto.GENERAL, BoletoGeneral::new);
        FABRICAS.put(TipoBoleto.PASE_ANUAL, BoletoAnual::new);
        FABRICAS.put(TipoBoleto.ALUMNO, BoletoAlumno::new);
        FABRICAS.put(TipoBoleto.PROFESOR, BoletoProfesor::new);
    }

    private FabricaBoletos() {
    }

    public static Boleto crear(TipoBoleto tipo) {
        Supplier<Boleto> fabrica = FABRICAS.get(tipo);

        if (fabrica == null) {
            throw new IllegalArgumentException("No existe una fábrica para ese tipo de boleto");
        }

        return fabrica.get();
    }
}
