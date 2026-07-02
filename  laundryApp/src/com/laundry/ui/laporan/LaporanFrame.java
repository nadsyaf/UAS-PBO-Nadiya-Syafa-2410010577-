package com.laundry.ui.laporan;

import com.laundry.db.DBConnection;
import com.laundry.util.Formatter;
import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Frame Laporan (fitur OPSIONAL sesuai ketentuan tugas).
 * Menampilkan rekap transaksi laundry beserta total pendapatan,
 * dan dapat dicetak langsung dari JTable.
 */
public class LaporanFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JLabel lTotalPendapatan;
    private JButton bCetak, bTutup;

    public LaporanFrame() {
        setTitle("Laporan Rekap Transaksi Laundry");
        setSize(850, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel header = new JLabel("LAPORAN REKAP TRANSAKSI LAUNDRY", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        main.add(header, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"ID", "Pelanggan", "Layanan", "Tgl Masuk", "Berat (Kg)", "Total Harga", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        main.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        lTotalPendapatan = new JLabel("Total Pendapatan: Rp 0", SwingConstants.RIGHT);
        lTotalPendapatan.setFont(new Font("SansSerif", Font.BOLD, 14));
        lTotalPendapatan.setBorder(new EmptyBorder(5, 5, 5, 15));
        south.add(lTotalPendapatan, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bCetak = new JButton("Cetak Laporan");
        bTutup = new JButton("Tutup");
        btnPanel.add(bCetak);
        btnPanel.add(bTutup);
        south.add(btnPanel, BorderLayout.SOUTH);

        main.add(south, BorderLayout.SOUTH);
        add(main);

        bTutup.addActionListener(e -> dispose());
        bCetak.addActionListener(e -> cetakLaporan());
    }

    private void loadData() {
        model.setRowCount(0);
        double totalPendapatan = 0;

        String sql = "SELECT t.id_transaksi, p.nama_pelanggan, l.nama_layanan, "
                + "t.tanggal_masuk, t.berat_kg, t.total_harga, t.status "
                + "FROM tb_transaksi t "
                + "JOIN tb_pelanggan p ON t.id_pelanggan = p.id_pelanggan "
                + "JOIN tb_layanan l ON t.id_layanan = l.id_layanan "
                + "ORDER BY t.tanggal_masuk DESC";
        Connection con = DBConnection.getConnection();
        if (con == null) return;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double total = rs.getDouble("total_harga");
                totalPendapatan += total;
                model.addRow(new Object[]{
                        rs.getInt("id_transaksi"),
                        rs.getString("nama_pelanggan"),
                        rs.getString("nama_layanan"),
                        rs.getDate("tanggal_masuk"),
                        rs.getDouble("berat_kg"),
                        Formatter.toRupiah(total),
                        rs.getString("status")
                });
            }
            lTotalPendapatan.setText("Total Pendapatan: " + Formatter.toRupiah(totalPendapatan));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data laporan: " + ex.getMessage());
        }
    }

    private void cetakLaporan() {
        try {
            table.print(JTable.PrintMode.FIT_WIDTH);
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak: " + ex.getMessage());
        }
    }
}
