package com.snailywalking.snailywalking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "penjualan")
public class PenjualanEntity {

    @Id
    @Column(name = "kode")
    private String kode;

    @Column(name = "tanggal")
    private String tanggal;

    @Column(name = "kode_topi")
    private String kodeTopi;

    @Column(name = "nama_pembeli")
    private String namaPembeli;

    @Column(name = "harga_jual")
    private double hargaJual;

    @Column(name = "metode")
    private String metode;

    public PenjualanEntity() {
    }

    public PenjualanEntity(String kode, String tanggal, String kodeTopi,
            String namaPembeli, double hargaJual, String metode) {
        this.kode = kode;
        this.tanggal = tanggal;
        this.kodeTopi = kodeTopi;
        this.namaPembeli = namaPembeli;
        this.hargaJual = hargaJual;
        this.metode = metode;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKodeTopi() {
        return kodeTopi;
    }

    public void setKodeTopi(String kodeTopi) {
        this.kodeTopi = kodeTopi;
    }

    public String getNamaPembeli() {
        return namaPembeli;
    }

    public void setNamaPembeli(String namaPembeli) {
        this.namaPembeli = namaPembeli;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public String getMetode() {
        return metode;
    }

    public void setMetode(String metode) {
        this.metode = metode;
    }
}