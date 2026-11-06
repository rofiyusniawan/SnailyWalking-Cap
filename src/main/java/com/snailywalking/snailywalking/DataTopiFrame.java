
package com.snailywalking.snailywalking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class DataTopiFrame extends JPanel {

    private DefaultTableModel model;
    private JTable table;

    private JTextField kodeField, merekField, modelField;
    private JTextField warnaField, kondisiField, modalField, jualField;
    private JTextField stokField;

    private final Runnable onKembali;

    public DataTopiFrame(Runnable onKembali) {
        this.onKembali = onKembali;

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );
        mainPanel.setBackground(new Color(245, 246, 248));

        JLabel title = new JLabel("Data Topi");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        mainPanel.add(title, BorderLayout.NORTH);

        // FORM INPUT
        JPanel form = new JPanel(new GridLayout(0, 4, 10, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(
                BorderFactory.createTitledBorder("Informasi Topi")
        );

        kodeField = new JTextField();
        merekField = new JTextField();
        modelField = new JTextField();
        warnaField = new JTextField();
        kondisiField = new JTextField();
        modalField = new JTextField();
        jualField = new JTextField();
        stokField = new JTextField("0");

        form.add(new JLabel("Kode Topi"));
        form.add(kodeField);
        form.add(new JLabel("Merek"));
        form.add(merekField);

        form.add(new JLabel("Model"));
        form.add(modelField);
        form.add(new JLabel("Warna"));
        form.add(warnaField);

        form.add(new JLabel("Kondisi"));
        form.add(kondisiField);
        form.add(new JLabel("Harga Modal (Rp)"));
        form.add(modalField);

        form.add(new JLabel("Harga Jual (Rp)"));
        form.add(jualField);
        form.add(new JLabel("Stok Awal"));
        form.add(stokField);

        // TABEL DATA
        String[] columns = {
            "Kode", "Merek", "Model", "Warna",
            "Kondisi", "Harga Modal", "Harga Jual",
            "Stok", "Status"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane tableScroll = new JScrollPane(table);

        // TOMBOL
        JButton tambahButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton hapusButton = new JButton("Hapus");
        JButton resetButton = new JButton("Reset Form");
        JButton kembaliButton = new JButton("Kembali");

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );
        buttons.setBackground(new Color(245, 246, 248));

        buttons.add(tambahButton);
        buttons.add(editButton);
        buttons.add(hapusButton);
        buttons.add(resetButton);
        buttons.add(kembaliButton);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(form, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        centerPanel.add(buttons, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // MUAT DATA DARI DATABASE
        muatDataTopi();

        // TAMBAH DATA
        tambahButton.addActionListener(e -> {
            String kode = kodeField.getText().trim();
            String merek = merekField.getText().trim();
            String namaModel = modelField.getText().trim();
            String warna = warnaField.getText().trim();
            String kondisi = kondisiField.getText().trim();

            if (kode.isEmpty() || merek.isEmpty()
                    || namaModel.isEmpty() || warna.isEmpty()
                    || kondisi.isEmpty()
                    || modalField.getText().trim().isEmpty()
                    || jualField.getText().trim().isEmpty()
                    || stokField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this, "Semua kolom harus diisi!"
                );
                return;
            }

            try {
                double modal = Double.parseDouble(
                        modalField.getText().trim()
                );
                double jual = Double.parseDouble(
                        jualField.getText().trim()
                );
                int stok = Integer.parseInt(
                        stokField.getText().trim()
                );

                if (modal < 0 || jual < 0 || stok < 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Harga dan stok tidak boleh negatif!"
                    );
                    return;
                }

                Transaction transaction = null;

                try (Session session = HibernateUtil
                        .getSessionFactory().openSession()) {

                    transaction = session.beginTransaction();

                    TopiEntity topiLama = session.get(
                            TopiEntity.class, kode
                    );

                    if (topiLama != null) {
                        transaction.rollback();

                        JOptionPane.showMessageDialog(
                                this, "Kode topi sudah digunakan!"
                        );
                        return;
                    }

                    TopiEntity topi = new TopiEntity(
                            kode, merek, namaModel, warna,
                            kondisi, modal, jual, stok
                    );

                    session.persist(topi);
                    transaction.commit();
                }

                muatDataTopi();
                resetForm();

                JOptionPane.showMessageDialog(
                        this, "Data topi berhasil ditambahkan!"
                );

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Harga harus berupa angka dan stok harus berupa bilangan bulat!"
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Gagal menambahkan data topi: " + ex.getMessage()
                );
                ex.printStackTrace();
            }
        });

        // PILIH BARIS UNTUK DIEDIT
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && table.getSelectedRow() != -1) {

                int row = table.getSelectedRow();

                kodeField.setText(
                        model.getValueAt(row, 0).toString()
                );
                merekField.setText(
                        model.getValueAt(row, 1).toString()
                );
                modelField.setText(
                        model.getValueAt(row, 2).toString()
                );
                warnaField.setText(
                        model.getValueAt(row, 3).toString()
                );
                kondisiField.setText(
                        model.getValueAt(row, 4).toString()
                );
                modalField.setText(
                        model.getValueAt(row, 5).toString()
                );
                jualField.setText(
                        model.getValueAt(row, 6).toString()
                );
                stokField.setText(
                        model.getValueAt(row, 7).toString()
                );

                kodeField.setEditable(false);
                stokField.setEditable(true);
            }
        });

        // EDIT DATA
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Pilih data topi yang ingin diedit!"
                );
                return;
            }

            try {
                String kode = kodeField.getText().trim();
                String merek = merekField.getText().trim();
                String namaModel = modelField.getText().trim();
                String warna = warnaField.getText().trim();
                String kondisi = kondisiField.getText().trim();

                if (merek.isEmpty() || namaModel.isEmpty()
                        || warna.isEmpty() || kondisi.isEmpty()
                        || modalField.getText().trim().isEmpty()
                        || jualField.getText().trim().isEmpty()
                        || stokField.getText().trim().isEmpty()) {

                    JOptionPane.showMessageDialog(
                            this, "Semua kolom harus diisi!"
                    );
                    return;
                }

                double modal = Double.parseDouble(
                        modalField.getText().trim()
                );
                double jual = Double.parseDouble(
                        jualField.getText().trim()
                );
                int stok = Integer.parseInt(
                        stokField.getText().trim()
                );

                if (modal < 0 || jual < 0 || stok < 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Harga dan stok tidak boleh negatif!"
                    );
                    return;
                }

                Transaction transaction = null;

                try (Session session = HibernateUtil
                        .getSessionFactory().openSession()) {

                    transaction = session.beginTransaction();

                    TopiEntity topi = session.get(
                            TopiEntity.class, kode
                    );

                    if (topi == null) {
                        transaction.rollback();

                        JOptionPane.showMessageDialog(
                                this, "Data topi tidak ditemukan!"
                        );
                        return;
                    }

                    topi.setMerek(merek);
                    topi.setModel(namaModel);
                    topi.setWarna(warna);
                    topi.setKondisi(kondisi);
                    topi.setHargaModal(modal);
                    topi.setHargaJual(jual);
                    topi.setStok(stok);

                    transaction.commit();
                }

                muatDataTopi();
                resetForm();

                JOptionPane.showMessageDialog(
                        this, "Data topi berhasil diedit!"
                );

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Harga harus berupa angka dan stok harus berupa bilangan bulat!"
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Gagal mengedit data topi: " + ex.getMessage()
                );
                ex.printStackTrace();
            }
        });

        // HAPUS DATA
        hapusButton.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Pilih data topi yang ingin dihapus!"
                );
                return;
            }

            String kode = model.getValueAt(row, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Yakin ingin menghapus data topi ini?",
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

                Long jumlahPembelian = session.createQuery(
                        "SELECT COUNT(p) FROM PembelianEntity p "
                        + "WHERE p.kodeTopi = :kode",
                        Long.class
                ).setParameter("kode", kode).getSingleResult();

                Long jumlahPenjualan = session.createQuery(
                        "SELECT COUNT(p) FROM PenjualanEntity p "
                        + "WHERE p.kodeTopi = :kode",
                        Long.class
                ).setParameter("kode", kode).getSingleResult();

                if (jumlahPembelian > 0 || jumlahPenjualan > 0) {
                    transaction.rollback();

                    JOptionPane.showMessageDialog(
                            this,
                            "Data topi tidak bisa dihapus karena sudah digunakan dalam transaksi!"
                    );
                    return;
                }

                TopiEntity topi = session.get(
                        TopiEntity.class, kode
                );

                if (topi == null) {
                    transaction.rollback();

                    JOptionPane.showMessageDialog(
                            this, "Data topi tidak ditemukan!"
                    );
                    return;
                }

                session.remove(topi);
                transaction.commit();

                muatDataTopi();
                resetForm();

                JOptionPane.showMessageDialog(
                        this, "Data topi berhasil dihapus!"
                );

            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }

                JOptionPane.showMessageDialog(
                        this,
                        "Gagal menghapus data topi: " + ex.getMessage()
                );
                ex.printStackTrace();
            }
        });

        resetButton.addActionListener(e -> resetForm());

        kembaliButton.addActionListener(
                e -> this.onKembali.run()
        );
    }

    // MEMUAT DATA DARI DATABASE KE TABEL
    public void muatDataTopi() {
        model.setRowCount(0);

        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {

            java.util.List<TopiEntity> daftarTopi = session.createQuery(
                    "FROM TopiEntity ORDER BY kode",
                    TopiEntity.class
            ).getResultList();

            for (TopiEntity topi : daftarTopi) {
                model.addRow(new Object[]{
                    topi.getKode(),
                    topi.getMerek(),
                    topi.getModel(),
                    topi.getWarna(),
                    topi.getKondisi(),
                    topi.getHargaModal(),
                    topi.getHargaJual(),
                    topi.getStok(),
                    topi.getStatus()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat data topi dari database: "
                    + ex.getMessage()
            );
            ex.printStackTrace();
        }
    }

    // RESET FORM
    private void resetForm() {
        kodeField.setText("");
        merekField.setText("");
        modelField.setText("");
        warnaField.setText("");
        kondisiField.setText("");
        modalField.setText("");
        jualField.setText("");
        stokField.setText("0");

        kodeField.setEditable(true);
        stokField.setEditable(true);

        table.clearSelection();
    }
}
