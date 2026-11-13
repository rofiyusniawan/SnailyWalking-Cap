
package com.snailywalking.snailywalking.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "topi")
public class TopiEntity {

    @Id
    @Column(name = "kode")
    private String kode;

    @Column(name = "merek")
    private String merek;

    @Column(name = "model")
    private String model;

    @Column(name = "warna")
    private String warna;

    @Column(name = "kondisi")
    private String kondisi;

    @Column(name = "harga_modal")
    private double hargaModal;

    @Column(name = "harga_jual")
    private double hargaJual;

    @Column(name = "stok")
    private int stok;

    public TopiEntity() {
    }

    public TopiEntity(String kode, String merek, String model,
            String warna, String kondisi, double hargaModal,
            double hargaJual, int stok) {

        this.kode = kode;
        this.merek = merek;
        this.model = model;
        this.warna = warna;
        this.kondisi = kondisi;
        this.hargaModal = hargaModal;
        this.hargaJual = hargaJual;
        this.stok = stok;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getMerek() {
        return merek;
    }

    public void setMerek(String merek) {
        this.merek = merek;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getWarna() {
        return warna;
    }

    public void setWarna(String warna) {
        this.warna = warna;
    }

    public String getKondisi() {
        return kondisi;
    }

    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }

    public double getHargaModal() {
        return hargaModal;
    }

    public void setHargaModal(double hargaModal) {
        this.hargaModal = hargaModal;
    }

    public double getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(double hargaJual) {
        this.hargaJual = hargaJual;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public String getStatus() {
        return stok > 0 ? "Tersedia" : "Terjual";
    }
}
