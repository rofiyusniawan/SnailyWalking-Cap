package com.snailywalking.snailywalking;

public class TestKoneksi {

    public static void main(String[] args) {

        try {
            HibernateUtil.getSessionFactory();

            System.out.println("================================");
            System.out.println("KONEKSI DATABASE BERHASIL!");
            System.out.println("Hibernate berhasil dijalankan.");
            System.out.println("================================");

            HibernateUtil.shutdown();

        } catch (Exception e) {
            System.out.println("KONEKSI DATABASE GAGAL!");
            e.printStackTrace();
        }
    }
}