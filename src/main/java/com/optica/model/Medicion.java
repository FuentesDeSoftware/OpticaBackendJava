package com.optica.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Receta oftalmológica: OD = ojo derecho, OI = ojo izquierdo, DIP = distancia interpupilar.
 */
public class Medicion {

    private Long id;
    private Long clienteId;
    private Long vendedorId;
    private LocalDate fecha;
    private BigDecimal odEsfera;
    private BigDecimal odCilindro;
    private Integer odEje;
    private BigDecimal odAdicion;
    private BigDecimal oiEsfera;
    private BigDecimal oiCilindro;
    private Integer oiEje;
    private BigDecimal oiAdicion;
    private BigDecimal dip;
    private String observaciones;

    public Medicion() {
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getOdEsfera() {
        return odEsfera;
    }

    public void setOdEsfera(BigDecimal odEsfera) {
        this.odEsfera = odEsfera;
    }

    public BigDecimal getOdCilindro() {
        return odCilindro;
    }

    public void setOdCilindro(BigDecimal odCilindro) {
        this.odCilindro = odCilindro;
    }

    public Integer getOdEje() {
        return odEje;
    }

    public void setOdEje(Integer odEje) {
        this.odEje = odEje;
    }

    public BigDecimal getOdAdicion() {
        return odAdicion;
    }

    public void setOdAdicion(BigDecimal odAdicion) {
        this.odAdicion = odAdicion;
    }

    public BigDecimal getOiEsfera() {
        return oiEsfera;
    }

    public void setOiEsfera(BigDecimal oiEsfera) {
        this.oiEsfera = oiEsfera;
    }

    public BigDecimal getOiCilindro() {
        return oiCilindro;
    }

    public void setOiCilindro(BigDecimal oiCilindro) {
        this.oiCilindro = oiCilindro;
    }

    public Integer getOiEje() {
        return oiEje;
    }

    public void setOiEje(Integer oiEje) {
        this.oiEje = oiEje;
    }

    public BigDecimal getOiAdicion() {
        return oiAdicion;
    }

    public void setOiAdicion(BigDecimal oiAdicion) {
        this.oiAdicion = oiAdicion;
    }

    public BigDecimal getDip() {
        return dip;
    }

    public void setDip(BigDecimal dip) {
        this.dip = dip;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
