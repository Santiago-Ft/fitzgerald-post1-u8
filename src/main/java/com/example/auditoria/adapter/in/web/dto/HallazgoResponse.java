package com.example.auditoria.adapter.in.web.dto;

import java.time.LocalDate;

public record HallazgoResponse(
    String id,
    String titulo,
    String descripcion,
    String areaResponsable,
    String severidad,
    String estado,
    LocalDate fechaDeteccion,
    LocalDate fechaCierre,
    String planResponsable,
    LocalDate planFechaLimite,
    String planNotas
) {}
