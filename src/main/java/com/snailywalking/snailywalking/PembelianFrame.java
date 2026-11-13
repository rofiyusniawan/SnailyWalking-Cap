
package com.snailywalking.snailywalking;

import com.snailywalking.snailywalking.model.SupplierEntity;
import com.snailywalking.snailywalking.model.TopiEntity;
import com.snailywalking.snailywalking.model.PembelianEntity;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.hibernate.Session;
import org.hibernate.Transaction;

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

        // MUAT DATA DARI DATABASE
        muatDataPembelian();

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

    // TAMBAH PEMBELIAN
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

            Math.multiplyExact((long) jumlah, harga);

            Transaction transaction = null;

            try (Session session = HibernateUtil
                    .getSessionFactory().openSession()) {

                transaction = session.beginTransaction();

                // CEK KODE PEMBELIAN
                PembelianEntity pembelianLama = session.get(
                        PembelianEntity.class, kode
                );

                if (pembelianLama != null) {
                    transaction.rollback();
                    tampilkanPesan("Kode pembelian sudah digunakan!");
                    return;
                }

                // CEK SUPPLIER
                SupplierEntity dataSupplier = session.get(
                        SupplierEntity.class, supplier
                );

                if (dataSupplier == null) {
                    transaction.rollback();
                    tampilkanPesan("Kode supplier tidak ditemukan!");
                    return;
                }

                // CEK TOPI
                TopiEntity topi = session.get(
                        TopiEntity.class, kodeTopi
                );

                if (topi == null) {
                    transaction.rollback();
                    tampilkanPesan(
                            "Kode topi tidak ditemukan! Periksa menu Data Topi."
                    );
                    return;
                }

                // PERBARUI STOK
                int stokBaru = Math.addExact(topi.getStok(), jumlah);
                topi.setStok(stokBaru);

                // SIMPAN TRANSAKSI
                PembelianEntity pembelian = new PembelianEntity();
                pembelian.setKode(kode);
                pembelian.setTanggal(tanggal);
                pembelian.setKodeSupplier(supplier);
                pembelian.setKodeTopi(kodeTopi);
                pembelian.setJumlah(jumlah);
                pembelian.setHargaModal(harga);

                session.persist(pembelian);

                transaction.commit();

                muatDataPembelian();
                resetForm();

                tampilkanPesan(
                        "Pembelian berhasil ditambahkan dan stok topi diperbarui!"
                );
            }

        } catch (NumberFormatException ex) {
            tampilkanPesan(
                    "Jumlah harus berupa bilangan bulat dan harga harus berupa angka."
            );
        } catch (DateTimeParseException ex) {
            tampilkanPesan("Format tanggal harus YYYY-MM-DD!");
        } catch (ArithmeticException ex) {
            tampilkanPesan(
                    "Jumlah, stok, atau total pembelian terlalu besar!"
            );
        } catch (Exception ex) {
            tampilkanPesan(
                    "Gagal menambahkan pembelian: " + ex.getMessage()
            );
            ex.printStackTrace();
        }
    }

    // EDIT PEMBELIAN
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

            int jumlahBaru = Integer.parseInt(
                    jumlahField.getText().trim()
            );
            long hargaBaru = Long.parseLong(
                    hargaField.getText().trim()
            );

            if (jumlahBaru <= 0 || hargaBaru < 0) {
                tampilkanPesan(
                        "Jumlah harus lebih dari 0 dan harga tidak boleh negatif!"
                );
                return;
            }

            Math.multiplyExact((long) jumlahBaru, hargaBaru);

            Transaction transaction = null;

            try (Session session = HibernateUtil
                    .getSessionFactory().openSession()) {

                transaction = session.beginTransaction();

                PembelianEntity pembelian = session.get(
                        PembelianEntity.class, kode
                );

                if (pembelian == null) {
                    transaction.rollback();
                    tampilkanPesan("Data pembelian tidak ditemukan!");
                    return;
                }

                // CEK SUPPLIER BARU
                SupplierEntity supplierBaru = session.get(
                        SupplierEntity.class, supplier
                );

                if (supplierBaru == null) {
                    transaction.rollback();
                    tampilkanPesan("Kode supplier tidak ditemukan!");
                    return;
                }

                // AMBIL DATA TOPI LAMA DAN BARU
                String kodeTopiLama = pembelian.getKodeTopi();

                TopiEntity topiLama = session.get(
                        TopiEntity.class, kodeTopiLama
                );

                TopiEntity topiBaru = session.get(
                        TopiEntity.class, kodeTopiBaru
                );

                if (topiLama == null || topiBaru == null) {
                    transaction.rollback();
                    tampilkanPesan(
                            "Data topi tidak ditemukan. Periksa kode topi."
                    );
                    return;
                }

                int jumlahLama = pembelian.getJumlah();

                // HITUNG PERUBAHAN STOK
                int stokLamaBaru;
                int stokBaruBaru = 0;

                if (kodeTopiLama.equalsIgnoreCase(kodeTopiBaru)) {

                    stokLamaBaru = Math.addExact(
                            Math.subtractExact(
                                    topiLama.getStok(), jumlahLama
                            ),
                            jumlahBaru
                    );

                    if (stokLamaBaru < 0) {
                        transaction.rollback();
                        tampilkanPesan(
                                "Perubahan ditolak karena stok topi tidak boleh minus."
                        );
                        return;
                    }

                    topiLama.setStok(stokLamaBaru);

                } else {

                    stokLamaBaru = Math.subtractExact(
                            topiLama.getStok(), jumlahLama
                    );

                    if (stokLamaBaru < 0) {
                        transaction.rollback();
                        tampilkanPesan(
                                "Stok topi lama tidak cukup untuk mengubah pembelian ini. "
                                + "Kemungkinan sebagian topi sudah terjual."
                        );
                        return;
                    }

                    stokBaruBaru = Math.addExact(
                            topiBaru.getStok(), jumlahBaru
                    );

                    topiLama.setStok(stokLamaBaru);
                    topiBaru.setStok(stokBaruBaru);
                }

                // PERBARUI DATA PEMBELIAN
                pembelian.setTanggal(tanggal);
                pembelian.setKodeSupplier(supplier);
                pembelian.setKodeTopi(kodeTopiBaru);
                pembelian.setJumlah(jumlahBaru);
                pembelian.setHargaModal(hargaBaru);

                transaction.commit();

                muatDataPembelian();
                resetForm();

                tampilkanPesan(
                        "Data pembelian berhasil diperbarui dan stok disesuaikan!"
                );
            }

        } catch (NumberFormatException ex) {
            tampilkanPesan(
                    "Jumlah harus bilangan bulat dan harga harus berupa angka."
            );
        } catch (DateTimeParseException ex) {
            tampilkanPesan("Format tanggal harus YYYY-MM-DD!");
        } catch (ArithmeticException ex) {
            tampilkanPesan(
                    "Perhitungan stok atau total pembelian terlalu besar!"
            );
        } catch (Exception ex) {
            tampilkanPesan(
                    "Gagal mengedit pembelian: " + ex.getMessage()
            );
            ex.printStackTrace();
        }
    }

    // HAPUS PEMBELIAN
    private void hapusPembelian() {
        int row = table.getSelectedRow();

        if (row == -1) {
            tampilkanPesan("Pilih data pembelian yang ingin dihapus!");
            return;
        }

        String kode = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus data pembelian ini?\n"
                + "Stok topi akan dikurangi sesuai jumlah pembelian.",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        Transaction transaction = null;

        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            PembelianEntity pembelian = session.get(
                    PembelianEntity.class, kode
            );

            if (pembelian == null) {
                transaction.rollback();
                tampilkanPesan("Data pembelian tidak ditemukan!");
                return;
            }

            TopiEntity topi = session.get(
                    TopiEntity.class, pembelian.getKodeTopi()
            );

            if (topi == null) {
                transaction.rollback();
                tampilkanPesan("Data topi terkait tidak ditemukan!");
                return;
            }

            if (topi.getStok() < pembelian.getJumlah()) {
                transaction.rollback();
                tampilkanPesan(
                        "Pembelian tidak dapat dihapus karena stok saat ini "
                        + "lebih sedikit daripada jumlah pembelian. "
                        + "Kemungkinan sebagian topi sudah terjual."
                );
                return;
            }

            // KURANGI STOK
            topi.setStok(
                    topi.getStok() - pembelian.getJumlah()
            );

            // HAPUS TRANSAKSI
            session.remove(pembelian);

            transaction.commit();

            muatDataPembelian();
            resetForm();

            tampilkanPesan(
                    "Data pembelian berhasil dihapus dan stok disesuaikan!"
            );

        } catch (Exception ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            tampilkanPesan(
                    "Gagal menghapus pembelian: " + ex.getMessage()
            );
            ex.printStackTrace();
        }
    }

    // MEMUAT DATA DARI DATABASE
    private void muatDataPembelian() {
        model.setRowCount(0);

        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {

            java.util.List<PembelianEntity> daftarPembelian =
                    session.createQuery(
                            "FROM PembelianEntity ORDER BY kode",
                            PembelianEntity.class
                    ).getResultList();

            for (PembelianEntity p : daftarPembelian) {
                model.addRow(new Object[]{
                    p.getKode(),
                    p.getTanggal(),
                    p.getKodeSupplier(),
                    p.getKodeTopi(),
                    p.getJumlah(),
                    p.getHargaModal(),
                    p.getTotal()
                });
            }

        } catch (Exception ex) {
            tampilkanPesan(
                    "Gagal memuat data pembelian: " + ex.getMessage()
            );
            ex.printStackTrace();
        }
    }

    // RESET FORM
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

    // MENAMPILKAN PESAN
    private void tampilkanPesan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan);
    }
}
