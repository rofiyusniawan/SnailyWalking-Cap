
package com.snailywalking.snailywalking;

import com.snailywalking.snailywalking.model.PenjualanEntity;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.context.ApplicationContext;

public class DashboardFrame extends JFrame {

    private final ApplicationContext context;

    private final Color dark = new Color(30, 35, 45);
    private final Color background = new Color(245, 247, 250);
    private final Color activeColor = new Color(59, 130, 246);
    private final Color sidebarColor = new Color(30, 35, 45);

    private JPanel contentCards;
    private CardLayout cardLayout;

    private final Map<String, JButton> menuButtons = new LinkedHashMap<>();

    private JLabel labelStok;
    private JLabel labelTerjual;
    private JLabel labelTotalPenjualan;
    private DefaultTableModel modelRiwayat;

    private final NumberFormat formatRupiah =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public DashboardFrame(ApplicationContext context) {
        this.context = context;

        setTitle("Dashboard - Snaily Walking Cap");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        buatSidebar();
        buatAreaKonten();

        tampilkanHalaman("Dashboard");
    }

    private void buatSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(sidebarColor);

        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setBackground(sidebarColor);
        brandPanel.setBorder(
                BorderFactory.createEmptyBorder(25, 20, 25, 20)
        );

        JLabel namaAplikasi = new JLabel("SNAILY");
        namaAplikasi.setForeground(Color.WHITE);
        namaAplikasi.setFont(new Font("SansSerif", Font.BOLD, 25));

        JLabel subNama = new JLabel("WALKING CAP");
        subNama.setForeground(new Color(156, 163, 175));
        subNama.setFont(new Font("SansSerif", Font.BOLD, 12));

