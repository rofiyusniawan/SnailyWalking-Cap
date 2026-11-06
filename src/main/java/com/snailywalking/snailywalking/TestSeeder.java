package com.snailywalking.snailywalking;

public class TestSeeder {

    public static void main(String[] args) {

        try {
            DataSeeder.isiDataAwal();

        } finally {
            HibernateUtil.shutdown();
        }
    }
}