
package com.snailywalking.snailywalking;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "supplier")
public class SupplierEntity {

    @Id
    @Column(name = "kode")
    private String kode;

    @Column(name = "nama")
    private String nama;

    @Column(name = "telepon")
    private String telepon;

    @Column(name = "lokasi")
    private String lokasi;

    @Column(name = "catatan")
    private String catatan;

    public SupplierEntity() {
    }

    public SupplierEntity(String kode, String nama, String telepon,
            String lokasi, String catatan) {
        this.kode = kode;
        this.nama = nama;
        this.telepon = telepon;
        this.lokasi = lokasi;
        this.catatan = catatan;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTelepon() {
        return telepon;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
}
