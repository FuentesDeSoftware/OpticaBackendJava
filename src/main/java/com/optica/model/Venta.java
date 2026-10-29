package com.optica.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cabecera de una venta. Asocia obligatoriamente cliente, medición, aro y luna.
 */
public class Venta {

    private Long id;
    private Long clienteId;
    private Long vendedorId;
    private Long medicionId;
    private Long aroId;
    private Long lunaId;
    private LocalDateTime fecha;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String clienteNombre;
    private String vendedorNombre;
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() {
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

    public Long getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Long vendedorId) {
        this.vendedorId = vendedorId;
    }

    public Long getMedicionId() {
        return medicionId;
    }

    public void setMedicionId(Long medicionId) {
        this.medicionId = medicionId;
    }

    public Long getAroId() {
        return aroId;
    }

    public void setAroId(Long aroId) {
        this.aroId = aroId;
    }

    public Long getLunaId() {
        return lunaId;
    }

    public void setLunaId(Long lunaId) {
        this.lunaId = lunaId;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIgv() {
        return igv;
    }

    public void setIgv(BigDecimal igv) {
        this.igv = igv;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public String getVendedorNombre() {
        return vendedorNombre;
    }

    public void setVendedorNombre(String vendedorNombre) {
        this.vendedorNombre = vendedorNombre;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}
