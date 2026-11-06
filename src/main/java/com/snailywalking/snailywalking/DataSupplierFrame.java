
package com.snailywalking.snailywalking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class DataSupplierFrame extends JPanel {

    private JTextField kodeField;
    private JTextField namaField;
    private JTextField teleponField;
    private JTextField lokasiField;
    private JTextArea catatanArea;

    private DefaultTableModel model;
    private JTable table;
    private final Runnable onKembali;

    public DataSupplierFrame(Runnable onKembali) {
        this.onKembali = onKembali;

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(245, 246, 248));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(28, 28, 28, 28)
        );

        JLabel title = new JLabel("Data Supplier");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        mainPanel.add(title, BorderLayout.NORTH);

        // FORM SUPPLIER
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBackground(Color.WHITE);
        form.setBorder(
                BorderFactory.createTitledBorder("Informasi Supplier")
        );

        kodeField = new JTextField();
        namaField = new JTextField();
        teleponField = new JTextField();
        lokasiField = new JTextField();
        catatanArea = new JTextArea(3, 20);
        catatanArea.setLineWrap(true);
        catatanArea.setWrapStyleWord(true);

        form.add(new JLabel("Kode Supplier"));
        form.add(kodeField);

        form.add(new JLabel("Nama Supplier"));
        form.add(namaField);

        form.add(new JLabel("Nomor Telepon"));
        form.add(teleponField);

        form.add(new JLabel("Lokasi / Alamat"));
        form.add(lokasiField);

        form.add(new JLabel("Catatan"));
        form.add(new JScrollPane(catatanArea));

        // TABEL SUPPLIER
        String[] columns = {
            "Kode", "Nama Supplier", "Telepon", "Lokasi", "Catatan"
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

        JScrollPane tableScroll = new JScrollPane(table);

        // TOMBOL
        JButton tambahButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton hapusButton = new JButton("Hapus");
        JButton resetButton = new JButton("Reset Form");
        JButton kembaliButton = new JButton("Kembali");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
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

        // MASUKKAN DATA SUPPLIER AWAL JIKA BELUM ADA
        isiDataSupplierAwal();

        // MUAT DATA DARI DATABASE
        muatDataSupplier();

        // TAMBAH SUPPLIER
        tambahButton.addActionListener(e -> {
            String kode = kodeField.getText().trim();
            String nama = namaField.getText().trim();
            String telepon = teleponField.getText().trim();
            String lokasi = lokasiField.getText().trim();
            String catatan = catatanArea.getText().trim();

            if (kode.isEmpty() || nama.isEmpty()
                    || telepon.isEmpty() || lokasi.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Kode, nama, telepon, dan lokasi wajib diisi!");
                return;
            }

            Transaction transaction = null;

            try (Session session = HibernateUtil
                    .getSessionFactory().openSession()) {

                transaction = session.beginTransaction();

                SupplierEntity supplierLama = session.get(
                        SupplierEntity.class, kode
                );

                if (supplierLama != null) {
                    transaction.rollback();

                    JOptionPane.showMessageDialog(this,
                            "Kode supplier sudah digunakan!");
                    return;
                }

                SupplierEntity supplier = new SupplierEntity();
                supplier.setKode(kode);
                supplier.setNama(nama);
                supplier.setTelepon(telepon);
                supplier.setLokasi(lokasi);
                supplier.setCatatan(catatan);

                session.persist(supplier);
                transaction.commit();

                muatDataSupplier();
                resetForm();

                JOptionPane.showMessageDialog(this,
                        "Supplier berhasil ditambahkan!");

            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }

                JOptionPane.showMessageDialog(this,
                        "Gagal menambahkan supplier: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // PILIH DATA UNTUK DIEDIT
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && table.getSelectedRow() != -1) {

                int row = table.getSelectedRow();

                kodeField.setText(model.getValueAt(row, 0).toString());
                namaField.setText(model.getValueAt(row, 1).toString());
                teleponField.setText(model.getValueAt(row, 2).toString());
                lokasiField.setText(model.getValueAt(row, 3).toString());
                catatanArea.setText(model.getValueAt(row, 4).toString());

                kodeField.setEditable(false);
            }
        });

        // EDIT SUPPLIER
        editButton.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih supplier yang ingin diedit!");
                return;
            }

            String kode = kodeField.getText().trim();
            String nama = namaField.getText().trim();
            String telepon = teleponField.getText().trim();
            String lokasi = lokasiField.getText().trim();
            String catatan = catatanArea.getText().trim();

            if (nama.isEmpty() || telepon.isEmpty()
                    || lokasi.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nama, telepon, dan lokasi wajib diisi!");
                return;
            }

            Transaction transaction = null;

            try (Session session = HibernateUtil
                    .getSessionFactory().openSession()) {

                transaction = session.beginTransaction();

                SupplierEntity supplier = session.get(
                        SupplierEntity.class, kode
                );

                if (supplier == null) {
                    transaction.rollback();

                    JOptionPane.showMessageDialog(this,
                            "Data supplier tidak ditemukan!");
                    return;
                }

                supplier.setNama(nama);
                supplier.setTelepon(telepon);
                supplier.setLokasi(lokasi);
                supplier.setCatatan(catatan);

                transaction.commit();

                muatDataSupplier();
                resetForm();

                JOptionPane.showMessageDialog(this,
                        "Data supplier berhasil diedit!");

            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }

                JOptionPane.showMessageDialog(this,
                        "Gagal mengedit supplier: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // HAPUS SUPPLIER
        hapusButton.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                        "Pilih supplier yang ingin dihapus!");
                return;
            }

            String kode = model.getValueAt(row, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin menghapus supplier ini?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            Transaction transaction = null;

            try (Session session = HibernateUtil
                    .getSessionFactory().openSession()) {

                transaction = session.beginTransaction();

                Long jumlahPembelian = session.createQuery(
                        "SELECT COUNT(p) FROM PembelianEntity p "
                        + "WHERE p.kodeSupplier = :kode",
                        Long.class
                ).setParameter("kode", kode).getSingleResult();

                if (jumlahPembelian > 0) {
                    transaction.rollback();

                    JOptionPane.showMessageDialog(this,
                            "Supplier tidak bisa dihapus karena sudah digunakan dalam transaksi pembelian!");
                    return;
                }

                SupplierEntity supplier = session.get(
                        SupplierEntity.class, kode
                );

                if (supplier == null) {
                    transaction.rollback();

                    JOptionPane.showMessageDialog(this,
                            "Data supplier tidak ditemukan!");
                    return;
                }

                session.remove(supplier);
                transaction.commit();

                muatDataSupplier();
                resetForm();

                JOptionPane.showMessageDialog(this,
                        "Data supplier berhasil dihapus!");

            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }

                JOptionPane.showMessageDialog(this,
                        "Gagal menghapus supplier: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        resetButton.addActionListener(e -> resetForm());

        kembaliButton.addActionListener(e -> this.onKembali.run());
    }

    // MEMASUKKAN DATA SUPPLIER AWAL JIKA BELUM ADA
    private void isiDataSupplierAwal() {
        Transaction transaction = null;

        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            if (session.get(SupplierEntity.class, "SP001") == null) {
                SupplierEntity supplier1 = new SupplierEntity();
                supplier1.setKode("SP001");
                supplier1.setNama("Supplier Pasar Baru");
                supplier1.setTelepon("081234567890");
                supplier1.setLokasi("Pasar Baru, Jakarta");
                supplier1.setCatatan("Stok topi vintage");

                session.persist(supplier1);
            }

            if (session.get(SupplierEntity.class, "SP002") == null) {
                SupplierEntity supplier2 = new SupplierEntity();
                supplier2.setKode("SP002");
                supplier2.setNama("Supplier Pasar Senen");
                supplier2.setTelepon("082345678901");
                supplier2.setLokasi("Pasar Senen, Jakarta");
                supplier2.setCatatan("Cari topi kondisi bagus");

                session.persist(supplier2);
            }

            transaction.commit();

        } catch (Exception ex) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

            JOptionPane.showMessageDialog(this,
                    "Gagal menyiapkan data supplier: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // MEMUAT DATA DARI DATABASE KE TABEL
    public void muatDataSupplier() {
        model.setRowCount(0);

        try (Session session = HibernateUtil
                .getSessionFactory().openSession()) {

            java.util.List<SupplierEntity> daftarSupplier =
                    session.createQuery(
                            "FROM SupplierEntity ORDER BY kode",
                            SupplierEntity.class
                    ).getResultList();

            for (SupplierEntity supplier : daftarSupplier) {
                model.addRow(new Object[]{
                    supplier.getKode(),
                    supplier.getNama(),
                    supplier.getTelepon(),
                    supplier.getLokasi(),
                    supplier.getCatatan()
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data supplier: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // RESET FORM
    private void resetForm() {
        kodeField.setText("");
        namaField.setText("");
        teleponField.setText("");
        lokasiField.setText("");
        catatanArea.setText("");

        kodeField.setEditable(true);
        table.clearSelection();
    }
}