        brandPanel.add(namaAplikasi);
        brandPanel.add(Box.createVerticalStrut(4));
        brandPanel.add(subNama);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(sidebarColor);
        menuPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        );

        String[] menus = {
            "Dashboard",
            "Data Topi",
            "Data Supplier",
            "Pembelian",
            "Persediaan",
            "Penjualan",
            "Laporan"
        };

        for (String menu : menus) {
            JButton button = buatTombolMenu(menu);
            menuButtons.put(menu, button);

            menuPanel.add(button);
            menuPanel.add(Box.createVerticalStrut(7));
        }

        sidebar.add(menuPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(sidebarColor);
        bottomPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 12, 20, 12)
        );

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));

        JButton logoutButton = buatTombolMenu("Logout");

        logoutButton.addActionListener(e -> {
            int pilihan = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah kamu yakin ingin keluar?",
                    "Konfirmasi Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (pilihan == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame(context).setVisible(true);
            }
        });

        bottomPanel.add(separator);
        bottomPanel.add(Box.createVerticalStrut(15));
        bottomPanel.add(logoutButton);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
    }

    private JButton buatTombolMenu(String nama) {
        JButton button = new JButton(nama);

        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        button.setPreferredSize(new Dimension(190, 43));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(new Color(209, 213, 219));
        button.setBackground(sidebarColor);
        button.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 10)
        );
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addActionListener(e -> bukaHalaman(nama));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!nama.equals(getHalamanAktif())) {
                    button.setBackground(new Color(55, 65, 81));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!nama.equals(getHalamanAktif())) {
                    button.setBackground(sidebarColor);
                }
            }
        });

        return button;
    }

    private String getHalamanAktif() {
        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            if (entry.getValue().getBackground().equals(activeColor)) {
                return entry.getKey();
            }
        }

        return "";
    }

    private void buatAreaKonten() {
        cardLayout = new CardLayout();
        contentCards = new JPanel(cardLayout);
        contentCards.setBackground(background);

        contentCards.add(buatHalamanDashboard(), "Dashboard");

        contentCards.add(
                new DataTopiFrame(() -> tampilkanHalaman("Dashboard")),
                "Data Topi"
        );

        contentCards.add(
                new DataSupplierFrame(() -> tampilkanHalaman("Dashboard")),
                "Data Supplier"
        );

        contentCards.add(
                new PembelianFrame(() -> tampilkanHalaman("Dashboard")),
                "Pembelian"
        );

        contentCards.add(
                new PersediaanFrame(() -> tampilkanHalaman("Dashboard")),
                "Persediaan"
        );

        contentCards.add(
                new PenjualanFrame(() -> tampilkanHalaman("Dashboard")),
                "Penjualan"
        );

        contentCards.add(
                new LaporanFrame(() -> tampilkanHalaman("Dashboard")),
                "Laporan"
        );

        add(contentCards, BorderLayout.CENTER);
    }

    private JPanel buatHalamanDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(background);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(
                BorderFactory.createEmptyBorder(22, 25, 22, 25)
        );

        JLabel judul = new JLabel("Dashboard");
        judul.setFont(new Font("SansSerif", Font.BOLD, 25));
        judul.setForeground(dark);

        JLabel deskripsi = new JLabel(
                "Ringkasan aktivitas bisnis Snaily Walking Cap"
        );
        deskripsi.setFont(new Font("SansSerif", Font.PLAIN, 13));
        deskripsi.setForeground(new Color(107, 114, 128));

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setBackground(Color.WHITE);
        headerText.add(judul);
        headerText.add(Box.createVerticalStrut(5));
        headerText.add(deskripsi);

        header.add(headerText, BorderLayout.WEST);
        panel.add(header, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(15, 20));
        content.setBackground(background);
        content.setBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        );

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 15));
        cards.setBackground(background);

        labelStok = new JLabel("0 Topi");
        labelTerjual = new JLabel("0 Transaksi");
        labelTotalPenjualan = new JLabel("Rp0");

        cards.add(
                buatKartuDashboard(
                        "Stok Tersedia",
                        labelStok,
                        new Color(220, 252, 231)
                )
        );

        cards.add(
                buatKartuDashboard(
                        "Topi Terjual",
                        labelTerjual,
                        new Color(219, 234, 254)
                )
        );

        cards.add(
                buatKartuDashboard(
                        "Total Penjualan",
                        labelTotalPenjualan,
                        new Color(243, 232, 255)
                )
        );

        JPanel riwayatPanel = new JPanel(new BorderLayout(0, 12));
        riwayatPanel.setBackground(background);

        JLabel riwayatTitle = new JLabel("Riwayat Penjualan Terbaru");
        riwayatTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        riwayatTitle.setForeground(dark);

        String[] kolom = {
            "Kode Penjualan",
            "Tanggal",
            "Kode Topi",
            "Nama Pembeli",
            "Harga Jual",
            "Metode"
        };

        modelRiwayat = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(modelRiwayat);
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232))
        );

        riwayatPanel.add(riwayatTitle, BorderLayout.NORTH);
        riwayatPanel.add(scrollPane, BorderLayout.CENTER);

        content.add(cards, BorderLayout.NORTH);
        content.add(riwayatPanel, BorderLayout.CENTER);

        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buatKartuDashboard(
            String judul,
            JLabel nilai,
            Color warna
    ) {
        JPanel kartu = new JPanel();
        kartu.setLayout(new BoxLayout(kartu, BoxLayout.Y_AXIS));
        kartu.setBackground(warna);
        kartu.setBorder(
                BorderFactory.createEmptyBorder(22, 20, 22, 20)
        );

        JLabel labelJudul = new JLabel(judul);
        labelJudul.setFont(new Font("SansSerif", Font.PLAIN, 13));
        labelJudul.setForeground(new Color(75, 85, 99));

        nilai.setFont(new Font("SansSerif", Font.BOLD, 23));
        nilai.setForeground(dark);

        kartu.add(labelJudul);
        kartu.add(Box.createVerticalStrut(12));
        kartu.add(nilai);

        return kartu;
    }

    // Memperbarui data Dashboard berdasarkan database MySQL
    private void perbaruiDashboard() {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Number totalStok = (Number) session.createQuery(
                    "SELECT COALESCE(SUM(t.stok), 0) FROM TopiEntity t"
            ).uniqueResult();

            labelStok.setText(totalStok.intValue() + " Topi");

            Long jumlahTerjual = session.createQuery(
                    "SELECT COUNT(p) FROM PenjualanEntity p",
                    Long.class
            ).uniqueResult();

            labelTerjual.setText(jumlahTerjual + " Transaksi");

            Number totalPenjualan = (Number) session.createQuery(
                    "SELECT COALESCE(SUM(p.hargaJual), 0) FROM PenjualanEntity p"
            ).uniqueResult();

            labelTotalPenjualan.setText(
                    formatRupiah.format(totalPenjualan.doubleValue())
            );

            modelRiwayat.setRowCount(0);

            List<PenjualanEntity> daftarPenjualan = session.createQuery(
                    "FROM PenjualanEntity p ORDER BY p.tanggal DESC, p.kode DESC",
                    PenjualanEntity.class
            ).list();

            for (PenjualanEntity p : daftarPenjualan) {
                modelRiwayat.addRow(new Object[]{
                    p.getKode(),
                    p.getTanggal(),
                    p.getKodeTopi(),
                    p.getNamaPembeli(),
                    formatRupiah.format(p.getHargaJual()),
                    p.getMetode()
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal mengambil data Dashboard dari database:\n"
                    + e.getMessage(),
                    "Kesalahan Database",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void bukaHalaman(String nama) {
        if (nama.equals("Logout")) {
            return;
        }

        switch (nama) {
            case "Dashboard":
            case "Data Topi":
            case "Data Supplier":
            case "Pembelian":
            case "Persediaan":
            case "Penjualan":
            case "Laporan":
                tampilkanHalaman(nama);
                break;

            default:
                JOptionPane.showMessageDialog(
                        this,
                        "Halaman tidak ditemukan.",
                        "Informasi",
                        JOptionPane.INFORMATION_MESSAGE
                );
                break;
        }
    }

    private void tampilkanHalaman(String nama) {
        if (nama.equals("Dashboard")
                && labelTotalPenjualan != null) {
            perbaruiDashboard();
        }

        cardLayout.show(contentCards, nama);

        for (Map.Entry<String, JButton> entry : menuButtons.entrySet()) {
            JButton button = entry.getValue();

            if (entry.getKey().equals(nama)) {
                button.setBackground(activeColor);
                button.setForeground(Color.WHITE);
                button.setFont(new Font("SansSerif", Font.BOLD, 14));
            } else {
                button.setBackground(sidebarColor);
                button.setForeground(new Color(209, 213, 219));
                button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            }
        }
    }
}
