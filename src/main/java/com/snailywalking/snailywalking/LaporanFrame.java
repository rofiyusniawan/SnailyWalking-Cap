package com.snailywalking.snailywalking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class LaporanFrame extends JPanel {

    private final Color dark = new Color(30, 35, 45);
    private final Color background = new Color(245, 247, 250);

    private final DefaultTableModel model;
    private final JLabel totalPenjualan;
    private final JLabel totalPembelian;
    private final JLabel jumlahTersedia;
    private final JLabel jumlahTerjual;

    private final NumberFormat formatRupiah =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public LaporanFrame(Runnable onKembali) {
        setLayout(new BorderLayout());
        setBackground(background);

        // HEADER
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(dark);
        header.setBorder(
                BorderFactory.createEmptyBorder(18, 25, 18, 25)
        );

        JLabel title = new JLabel("Laporan");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JLabel subtitle = new JLabel(
                "Ringkasan aktivitas bisnis Snaily Walking Cap"
        );
        subtitle.setForeground(new Color(210, 215, 225));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setOpaque(false);
        headerText.add(title);
        headerText.add(Box.createVerticalStrut(5));
        headerText.add(subtitle);

        header.add(headerText, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // KONTEN UTAMA
        JPanel content = new JPanel(new BorderLayout(15, 20));
        content.setBackground(background);
        content.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        // KARTU RINGKASAN
        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 15));
        cards.setBackground(background);

        totalPenjualan = new JLabel("Rp0");
        totalPembelian = new JLabel("Rp0");
        jumlahTersedia = new JLabel("0 Topi");
        jumlahTerjual = new JLabel("0 Transaksi");

        cards.add(buatKartu(
                "Total Penjualan",
                totalPenjualan,
                new Color(219, 234, 254)
        ));

        cards.add(buatKartu(
                "Total Pembelian",
                totalPembelian,
                new Color(254, 243, 199)
        ));

        cards.add(buatKartu(
                "Stok Tersedia",
                jumlahTersedia,
                new Color(220, 252, 231)
        ));

        cards.add(buatKartu(
                "Topi Terjual",
                jumlahTerjual,
                new Color(243, 232, 255)
        ));

        // TABEL RIWAYAT PENJUALAN
        String[] columns = {
            "Kode Penjualan",
            "Tanggal",
            "Kode Topi",
            "Nama Pembeli",
            "Harga Jual",
            "Metode Pembayaran"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 225, 232)
                )
        );

        JPanel tablePanel = new JPanel(new BorderLayout(0, 12));
        tablePanel.setBackground(background);

        JLabel tableTitle = new JLabel("Riwayat Penjualan");
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableTitle.setForeground(dark);

        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        content.add(cards, BorderLayout.NORTH);
        content.add(tablePanel, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        // FOOTER
        JPanel footer = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 25, 12)
        );
        footer.setBackground(Color.WHITE);

        JButton kembali = new JButton("Kembali ke Dashboard");
        kembali.setFocusPainted(false);
        kembali.setCursor(new Cursor(Cursor.HAND_CURSOR));
        kembali.addActionListener(e -> onKembali.run());

        footer.add(kembali);
        add(footer, BorderLayout.SOUTH);

        // MUAT DATA AWAL
        perbaruiLaporan();
    }

    private JPanel buatKartu(
            String judul,
            JLabel nilai,
            Color warna
    ) {
        JPanel kartu = new JPanel();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setBackground(warna);
        kartu.setBorder(
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        );

        JLabel labelJudul = new JLabel(judul);
        labelJudul.setFont(
                new Font("SansSerif", Font.PLAIN, 12)
        );
        labelJudul.setForeground(new Color(75, 85, 99));

        nilai.setFont(new Font("SansSerif", Font.BOLD, 19));
        nilai.setForeground(dark);

        kartu.add(labelJudul);
        kartu.add(Box.createVerticalStrut(15));
        kartu.add(nilai);

        return kartu;
    }

    // MEMPERBARUI LAPORAN DARI DATASTORE
    public void perbaruiLaporan() {

        // Total pendapatan penjualan
        totalPenjualan.setText(
                formatRupiah.format(DataStore.getTotalPenjualan())
        );

        // Total pengeluaran pembelian
        totalPembelian.setText(
                formatRupiah.format(DataStore.getTotalPembelian())
        );

        // Total stok fisik yang tersedia
        jumlahTersedia.setText(
                DataStore.getTotalStok() + " Topi"
        );

        // Jumlah transaksi penjualan
        jumlahTerjual.setText(
                DataStore.getJumlahTransaksiPenjualan() + " Transaksi"
        );

        // Kosongkan tabel sebelum memuat ulang
        model.setRowCount(0);

        // Tampilkan transaksi terbaru di baris paling atas
        for (int i = DataStore.daftarPenjualan.size() - 1; i >= 0; i--) {

            DataStore.Penjualan p = DataStore.daftarPenjualan.get(i);

            model.addRow(new Object[]{
                p.kode,
                p.tanggal,
                p.kodeTopi,
                p.namaPembeli,
                formatRupiah.format(p.hargaJual),
                p.metode
            });
        }
    }
}