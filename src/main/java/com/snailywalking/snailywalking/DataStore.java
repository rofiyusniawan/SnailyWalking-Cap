package com.snailywalking.snailywalking;

import java.util.ArrayList;
import java.util.List;

public class DataStore {

    // CLASS UNTUK MENYIMPAN DATA TOPI
    public static class Topi {

        public String kode;
        public String merek;
        public String model;
        public String warna;
        public String kondisi;
        public double hargaModal;
        public double hargaJual;
        public int stok;

        public Topi(
                String kode,
                String merek,
                String model,
                String warna,
                String kondisi,
                double hargaModal,
                double hargaJual,
                int stok
        ) {
            this.kode = kode;
            this.merek = merek;
            this.model = model;
            this.warna = warna;
            this.kondisi = kondisi;
            this.hargaModal = hargaModal;
            this.hargaJual = hargaJual;
            this.stok = stok;
        }

        public String getStatus() {
            return stok > 0 ? "Tersedia" : "Terjual";
        }
    }

    // CLASS UNTUK MENYIMPAN DATA PENJUALAN
    public static class Penjualan {

        public String kode;
        public String tanggal;
        public String kodeTopi;
        public String namaPembeli;
        public double hargaJual;
        public String metode;

        public Penjualan(
                String kode,
                String tanggal,
                String kodeTopi,
                String namaPembeli,
                double hargaJual,
                String metode
        ) {
            this.kode = kode;
            this.tanggal = tanggal;
            this.kodeTopi = kodeTopi;
            this.namaPembeli = namaPembeli;
            this.hargaJual = hargaJual;
            this.metode = metode;
        }
    }

    // CLASS UNTUK MENYIMPAN DATA PEMBELIAN
    public static class Pembelian {

        public String kode;
        public String tanggal;
        public String kodeSupplier;
        public String kodeTopi;
        public int jumlah;
        public double hargaModal;

        public Pembelian(
                String kode,
                String tanggal,
                String kodeSupplier,
                String kodeTopi,
                int jumlah,
                double hargaModal
        ) {
            this.kode = kode;
            this.tanggal = tanggal;
            this.kodeSupplier = kodeSupplier;
            this.kodeTopi = kodeTopi;
            this.jumlah = jumlah;
            this.hargaModal = hargaModal;
        }

        public double getTotal() {
            return jumlah * hargaModal;
        }
    }

    // PENYIMPANAN DATA BERSAMA
    public static final List<Topi> daftarTopi = new ArrayList<>();

    public static final List<Penjualan> daftarPenjualan =
            new ArrayList<>();

    public static final List<Pembelian> daftarPembelian =
            new ArrayList<>();

    // DATA AWAL
    static {

        // DATA TOPI
        daftarTopi.add(
                new Topi(
                        "TP001",
                        "New Era",
                        "Baseball Cap",
                        "Hitam",
                        "Baik",
                        80000,
                        150000,
                        1
                )
        );

        daftarTopi.add(
                new Topi(
                        "TP002",
                        "Adidas",
                        "Classic Cap",
                        "Biru",
                        "Sangat Baik",
                        60000,
                        125000,
                        1
                )
        );

        daftarTopi.add(
                new Topi(
                        "TP003",
                        "Nike",
                        "Swoosh Cap",
                        "Merah",
                        "Cukup Baik",
                        80000,
                        150000,
                        0
                )
        );

        // DATA AWAL PENJUALAN
        daftarPenjualan.add(
                new Penjualan(
                        "PJ001",
                        java.time.LocalDate.now().toString(),
                        "TP003",
                        "Andi",
                        150000,
                        "COD"
                )
        );

        // DATA AWAL PEMBELIAN
        daftarPembelian.add(
                new Pembelian(
                        "PB001",
                        java.time.LocalDate.now().toString(),
                        "SP001",
                        "TP001",
                        1,
                        80000
                )
        );

        daftarPembelian.add(
                new Pembelian(
                        "PB002",
                        java.time.LocalDate.now().toString(),
                        "SP002",
                        "TP002",
                        1,
                        60000
                )
        );
    }

    // MENCARI TOPI BERDASARKAN KODE
    public static Topi cariTopi(String kode) {

        for (Topi topi : daftarTopi) {
            if (topi.kode.equalsIgnoreCase(kode)) {
                return topi;
            }
        }

        return null;
    }

    // MENGHITUNG TOTAL JUMLAH STOK
    public static int getTotalStok() {

        int total = 0;

        for (Topi topi : daftarTopi) {
            total += topi.stok;
        }

        return total;
    }

    // MENGHITUNG JUMLAH TOPI YANG TERSEDIA
    public static int getJumlahTopiTersedia() {

        int total = 0;

        for (Topi topi : daftarTopi) {
            if (topi.stok > 0) {
                total++;
            }
        }

        return total;
    }

    // MENGHITUNG JUMLAH JENIS TOPI YANG STOKNYA HABIS
    public static int getJumlahTopiHabis() {

        int total = 0;

        for (Topi topi : daftarTopi) {
            if (topi.stok == 0) {
                total++;
            }
        }

        return total;
    }

    // MENGHITUNG TOTAL PENJUALAN
    public static double getTotalPenjualan() {

        double total = 0;

        for (Penjualan p : daftarPenjualan) {
            total += p.hargaJual;
        }

        return total;
    }

    // MENGHITUNG TOTAL PEMBELIAN
    public static double getTotalPembelian() {

        double total = 0;

        for (Pembelian p : daftarPembelian) {
            total += p.getTotal();
        }

        return total;
    }

    // MENGHITUNG JUMLAH TRANSAKSI PENJUALAN
    public static int getJumlahTransaksiPenjualan() {
        return daftarPenjualan.size();
    }

    // MENGHITUNG JUMLAH TRANSAKSI PEMBELIAN
    public static int getJumlahTransaksiPembelian() {
        return daftarPembelian.size();
    }
}