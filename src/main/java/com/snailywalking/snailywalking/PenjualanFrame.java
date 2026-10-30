package com.snailywalking.snailywalking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class PenjualanFrame extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;
    private final Runnable onKembali;

    private final NumberFormat rupiah =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public PenjualanFrame(Runnable onKembali) {
        this.onKembali = onKembali;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 35, 45));
        header.setBorder(
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        );

        JLabel title = new JLabel("Data Penjualan");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // TABEL
        String[] columns = {
            "Kode Penjualan",
            "Tanggal",
            "Kode Topi",
            "Nama Pembeli",
            "Harga Jual",
            "Metode"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        add(scrollPane, BorderLayout.CENTER);

        // TOMBOL
        JButton tambah = new JButton("Tambah Penjualan");
        JButton edit = new JButton("Edit");
        JButton hapus = new JButton("Hapus");
        JButton kembali = new JButton("Kembali");

        tambah.addActionListener(e -> tambahPenjualan());
        edit.addActionListener(e -> editPenjualan());
        hapus.addActionListener(e -> hapusPenjualan());
        kembali.addActionListener(e -> onKembali.run());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        buttons.setBackground(Color.WHITE);

        buttons.add(tambah);
        buttons.add(edit);
        buttons.add(hapus);
        buttons.add(kembali);

        add(buttons, BorderLayout.SOUTH);

        // TAMPILKAN DATA BERSAMA
        muatData();
    }

    // MEMUAT DATA DARI DATASTORE
    private void muatData() {
        model.setRowCount(0);

        for (DataStore.Penjualan p : DataStore.daftarPenjualan) {
            model.addRow(new Object[]{
                p.kode,
                p.tanggal,
                p.kodeTopi,
                p.namaPembeli,
                p.hargaJual,
                p.metode
            });
        }
    }

    // TAMBAH PENJUALAN
    private void tambahPenjualan() {
        JTextField kode = new JTextField();
        JTextField tanggal = new JTextField(
                java.time.LocalDate.now().toString()
        );
        JTextField kodeTopi = new JTextField();
        JTextField namaPembeli = new JTextField();
        JTextField hargaJual = new JTextField();
        String[] metode = {"COD", "JNE"};

        JComboBox<String> pilihanMetode = new JComboBox<>(metode);

        Object[] fields = {
            "Kode Penjualan:", kode,
            "Tanggal (YYYY-MM-DD):", tanggal,
            "Kode Topi:", kodeTopi,
            "Nama Pembeli:", namaPembeli,
            "Harga Jual:", hargaJual,
            "Metode Pembayaran:", pilihanMetode
        };

        int hasil = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Tambah Penjualan",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (hasil != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String k = kode.getText().trim();
            String t = tanggal.getText().trim();
            String kt = kodeTopi.getText().trim();
            String np = namaPembeli.getText().trim();
            double harga = Double.parseDouble(hargaJual.getText().trim());
            String m = pilihanMetode.getSelectedItem().toString();

            if (k.isEmpty() || t.isEmpty() || kt.isEmpty()
                    || np.isEmpty() || harga <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Semua data harus diisi dan harga harus lebih dari 0."
                );
                return;
            }

            if (!t.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Format tanggal harus YYYY-MM-DD."
                );
                return;
            }

            // CEK TANGGAL VALID
            java.time.LocalDate.parse(t);

            // CEK KODE DUPLIKAT
            for (DataStore.Penjualan p : DataStore.daftarPenjualan) {
                if (p.kode.equalsIgnoreCase(k)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Kode penjualan sudah digunakan."
                    );
                    return;
                }
            }

            DataStore.daftarPenjualan.add(
                    new DataStore.Penjualan(k, t, kt, np, harga, m)
            );

            muatData();

            JOptionPane.showMessageDialog(
                    this,
                    "Data penjualan berhasil ditambahkan."
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Harga jual harus berupa angka."
            );
        } catch (java.time.DateTimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tanggal tidak valid."
            );
        }
    }

    // EDIT PENJUALAN
    private void editPenjualan() {
        int baris = table.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih transaksi yang ingin diedit."
            );
            return;
        }

        String kodeLama = model.getValueAt(baris, 0).toString();

        DataStore.Penjualan data = null;

        for (DataStore.Penjualan p : DataStore.daftarPenjualan) {
            if (p.kode.equals(kodeLama)) {
                data = p;
                break;
            }
        }

        if (data == null) {
            return;
        }

        JTextField kode = new JTextField(data.kode);
        JTextField tanggal = new JTextField(data.tanggal);
        JTextField kodeTopi = new JTextField(data.kodeTopi);
        JTextField namaPembeli = new JTextField(data.namaPembeli);
        JTextField hargaJual = new JTextField(
                String.valueOf(data.hargaJual)
        );

        String[] metode = {"COD", "JNE"};
        JComboBox<String> pilihanMetode = new JComboBox<>(metode);
        pilihanMetode.setSelectedItem(data.metode);

        Object[] fields = {
            "Kode Penjualan:", kode,
            "Tanggal (YYYY-MM-DD):", tanggal,
            "Kode Topi:", kodeTopi,
            "Nama Pembeli:", namaPembeli,
            "Harga Jual:", hargaJual,
            "Metode Pembayaran:", pilihanMetode
        };

        int hasil = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Edit Penjualan",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (hasil != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            String k = kode.getText().trim();
            String t = tanggal.getText().trim();
            String kt = kodeTopi.getText().trim();
            String np = namaPembeli.getText().trim();
            double harga = Double.parseDouble(hargaJual.getText().trim());
            String m = pilihanMetode.getSelectedItem().toString();

            if (k.isEmpty() || t.isEmpty() || kt.isEmpty()
                    || np.isEmpty() || harga <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Semua data harus diisi dan harga harus lebih dari 0."
                );
                return;
            }

            java.time.LocalDate.parse(t);

            for (DataStore.Penjualan p : DataStore.daftarPenjualan) {
                if (!p.kode.equals(kodeLama)
                        && p.kode.equalsIgnoreCase(k)) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Kode penjualan sudah digunakan."
                    );
                    return;
                }
            }

            data.kode = k;
            data.tanggal = t;
            data.kodeTopi = kt;
            data.namaPembeli = np;
            data.hargaJual = harga;
            data.metode = m;

            muatData();

            JOptionPane.showMessageDialog(
                    this,
                    "Data penjualan berhasil diperbarui."
            );

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Harga jual harus berupa angka."
            );
        } catch (java.time.DateTimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Tanggal tidak valid."
            );
        }
    }

    // HAPUS PENJUALAN
    private void hapusPenjualan() {
        int baris = table.getSelectedRow();

        if (baris == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Pilih transaksi yang ingin dihapus."
            );
            return;
        }

        String kode = model.getValueAt(baris, 0).toString();

        int konfirmasi = JOptionPane.showConfirmDialog(
                this,
                "Hapus transaksi " + kode + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (konfirmasi == JOptionPane.YES_OPTION) {
            DataStore.daftarPenjualan.removeIf(
                    p -> p.kode.equals(kode)
            );

            muatData();

            JOptionPane.showMessageDialog(
                    this,
                    "Transaksi berhasil dihapus."
            );
        }
    }
}