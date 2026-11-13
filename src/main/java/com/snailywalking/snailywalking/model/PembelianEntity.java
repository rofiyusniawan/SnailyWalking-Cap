
package com.snailywalking.snailywalking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "pembelian")
public class PembelianEntity {

    @Id
    @Column(name = "kode")
    private String kode;

    @Column(name = "tanggal")
    private String tanggal;

    @Column(name = "kode_supplier")
    private String kodeSupplier;

    @Column(name = "kode_topi")
    private String kodeTopi;

    @Column(name = "jumlah")
    private int jumlah;

    @Column(name = "harga_modal")
    private double hargaModal;

    public PembelianEntity() {
    }

    public PembelianEntity(String kode, String tanggal,
            String kodeSupplier, String kodeTopi,
            int jumlah, double hargaModal) {

        this.kode = kode;
        this.tanggal = tanggal;
        this.kodeSupplier = kodeSupplier;
        this.kodeTopi = kodeTopi;
        this.jumlah = jumlah;
        this.hargaModal = hargaModal;
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

    public String getKodeSupplier() {
        return kodeSupplier;
    }

    public void setKodeSupplier(String kodeSupplier) {
        this.kodeSupplier = kodeSupplier;
    }

    public String getKodeTopi() {
        return kodeTopi;
    }

    public void setKodeTopi(String kodeTopi) {
        this.kodeTopi = kodeTopi;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public double getHargaModal() {
        return hargaModal;
    }

    public void setHargaModal(double hargaModal) {
        this.hargaModal = hargaModal;
    }

    public double getTotal() {
        return jumlah * hargaModal;
    }
}
