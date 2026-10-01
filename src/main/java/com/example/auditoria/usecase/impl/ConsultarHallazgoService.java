package com.example.auditoria.usecase.impl;

import com.example.auditoria.adapter.in.web.dto.HallazgoResponse;
import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.ConsultarHallazgoUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.List;

public class ConsultarHallazgoService implements ConsultarHallazgoUseCase {
    private final HallazgoRepositoryPort repo;

    public ConsultarHallazgoService(HallazgoRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public HallazgoResponse buscarPorId(HallazgoId id) {
        return repo.buscarPorId(id)
                .map(this::toResponse)
                .orElseThrow(() -> new HallazgoNotFoundException(id));
    }

    @Override
    public List<HallazgoResponse> listarTodos() {
        return repo.buscarTodos().stream()
                .map(this::toResponse)
                .toList();
    }

    private HallazgoResponse toResponse(HallazgoAuditoria h) {
        String resp = h.getPlanRemediacion() != null ? h.getPlanRemediacion().responsable() : null;
        var limite = h.getPlanRemediacion() != null ? h.getPlanRemediacion().fechaLimite() : null;
        String notas = h.getPlanRemediacion() != null ? h.getPlanRemediacion().notas() : null;

        return new HallazgoResponse(
                h.getId().toString(),
                h.getTitulo(),
                h.getDescripcion(),
                h.getAreaResponsable(),
                h.getSeveridad().toString(),
                h.getEstado().toString(),
                h.getFechaDeteccion(),
                h.getFechaCierre(),
                resp,
                limite,
                notas
        );
    }
}