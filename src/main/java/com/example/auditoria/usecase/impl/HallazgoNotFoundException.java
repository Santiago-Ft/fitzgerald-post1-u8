package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.valueobject.HallazgoId;

public class HallazgoNotFoundException extends RuntimeException {
    public HallazgoNotFoundException(HallazgoId id) {
        super("Hallazgo no encontrado con ID: " + id);
    }
}
