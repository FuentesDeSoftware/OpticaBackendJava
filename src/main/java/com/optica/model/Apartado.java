package com.optica.model;

import java.time.LocalDateTime;

/**
 * Aro reservado por un cliente para una cotización o compra posterior.
 */
public class Apartado {

    private Long id;
    private Long clienteId;
    private Long aroId;
    private LocalDateTime fecha;
    private EstadoApartado estado;
    private Aro aro;

    public Apartado() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getAroId() {
        return aroId;
    }

    public void setAroId(Long aroId) {
        this.aroId = aroId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoApartado getEstado() {
        return estado;
    }

    public void setEstado(EstadoApartado estado) {
        this.estado = estado;
    }

    public Aro getAro() {
        return aro;
    }

    public void setAro(Aro aro) {
        this.aro = aro;
    }
}
