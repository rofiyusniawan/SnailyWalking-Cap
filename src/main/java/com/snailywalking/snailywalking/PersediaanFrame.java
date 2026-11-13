
package com.snailywalking.snailywalking;

import com.snailywalking.snailywalking.model.TopiEntity;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

import org.hibernate.Session;

public class PersediaanFrame extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField cariField;
    private JComboBox<String> statusCombo;

    private JLabel totalLabel;
    private JLabel tersediaLabel;
    private JLabel terjualLabel;

    private final Runnable onKembali;

    private final Color background = new Color(245, 247, 250);
    private final Color blue = new Color(59, 130, 246);

    public PersediaanFrame(Runnable onKembali) {
        this.onKembali = onKembali;

        setLayout(new BorderLayout());
        setBackground(background);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 20));
        mainPanel.setBackground(background);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(28, 28, 28, 28)
        );

        JLabel title = new JLabel("Persediaan Topi");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(35, 39, 47));

        mainPanel.add(title, BorderLayout.NORTH);

        // KARTU RINGKASAN
        JPanel summaryPanel = new JPanel(
                new GridLayout(1, 3, 15, 0)
        );
        summaryPanel.setOpaque(false);

        totalLabel = new JLabel("0");
        tersediaLabel = new JLabel("0");
        terjualLabel = new JLabel("0");

        summaryPanel.add(buatKartu("Total Jenis Topi", totalLabel));
        summaryPanel.add(buatKartu("Jenis Topi Tersedia", tersediaLabel));
        summaryPanel.add(buatKartu("Jenis Topi Stok Habis", terjualLabel));

        // PANEL PENCARIAN
        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 10)
        );
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(
                BorderFactory.createTitledBorder("Cari dan Filter")
        );

        cariField = new JTextField(18);

        statusCombo = new JComboBox<>(new String[]{
            "Semua Status", "Tersedia", "Stok Habis"
        });

        JButton cariButton = new JButton("Cari");
        JButton resetButton = new JButton("Reset");

        filterPanel.add(new JLabel("Cari Topi:"));
        filterPanel.add(cariField);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusCombo);
        filterPanel.add(cariButton);
        filterPanel.add(resetButton);

        // TABEL PERSEDIAAN
        String[] columns = {
            "Kode Topi",
            "Merek",
            "Model",
            "Warna",
            "Kondisi",
            "Harga Modal",
            "Harga Jual",
            "Stok",
            "Status"
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
        table.setAutoCreateRowSorter(false);

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 12)
        );
        table.getTableHeader().setBackground(
                new Color(235, 239, 245)
        );

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 229, 235)
                )
        );

        // TOMBOL KEMBALI
        JButton kembaliButton = new JButton("Kembali");

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );
        bottomPanel.setOpaque(false);
        bottomPanel.add(kembaliButton);

        // TATA LETAK
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        JPanel topPanel = new JPanel(new BorderLayout(0, 15));
        topPanel.setOpaque(false);
        topPanel.add(summaryPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        centerPanel.add(topPanel, BorderLayout.NORTH);
        centerPanel.add(tableScroll, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // EVENT PENCARIAN
        cariButton.addActionListener(e -> filterData());
        cariField.addActionListener(e -> filterData());
        statusCombo.addActionListener(e -> filterData());

        resetButton.addActionListener(e -> {
            cariField.setText("");
            statusCombo.setSelectedIndex(0);
            filterData();
        });

        kembaliButton.addActionListener(e -> this.onKembali.run());

        // MEMUAT DATA DARI DATABASE
        muatDataPersediaan();
    }

    // MEMBUAT KARTU RINGKASAN
    private JPanel buatKartu(String judul, JLabel nilai) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 229, 235)
                ),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));

        JLabel titleLabel = new JLabel(judul);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 110, 125));

        nilai.setFont(new Font("Arial", Font.BOLD, 25));
        nilai.setForeground(blue);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(nilai);

        return card;
    }

    // MEMUAT DATA TOPI DARI MYSQL
    public void muatDataPersediaan() {
        model.setRowCount(0);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<TopiEntity> daftarTopi = session.createQuery(
                    "FROM TopiEntity ORDER BY kode",
                    TopiEntity.class
            ).getResultList();

            for (TopiEntity topi : daftarTopi) {
                String status = topi.getStok() > 0
                        ? "Tersedia"
                        : "Stok Habis";

                model.addRow(new Object[]{
                    topi.getKode(),
                    topi.getMerek(),
                    topi.getModel(),
                    topi.getWarna(),
                    topi.getKondisi(),
                    topi.getHargaModal(),
                    topi.getHargaJual(),
                    topi.getStok(),
                    status
                });
            }

            perbaruiRingkasan(daftarTopi);

            filterData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Gagal memuat data persediaan: " + e.getMessage()
            );
            e.printStackTrace();
        }
    }

    // PENCARIAN DAN FILTER
    private void filterData() {
        if (sorter == null || cariField == null || statusCombo == null) {
            return;
        }

        String kataKunci = cariField.getText().trim();
        String status = statusCombo.getSelectedItem().toString();

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(
                    Entry<? extends DefaultTableModel, ? extends Integer> entry) {

                String kode = entry.getStringValue(0);
                String merek = entry.getStringValue(1);
                String statusTopi = entry.getStringValue(8);

                boolean cocokKata = kataKunci.isEmpty()
                        || kode.toLowerCase().contains(kataKunci.toLowerCase())
                        || merek.toLowerCase().contains(kataKunci.toLowerCase());

                boolean cocokStatus = status.equals("Semua Status")
                        || statusTopi.equalsIgnoreCase(status);

                return cocokKata && cocokStatus;
            }
        });
    }

    // MEMPERBARUI RINGKASAN DARI DATA DATABASE
    private void perbaruiRingkasan(List<TopiEntity> daftarTopi) {
        int total = daftarTopi.size();
        int tersedia = 0;
        int stokHabis = 0;

        for (TopiEntity topi : daftarTopi) {
            if (topi.getStok() > 0) {
                tersedia++;
            } else {
                stokHabis++;
            }
        }

        totalLabel.setText(String.valueOf(total));
        tersediaLabel.setText(String.valueOf(tersedia));
        terjualLabel.setText(String.valueOf(stokHabis));
    }
}
