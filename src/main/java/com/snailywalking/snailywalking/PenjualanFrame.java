
package com.snailywalking.snailywalking;

import com.snailywalking.snailywalking.model.PenjualanEntity;
import com.snailywalking.snailywalking.model.TopiEntity;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class PenjualanFrame extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;
    private final Runnable onKembali;

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

        // TAMPILKAN DATA DARI DATABASE
        muatData();
    }

    // MEMUAT DATA PENJUALAN DARI MYSQL
    private void muatData() {
        model.setRowCount(0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<PenjualanEntity> daftarPenjualan =
                    session.createQuery(
                            "FROM PenjualanEntity ORDER BY kode",
                            PenjualanEntity.class
                    ).getResultList();

            for (PenjualanEntity p : daftarPenjualan) {
                model.addRow(new Object[]{
                    p.getKode(),
                    p.getTanggal(),
                    p.getKodeTopi(),
                    p.getNamaPembeli(),
                    p.getHargaJual(),
                    p.getMetode()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat data penjualan: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    // TAMBAH PENJUALAN
    private void tambahPenjualan() {
        JTextField kode = new JTextField();
        JTextField tanggal = new JTextField(LocalDate.now().toString());
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

        Transaction transaction = null;

        try {
            String k = kode.getText().trim();
            String t = tanggal.getText().trim();
            String kt = kodeTopi.getText().trim();
            String np = namaPembeli.getText().trim();
            double harga = Double.parseDouble(hargaJual.getText().trim());
            String m = pilihanMetode.getSelectedItem().toString();

            if (k.isEmpty() || t.isEmpty() || kt.isEmpty()
                    || np.isEmpty() || harga <= 0
                    || !Double.isFinite(harga)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Semua data harus diisi dan harga harus lebih dari 0."
                );
                return;
            }

            LocalDate.parse(t);

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();

                // CEK KODE PENJUALAN DUPLIKAT
                Long jumlah = session.createQuery(
                        "SELECT COUNT(p) FROM PenjualanEntity p "
                        + "WHERE LOWER(p.kode) = LOWER(:kode)",
                        Long.class
                ).setParameter("kode", k).getSingleResult();

                if (jumlah > 0) {
                    transaction.rollback();
                    JOptionPane.showMessageDialog(
                            this,
                            "Kode penjualan sudah digunakan."
                    );
                    return;
                }

                // CEK TOPI DAN STOK
                TopiEntity topi = session.get(TopiEntity.class, kt);

                if (topi == null) {
                    transaction.rollback();
                    JOptionPane.showMessageDialog(
                            this,
                            "Kode topi tidak ditemukan."
                    );
                    return;
                }

                if (topi.getStok() <= 0) {
                    transaction.rollback();
                    JOptionPane.showMessageDialog(
                            this,
                            "Stok topi sudah habis."
                    );
                    return;
                }

                // KURANGI STOK
                topi.setStok(topi.getStok() - 1);

                // SIMPAN TRANSAKSI
                PenjualanEntity data = new PenjualanEntity(
                        k, t, kt, np, harga, m
                );

                session.persist(data);

                transaction.commit();
            }

            muatData();

            JOptionPane.showMessageDialog(
                    this,
                    "Data penjualan berhasil ditambahkan."
            );

        } catch (NumberFormatException ex) {
            rollback(transaction);
            JOptionPane.showMessageDialog(
                    this,
                    "Harga jual harus berupa angka."
            );
        } catch (java.time.DateTimeException ex) {
            rollback(transaction);
            JOptionPane.showMessageDialog(
                    this,
                    "Tanggal tidak valid. Gunakan format YYYY-MM-DD."
            );
        } catch (Exception ex) {
            rollback(transaction);
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menambahkan penjualan: " + ex.getMessage()
            );
            ex.printStackTrace();
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

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            PenjualanEntity data = session.get(
                    PenjualanEntity.class, kodeLama
            );

            if (data == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Data penjualan tidak ditemukan."
                );
                muatData();
                return;
            }

            JTextField kode = new JTextField(data.getKode());
            JTextField tanggal = new JTextField(data.getTanggal());
            JTextField kodeTopi = new JTextField(data.getKodeTopi());
            JTextField namaPembeli = new JTextField(data.getNamaPembeli());
            JTextField hargaJual = new JTextField(
                    String.valueOf(data.getHargaJual())
            );

            String[] metode = {"COD", "JNE"};
            JComboBox<String> pilihanMetode = new JComboBox<>(metode);
            pilihanMetode.setSelectedItem(data.getMetode());

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

            String k = kode.getText().trim();
            String t = tanggal.getText().trim();
            String kt = kodeTopi.getText().trim();
            String np = namaPembeli.getText().trim();
            double harga = Double.parseDouble(hargaJual.getText().trim());
            String m = pilihanMetode.getSelectedItem().toString();

            if (k.isEmpty() || t.isEmpty() || kt.isEmpty()
                    || np.isEmpty() || harga <= 0
                    || !Double.isFinite(harga)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Semua data harus diisi dan harga harus lebih dari 0."
                );
                return;
            }

            LocalDate.parse(t);

            Transaction transaction = null;

            try {
                transaction = session.beginTransaction();

                // CEK KODE DUPLIKAT
                Long jumlah = session.createQuery(
                        "SELECT COUNT(p) FROM PenjualanEntity p "
                        + "WHERE LOWER(p.kode) = LOWER(:kode) "
                        + "AND p.kode <> :kodeLama",
                        Long.class
                )
                .setParameter("kode", k)
                .setParameter("kodeLama", kodeLama)
                .getSingleResult();

                if (jumlah > 0) {
                    transaction.rollback();
                    JOptionPane.showMessageDialog(
                            this,
                            "Kode penjualan sudah digunakan."
                    );
                    return;
                }

                // AMBIL DATA TOPI LAMA DAN BARU
                TopiEntity topiLama = session.get(
                        TopiEntity.class, data.getKodeTopi()
                );

                TopiEntity topiBaru = session.get(
                        TopiEntity.class, kt
                );

                if (topiLama == null || topiBaru == null) {
                    transaction.rollback();
                    JOptionPane.showMessageDialog(
                            this,
                            "Data topi tidak ditemukan."
                    );
                    return;
                }

                // KEMBALIKAN STOK TOPI LAMA
                topiLama.setStok(topiLama.getStok() + 1);

                // CEK STOK TOPI BARU
                if (topiBaru.getStok() <= 0) {
                    transaction.rollback();
                    JOptionPane.showMessageDialog(
                            this,
                            "Stok topi yang dipilih sudah habis."
                    );
                    return;
                }

                // KURANGI STOK TOPI BARU
                topiBaru.setStok(topiBaru.getStok() - 1);

                // PERBARUI DATA PENJUALAN
                data.setKode(k);
                data.setTanggal(t);
                data.setKodeTopi(kt);
                data.setNamaPembeli(np);
                data.setHargaJual(harga);
                data.setMetode(m);

                session.merge(data);

                transaction.commit();

            } catch (Exception ex) {
                rollback(transaction);
                throw ex;
            }

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
                    "Tanggal tidak valid. Gunakan format YYYY-MM-DD."
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal mengedit penjualan: " + ex.getMessage()
            );
            ex.printStackTrace();
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

        if (konfirmasi != JOptionPane.YES_OPTION) {
            return;
        }

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            PenjualanEntity data = session.get(
                    PenjualanEntity.class, kode
            );

            if (data == null) {
                transaction.rollback();
                JOptionPane.showMessageDialog(
                        this,
                        "Data penjualan tidak ditemukan."
                );
                muatData();
                return;
            }

            // KEMBALIKAN STOK TOPI
            TopiEntity topi = session.get(
                    TopiEntity.class, data.getKodeTopi()
            );

            if (topi != null) {
                topi.setStok(topi.getStok() + 1);
            }

            // HAPUS TRANSAKSI
            session.remove(data);

            transaction.commit();

            muatData();

            JOptionPane.showMessageDialog(
                    this,
                    "Transaksi berhasil dihapus."
            );

        } catch (Exception ex) {
            rollback(transaction);
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal menghapus penjualan: " + ex.getMessage()
            );
            ex.printStackTrace();
        }
    }

    // MEMBATALKAN TRANSAKSI JIKA TERJADI KESALAHAN
    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}
