package com.snailywalking.snailywalking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PembelianFrame extends JPanel {

    private JTextField kodeField;
    private JTextField tanggalField;
    private JTextField supplierField;
    private JTextField kodeTopiField;
    private JTextField jumlahField;
    private JTextField hargaField;

    private DefaultTableModel model;
    private JTable table;

    private final Runnable onKembali;

    public PembelianFrame(Runnable onKembali) {
        this.onKembali = onKembali;

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(12, 15));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(28, 28, 28, 28)
        );

        JLabel title = new JLabel("Pembelian Topi");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(35, 39, 47));

        mainPanel.add(title, BorderLayout.NORTH);

        // FORM PEMBELIAN
        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Informasi Pembelian"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        kodeField = new JTextField();
        tanggalField = new JTextField(LocalDate.now().toString());
        supplierField = new JTextField();
        kodeTopiField = new JTextField();
        jumlahField = new JTextField("1");
        hargaField = new JTextField();

        form.add(new JLabel("Kode Pembelian"));
        form.add(kodeField);

        form.add(new JLabel("Tanggal (YYYY-MM-DD)"));
        form.add(tanggalField);

        form.add(new JLabel("Kode Supplier"));
        form.add(supplierField);

        form.add(new JLabel("Kode Topi"));
        form.add(kodeTopiField);

        form.add(new JLabel("Jumlah Topi"));
        form.add(jumlahField);

        form.add(new JLabel("Harga Modal per Topi (Rp)"));
        form.add(hargaField);

        // TABEL PEMBELIAN
        String[] columns = {
            "Kode Pembelian",
            "Tanggal",
            "Kode Supplier",
            "Kode Topi",
            "Jumlah",
            "Harga Modal",
            "Total Pembelian"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getColumnModel().getColumn(0).setPreferredWidth(125);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(110);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(70);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(135);

        muatDataPembelian();

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 229, 235)
                )
        );

        // TOMBOL
        JButton tambahButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton hapusButton = new JButton("Hapus");
        JButton resetButton = new JButton("Reset Form");
        JButton kembaliButton = new JButton("Kembali");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.setBackground(new Color(245, 247, 250));

        buttons.add(tambahButton);
        buttons.add(editButton);
        buttons.add(hapusButton);
        buttons.add(resetButton);
        buttons.add(kembaliButton);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(form, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        centerPanel.add(buttons, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // TAMBAH PEMBELIAN
        tambahButton.addActionListener(e -> tambahPembelian());

        // PILIH DATA UNTUK DIEDIT
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && table.getSelectedRow() != -1) {

                int row = table.getSelectedRow();

                kodeField.setText(model.getValueAt(row, 0).toString());
                tanggalField.setText(model.getValueAt(row, 1).toString());
                supplierField.setText(model.getValueAt(row, 2).toString());
                kodeTopiField.setText(model.getValueAt(row, 3).toString());
                jumlahField.setText(model.getValueAt(row, 4).toString());
                hargaField.setText(model.getValueAt(row, 5).toString());

                kodeField.setEditable(false);
            }
        });

        // EDIT PEMBELIAN
        editButton.addActionListener(e -> editPembelian());

        // HAPUS PEMBELIAN
        hapusButton.addActionListener(e -> hapusPembelian());

        resetButton.addActionListener(e -> resetForm());

        kembaliButton.addActionListener(e -> this.onKembali.run());
    }

    private void tambahPembelian() {
        try {
            String kode = kodeField.getText().trim();
            String tanggal = tanggalField.getText().trim();
            String supplier = supplierField.getText().trim();
            String kodeTopi = kodeTopiField.getText().trim();

            if (kode.isEmpty() || tanggal.isEmpty()
                    || supplier.isEmpty() || kodeTopi.isEmpty()
                    || jumlahField.getText().trim().isEmpty()
                    || hargaField.getText().trim().isEmpty()) {

                tampilkanPesan("Semua kolom wajib diisi!");
                return;
            }

            LocalDate.parse(tanggal);

            int jumlah = Integer.parseInt(jumlahField.getText().trim());
            long harga = Long.parseLong(hargaField.getText().trim());

            if (jumlah <= 0 || harga < 0) {
                tampilkanPesan(
                        "Jumlah harus lebih dari 0 dan harga tidak boleh negatif!"
                );
                return;
            }

            // Pastikan kode pembelian belum digunakan.
            for (DataStore.Pembelian p : DataStore.daftarPembelian) {
                if (p.kode.equalsIgnoreCase(kode)) {
                    tampilkanPesan("Kode pembelian sudah digunakan!");
                    return;
                }
            }

            // Pastikan topi terdaftar.
            DataStore.Topi topi = DataStore.cariTopi(kodeTopi);

            if (topi == null) {
                tampilkanPesan(
                        "Kode topi tidak ditemukan! Periksa data pada menu Data Topi."
                );
                return;
            }

            // Cegah nilai stok dan total melampaui batas integer/long.
            int stokBaru = Math.addExact(topi.stok, jumlah);
            long total = Math.multiplyExact((long) jumlah, harga);

            DataStore.Pembelian pembelian = new DataStore.Pembelian(
                    kode, tanggal, supplier, kodeTopi, jumlah, harga
            );

            DataStore.daftarPembelian.add(pembelian);
            topi.stok = stokBaru;

            muatDataPembelian();
            resetForm();

            tampilkanPesan("Pembelian berhasil ditambahkan dan stok topi diperbarui!");

        } catch (NumberFormatException ex) {
            tampilkanPesan("Jumlah harus berupa bilangan bulat dan harga harus berupa angka.");
        } catch (DateTimeParseException ex) {
            tampilkanPesan("Format tanggal harus YYYY-MM-DD!");
        } catch (ArithmeticException ex) {
            tampilkanPesan("Jumlah, stok, atau total pembelian terlalu besar!");
        }
    }

    private void editPembelian() {
        int row = table.getSelectedRow();

        if (row == -1) {
            tampilkanPesan("Pilih data pembelian yang ingin diedit!");
            return;
        }

        try {
            String kode = model.getValueAt(row, 0).toString();
            String tanggal = tanggalField.getText().trim();
            String supplier = supplierField.getText().trim();
            String kodeTopiBaru = kodeTopiField.getText().trim();

            if (tanggal.isEmpty() || supplier.isEmpty()
                    || kodeTopiBaru.isEmpty()
                    || jumlahField.getText().trim().isEmpty()
                    || hargaField.getText().trim().isEmpty()) {

                tampilkanPesan("Semua kolom wajib diisi!");
                return;
            }

            LocalDate.parse(tanggal);

            int jumlahBaru = Integer.parseInt(jumlahField.getText().trim());
            long hargaBaru = Long.parseLong(hargaField.getText().trim());

            if (jumlahBaru <= 0 || hargaBaru < 0) {
                tampilkanPesan(
                        "Jumlah harus lebih dari 0 dan harga tidak boleh negatif!"
                );
                return;
            }

            DataStore.Pembelian pembelianLama = cariPembelian(kode);

            if (pembelianLama == null) {
                tampilkanPesan("Data pembelian tidak ditemukan di DataStore.");
                return;
            }

            DataStore.Topi topiLama =
                    DataStore.cariTopi(pembelianLama.kodeTopi);

            DataStore.Topi topiBaru =
                    DataStore.cariTopi(kodeTopiBaru);

            if (topiLama == null || topiBaru == null) {
                tampilkanPesan("Data topi tidak ditemukan. Periksa kode topi.");
                return;
            }

            // Hitung perubahan stok sebelum mengubah data apa pun.
            int stokLamaBaru;
            int stokBaruBaru;

            if (topiLama == topiBaru) {
                stokLamaBaru = Math.addExact(
                        topiLama.stok,
                        jumlahBaru - pembelianLama.jumlah
                );

                stokBaruBaru = stokLamaBaru;
            } else {
                stokLamaBaru = Math.subtractExact(
                        topiLama.stok,
                        pembelianLama.jumlah
                );

                if (stokLamaBaru < 0) {
                    tampilkanPesan(
                            "Stok tidak cukup untuk mengubah pembelian ini. "
                            + "Sebagian topi mungkin sudah terjual."
                    );
                    return;
                }

                stokBaruBaru = Math.addExact(topiBaru.stok, jumlahBaru);
            }

            if (stokLamaBaru < 0) {
                tampilkanPesan(
                        "Perubahan ditolak karena stok topi tidak boleh minus."
                );
                return;
            }

            Math.multiplyExact((long) jumlahBaru, hargaBaru);

            // Terapkan perubahan stok.
            topiLama.stok = stokLamaBaru;

            if (topiLama != topiBaru) {
                topiBaru.stok = stokBaruBaru;
            }

            // Perbarui data pembelian yang tersimpan.
            pembelianLama.tanggal = tanggal;
            pembelianLama.kodeSupplier = supplier;
            pembelianLama.kodeTopi = kodeTopiBaru;
            pembelianLama.jumlah = jumlahBaru;
            pembelianLama.hargaModal = hargaBaru;

            muatDataPembelian();
            resetForm();

            tampilkanPesan("Data pembelian berhasil diperbarui dan stok disesuaikan!");

        } catch (NumberFormatException ex) {
            tampilkanPesan("Jumlah harus bilangan bulat dan harga harus berupa angka.");
        } catch (DateTimeParseException ex) {
            tampilkanPesan("Format tanggal harus YYYY-MM-DD!");
        } catch (ArithmeticException ex) {
            tampilkanPesan("Perhitungan stok atau total pembelian terlalu besar!");
        }
    }

    private void hapusPembelian() {
        int row = table.getSelectedRow();

        if (row == -1) {
            tampilkanPesan("Pilih data pembelian yang ingin dihapus!");
            return;
        }

        String kode = model.getValueAt(row, 0).toString();
        DataStore.Pembelian pembelian = cariPembelian(kode);

        if (pembelian == null) {
            tampilkanPesan("Data pembelian tidak ditemukan.");
            return;
        }

        DataStore.Topi topi = DataStore.cariTopi(pembelian.kodeTopi);

        if (topi == null) {
            tampilkanPesan("Data topi terkait tidak ditemukan.");
            return;
        }

        if (topi.stok < pembelian.jumlah) {
            tampilkanPesan(
                    "Pembelian tidak dapat dihapus karena stok saat ini lebih sedikit "
                    + "daripada jumlah pembelian. Kemungkinan sebagian topi sudah terjual."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus data pembelian ini?\n"
                + "Stok topi akan dikurangi sebanyak " + pembelian.jumlah + ".",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            topi.stok -= pembelian.jumlah;
            DataStore.daftarPembelian.remove(pembelian);

            muatDataPembelian();
            resetForm();

            tampilkanPesan("Data pembelian berhasil dihapus dan stok disesuaikan!");
        }
    }

    private DataStore.Pembelian cariPembelian(String kode) {
        for (DataStore.Pembelian p : DataStore.daftarPembelian) {
            if (p.kode.equalsIgnoreCase(kode)) {
                return p;
            }
        }
        return null;
    }

    private void muatDataPembelian() {
        model.setRowCount(0);

        for (DataStore.Pembelian p : DataStore.daftarPembelian) {
            model.addRow(new Object[]{
                p.kode,
                p.tanggal,
                p.kodeSupplier,
                p.kodeTopi,
                p.jumlah,
                p.hargaModal,
                p.getTotal()
            });
        }
    }

    private void resetForm() {
        kodeField.setText("");
        tanggalField.setText(LocalDate.now().toString());
        supplierField.setText("");
        kodeTopiField.setText("");
        jumlahField.setText("1");
        hargaField.setText("");

        kodeField.setEditable(true);
        table.clearSelection();
    }

    private void tampilkanPesan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan);
    }
}