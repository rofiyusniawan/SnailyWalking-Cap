
package com.snailywalking.snailywalking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

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

        model.addRow(new Object[]{
            "SP001", "Supplier Pasar Baru",
            "081234567890", "Pasar Baru, Jakarta",
            "Stok topi vintage"
        });

        model.addRow(new Object[]{
            "SP002", "Supplier Pasar Senen",
            "082345678901", "Pasar Senen, Jakarta",
            "Cari topi kondisi bagus"
        });

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

            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).toString()
                        .equalsIgnoreCase(kode)) {
                    JOptionPane.showMessageDialog(this,
                        "Kode supplier sudah digunakan!");
                    return;
                }
            }

            model.addRow(new Object[]{
                kode, nama, telepon, lokasi, catatan
            });

            resetForm();

            JOptionPane.showMessageDialog(this,
                "Supplier berhasil ditambahkan!");
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

            model.setValueAt(nama, row, 1);
            model.setValueAt(telepon, row, 2);
            model.setValueAt(lokasi, row, 3);
            model.setValueAt(catatan, row, 4);

            resetForm();

            JOptionPane.showMessageDialog(this,
                "Data supplier berhasil diedit!");
        });

        // HAPUS SUPPLIER
        hapusButton.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(this,
                    "Pilih supplier yang ingin dihapus!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus supplier ini?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                model.removeRow(row);
                resetForm();
            }
        });

        resetButton.addActionListener(e -> resetForm());

        kembaliButton.addActionListener(e -> this.onKembali.run());
    }

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
